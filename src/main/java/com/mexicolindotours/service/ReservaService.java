package com.mexicolindotours.service;

import com.mexicolindotours.model.Reserva;
import com.mexicolindotours.model.Salida;
import com.mexicolindotours.model.UsuarioPublico;
import com.mexicolindotours.repository.ReservaRepository;
import com.mexicolindotours.repository.SalidaRepository;
import com.mexicolindotours.repository.UsuarioPublicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReservaService {

	@Autowired
	private ReservaRepository reservaRepository;

	@Autowired
	private SalidaRepository salidaRepository;

	@Autowired
	private UsuarioPublicoRepository usuarioPublicoRepository;

	/** Horas que una reserva puede retener asientos sin comprobante. */
	@Value("${app.reservas.horas-para-pagar:48}")
	private long horasParaPagar;

	/** Porcentaje que hay que cubrir para asegurar los asientos. */
	@Value("${app.reservas.porcentaje-anticipo:50}")
	private int porcentajeAnticipo;

	@Autowired
	private NotificacionService notificacionService;

	@Autowired
	private SalidaService salidaService;

	@Autowired
	private UsuarioPublicoService usuarioPublicoService;

	/**
	 * Aparta asientos. Va en una transaccion con bloqueo pesimista sobre la
	 * salida: sin eso, dos personas apartando a la vez podrian llevarse ambas
	 * el ultimo lugar y dejar la camioneta sobrevendida.
	 */
	@Transactional
	public Reserva apartar(Long salidaId, Long usuarioPublicoId, Integer numAsientos, String notas) {

		if (numAsientos == null || numAsientos < 1) {
			throw new IllegalArgumentException("Debes apartar al menos 1 asiento");
		}

		UsuarioPublico usuario = usuarioPublicoRepository.findById(usuarioPublicoId)
				.orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

		Salida salida = salidaRepository.findByIdBloqueando(salidaId)
				.orElseThrow(() -> new IllegalArgumentException("Salida no encontrada"));

		if (salida.getEstado() != Salida.Estado.programada) {
			throw new IllegalArgumentException("Esta salida ya no admite reservas (" + salida.getEstado() + ")");
		}
		if (salida.getFechaSalida().isBefore(LocalDate.now())) {
			throw new IllegalArgumentException("Esta salida ya pasó");
		}

		int ocupados = reservaRepository.asientosOcupados(salidaId);
		int disponibles = salida.getCupoTotal() - ocupados;

		if (numAsientos > disponibles) {
			throw new IllegalArgumentException(
					"Solo quedan " + disponibles + " asiento(s) disponibles en esta salida");
		}

		BigDecimal monto = salida.precioEfectivo().multiply(BigDecimal.valueOf(numAsientos));
		BigDecimal anticipo = monto
				.multiply(BigDecimal.valueOf(porcentajeAnticipo))
				.divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

		Reserva reserva = new Reserva(salida, usuario, numAsientos, monto, anticipo);
		reserva.setNotas(notas);

		Reserva guardada = reservaRepository.save(reserva);
		salidaService.actualizarCierrePorCupo(salidaId);
		notificacionService.reservaCreada(guardada);

		return guardada;
	}

	/** El cliente registra su comprobante de transferencia. */
	public Reserva registrarComprobante(Long reservaId, Long usuarioPublicoId, String comprobanteUrl, String referencia) {
		Reserva reserva = obtenerPorId(reservaId);

		if (!reserva.getUsuarioPublico().getId().equals(usuarioPublicoId)) {
			throw new SeguridadException("Esta reserva no te pertenece");
		}
		if (reserva.getEstado() == Reserva.Estado.cancelada) {
			throw new IllegalArgumentException("La reserva está cancelada");
		}
		if (reserva.liquidada()) {
			throw new IllegalArgumentException("La reserva ya está liquidada");
		}
		if (comprobanteUrl == null || comprobanteUrl.isBlank()) {
			throw new IllegalArgumentException("Falta el comprobante");
		}

		reserva.setComprobanteUrl(comprobanteUrl);
		reserva.setReferenciaTransferencia(referencia);
		// Si ya estaba confirmada, es la liquidacion: no se degrada el estado.
		if (reserva.getEstado() != Reserva.Estado.confirmada) {
			reserva.setEstado(Reserva.Estado.en_revision);
		}
		reserva.setUpdatedAt(LocalDateTime.now());

		Reserva guardada = reservaRepository.save(reserva);
		notificacionService.comprobanteRecibido(guardada);

		return guardada;
	}

	/**
	 * El personal verifica una transferencia y registra el dinero recibido.
	 * Si no se indica monto se asume el anticipo pendiente. Los asientos
	 * quedan asegurados en cuanto lo pagado alcanza el anticipo; el resto
	 * puede liquidarse despues con otra transferencia.
	 */
	public Reserva confirmar(Long reservaId, BigDecimal montoRecibido) {
		Reserva reserva = obtenerPorId(reservaId);

		if (reserva.getEstado() == Reserva.Estado.cancelada) {
			throw new IllegalArgumentException("No se puede confirmar una reserva cancelada");
		}
		if (reserva.getComprobanteUrl() == null) {
			throw new IllegalArgumentException("No hay comprobante que verificar");
		}

		BigDecimal monto = montoRecibido;
		if (monto == null) {
			// Por omision, lo que falte para cubrir el anticipo.
			monto = reserva.getMontoAnticipo().subtract(reserva.getMontoPagado());
			if (monto.compareTo(BigDecimal.ZERO) <= 0) {
				monto = reserva.saldoPendiente();
			}
		}
		if (monto.compareTo(BigDecimal.ZERO) <= 0) {
			throw new IllegalArgumentException("El monto verificado debe ser mayor que cero");
		}
		if (monto.compareTo(reserva.saldoPendiente()) > 0) {
			throw new IllegalArgumentException(
					"El monto (" + monto + ") supera el saldo pendiente (" + reserva.saldoPendiente() + ")");
		}

		// Se valida ANTES de tocar la entidad, para no dejarla a medias.
		BigDecimal nuevoPagado = reserva.getMontoPagado().add(monto);
		if (nuevoPagado.compareTo(reserva.getMontoAnticipo()) < 0) {
			throw new IllegalArgumentException(
					"Con " + nuevoPagado + " no se cubre el anticipo de " + reserva.getMontoAnticipo());
		}

		reserva.setMontoPagado(nuevoPagado);

		boolean primeraVez = reserva.getEstado() != Reserva.Estado.confirmada;
		reserva.setEstado(Reserva.Estado.confirmada);
		if (primeraVez) {
			reserva.setConfirmadaAt(LocalDateTime.now());
		}
		reserva.setUpdatedAt(LocalDateTime.now());

		Reserva guardada = reservaRepository.save(reserva);
		if (primeraVez) {
			notificacionService.reservaConfirmada(guardada);
		}

		return guardada;
	}

	/** Cancelar libera los asientos de inmediato. */
	public Reserva cancelar(Long reservaId, Long usuarioPublicoId) {
		Reserva reserva = obtenerPorId(reservaId);

		// usuarioPublicoId null = cancelacion desde el panel interno
		if (usuarioPublicoId != null && !reserva.getUsuarioPublico().getId().equals(usuarioPublicoId)) {
			throw new SeguridadException("Esta reserva no te pertenece");
		}

		reserva.setEstado(Reserva.Estado.cancelada);
		reserva.setUpdatedAt(LocalDateTime.now());

		Reserva guardada = reservaRepository.save(reserva);
		salidaService.actualizarCierrePorCupo(reserva.getSalida().getId());

		return guardada;
	}

	/**
	 * Libera los asientos de las reservas que nunca subieron comprobante.
	 * Sin esto, alguien puede apartar la camioneta entera y no pagar nunca,
	 * dejando la salida muerta: los asientos jamas volverian al inventario.
	 */
	@Scheduled(fixedDelayString = "${app.reservas.intervalo-limpieza-ms:3600000}")
	public int caducarPendientes() {
		LocalDateTime limite = LocalDateTime.now().minusHours(horasParaPagar);
		List<Reserva> vencidas = reservaRepository.pendientesVencidas(limite);

		for (Reserva r : vencidas) {
			r.setEstado(Reserva.Estado.cancelada);
			r.setNotas(agregarNota(r.getNotas(), "Cancelada automáticamente: sin comprobante tras "
					+ horasParaPagar + " h"));
			r.setUpdatedAt(LocalDateTime.now());
			reservaRepository.save(r);
			salidaService.actualizarCierrePorCupo(r.getSalida().getId());
			notificacionService.reservaCaducada(r);
		}

		return vencidas.size();
	}

	private String agregarNota(String actuales, String nueva) {
		if (actuales == null || actuales.isBlank()) return nueva;
		String combinada = actuales + " | " + nueva;
		return combinada.length() > 500 ? combinada.substring(0, 500) : combinada;
	}

	/**
	 * Reserva que registra el PERSONAL en nombre de un cliente que no usa la
	 * app: familias que solo manejan WhatsApp, o que reservan la camioneta
	 * completa para un evento y arreglan todo por telefono. Se identifica al
	 * cliente por telefono (se crea la cuenta si no existia) y se reutiliza
	 * el mismo apartar()/confirmar() que usa el flujo publico, para no
	 * duplicar la logica de cupo y anticipo que ya esta probada.
	 */
	/**
	 * @Transactional aqui es imprescindible, no decorativo: crearManual
	 * llama a apartar() y confirmar() DENTRO de la misma clase
	 * (auto-invocacion). El proxy de Spring que aplica @Transactional a esos
	 * metodos no se activa en llamadas internas, asi que sin esta anotacion
	 * en crearManual el bloqueo pesimista de la salida se ejecuta sin
	 * transaccion y falla.
	 */
	@Transactional
	public Reserva crearManual(Long salidaId, String nombreCliente, String telefonoCliente, String correoCliente,
							   Integer numAsientos, String notas, boolean marcarConfirmada, BigDecimal montoRecibido) {

		UsuarioPublico cliente = usuarioPublicoService.obtenerOCrearPorTelefono(nombreCliente, telefonoCliente, correoCliente);

		String notaCompleta = "Reservado por el personal" + (notas != null && !notas.isBlank() ? " — " + notas : "");
		Reserva reserva = apartar(salidaId, cliente.getId(), numAsientos, notaCompleta);

		if (marcarConfirmada) {
			// El "comprobante" aqui es la palabra del personal, no un archivo:
			// confirmar() exige que exista comprobanteUrl para no verificar
			// pagos a ciegas, asi que se deja un marcador explicito.
			reserva.setComprobanteUrl("registro-manual-del-personal");
			reservaRepository.save(reserva);
			reserva = confirmar(reserva.getId(), montoRecibido);
		}

		return reserva;
	}

	public Reserva obtenerPorId(Long id) {
		return reservaRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Reserva no encontrada"));
	}

	public List<Reserva> misReservas(Long usuarioPublicoId) {
		return reservaRepository.findByUsuarioPublicoIdOrderByIdDesc(usuarioPublicoId);
	}

	public List<Reserva> todas() {
		return reservaRepository.findAll();
	}

	public List<Reserva> porEstado(Reserva.Estado estado) {
		return reservaRepository.findByEstadoOrderByIdDesc(estado);
	}

	public List<Reserva> porSalida(Long salidaId) {
		return reservaRepository.findBySalidaId(salidaId);
	}

	/** Se lanza cuando alguien toca una reserva ajena: el controller la mapea a 403. */
	public static class SeguridadException extends RuntimeException {
		public SeguridadException(String mensaje) {
			super(mensaje);
		}
	}

}
