package com.mexicolindotours.service;

import com.mexicolindotours.model.Reserva;
import com.mexicolindotours.model.Salida;
import com.mexicolindotours.model.UsuarioPublico;
import com.mexicolindotours.repository.ReservaRepository;
import com.mexicolindotours.repository.SalidaRepository;
import com.mexicolindotours.repository.UsuarioPublicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
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

		Reserva reserva = new Reserva(salida, usuario, numAsientos, monto);
		reserva.setNotas(notas);

		return reservaRepository.save(reserva);
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
		if (reserva.getEstado() == Reserva.Estado.confirmada) {
			throw new IllegalArgumentException("La reserva ya está confirmada");
		}
		if (comprobanteUrl == null || comprobanteUrl.isBlank()) {
			throw new IllegalArgumentException("Falta el comprobante");
		}

		reserva.setComprobanteUrl(comprobanteUrl);
		reserva.setReferenciaTransferencia(referencia);
		reserva.setEstado(Reserva.Estado.en_revision);
		reserva.setUpdatedAt(LocalDateTime.now());

		return reservaRepository.save(reserva);
	}

	/** El personal verifica la transferencia y confirma. */
	public Reserva confirmar(Long reservaId) {
		Reserva reserva = obtenerPorId(reservaId);

		if (reserva.getEstado() == Reserva.Estado.cancelada) {
			throw new IllegalArgumentException("No se puede confirmar una reserva cancelada");
		}
		if (reserva.getComprobanteUrl() == null) {
			throw new IllegalArgumentException("No hay comprobante que verificar");
		}

		reserva.setEstado(Reserva.Estado.confirmada);
		reserva.setConfirmadaAt(LocalDateTime.now());
		reserva.setUpdatedAt(LocalDateTime.now());

		return reservaRepository.save(reserva);
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

		return reservaRepository.save(reserva);
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
