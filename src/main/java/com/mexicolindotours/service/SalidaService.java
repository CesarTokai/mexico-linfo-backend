package com.mexicolindotours.service;

import com.mexicolindotours.model.Camioneta;
import com.mexicolindotours.model.Chofer;
import com.mexicolindotours.model.Paquete;
import com.mexicolindotours.model.Salida;
import com.mexicolindotours.repository.CamionetaRepository;
import com.mexicolindotours.repository.ChoferRepository;
import com.mexicolindotours.repository.PaqueteRepository;
import com.mexicolindotours.repository.ReservaRepository;
import com.mexicolindotours.repository.SalidaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class SalidaService {

	@Autowired
	private SalidaRepository salidaRepository;

	@Autowired
	private PaqueteRepository paqueteRepository;

	@Autowired
	private ReservaRepository reservaRepository;

	@Autowired
	private CamionetaRepository camionetaRepository;

	@Autowired
	private DisponibilidadUnidadService disponibilidadUnidadService;

	@Autowired
	private ChoferRepository choferRepository;

	@Autowired
	private DisponibilidadChoferService disponibilidadChoferService;

	public Salida crear(Long paqueteId, LocalDate fechaSalida, LocalDate fechaRegreso,
						Integer cupoTotal, BigDecimal precioPorPersona, Long camionetaId, Long choferId) {

		Paquete paquete = paqueteRepository.findById(paqueteId)
				.orElseThrow(() -> new IllegalArgumentException("Paquete no encontrado"));

		if (fechaSalida == null || fechaRegreso == null) {
			throw new IllegalArgumentException("Las fechas de salida y regreso son obligatorias");
		}
		if (fechaRegreso.isBefore(fechaSalida)) {
			throw new IllegalArgumentException("La fecha de regreso no puede ser anterior a la de salida");
		}
		if (cupoTotal == null || cupoTotal < 1) {
			throw new IllegalArgumentException("El cupo debe ser al menos 1");
		}

		Salida s = new Salida(paquete, fechaSalida, fechaRegreso, cupoTotal);
		s.setPrecioPorPersona(precioPorPersona);

		if (camionetaId != null) {
			s.setCamioneta(resolverCamionetaLibre(camionetaId, fechaSalida, fechaRegreso, null, cupoTotal));
		}
		if (choferId != null) {
			s.setChofer(resolverChoferLibre(choferId, fechaSalida, fechaRegreso, null));
		}

		return salidaRepository.save(s);
	}

	public Salida actualizar(Long id, LocalDate fechaSalida, LocalDate fechaRegreso, Integer cupoTotal,
							 BigDecimal precioPorPersona, String estado, Long camionetaId, Long choferId) {

		Salida s = obtenerPorId(id);

		if (cupoTotal != null) {
			int ocupados = reservaRepository.asientosOcupados(id);
			if (cupoTotal < ocupados) {
				throw new IllegalArgumentException(
						"No se puede bajar el cupo a " + cupoTotal + ": ya hay " + ocupados + " asiento(s) reservados");
			}
			s.setCupoTotal(cupoTotal);
		}

		LocalDate fi = fechaSalida != null ? fechaSalida : s.getFechaSalida();
		LocalDate ff = fechaRegreso != null ? fechaRegreso : s.getFechaRegreso();
		if (ff.isBefore(fi)) {
			throw new IllegalArgumentException("La fecha de regreso no puede ser anterior a la de salida");
		}
		s.setFechaSalida(fi);
		s.setFechaRegreso(ff);

		if (precioPorPersona != null) s.setPrecioPorPersona(precioPorPersona);

		// Al mover fechas con unidad ya asignada hay que revalidar el calendario.
		Long unidad = camionetaId != null ? camionetaId
				: (s.getCamioneta() != null ? s.getCamioneta().getId() : null);
		if (unidad != null) {
			s.setCamioneta(resolverCamionetaLibre(unidad, fi, ff, id, s.getCupoTotal()));
		}

		Long chofer = choferId != null ? choferId
				: (s.getChofer() != null ? s.getChofer().getId() : null);
		if (chofer != null) {
			s.setChofer(resolverChoferLibre(chofer, fi, ff, id));
		}

		if (estado != null && !estado.isBlank()) {
			try {
				s.setEstado(Salida.Estado.valueOf(estado));
			} catch (IllegalArgumentException e) {
				throw new IllegalArgumentException("Estado inválido: " + estado + " (programada, cerrada o cancelada)");
			}
		}

		s.setUpdatedAt(LocalDateTime.now());
		return salidaRepository.save(s);
	}

	public Salida obtenerPorId(Long id) {
		return salidaRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Salida no encontrada"));
	}

	public List<Salida> obtenerPorPaquete(Long paqueteId) {
		return salidaRepository.findByPaqueteIdOrderByFechaSalidaAsc(paqueteId);
	}

	/**
	 * Salidas visibles para el publico: futuras y no canceladas. Una salida
	 * agotada (cerrada) se sigue mostrando con 0 disponibles, no desaparece
	 * — importa sobre todo cuando el cierre lo causaron reservas PENDIENTES
	 * de pago que aun pueden caducar y liberar el cupo.
	 */
	public List<Salida> proximasDelPaquete(Long paqueteId) {
		return salidaRepository.findByPaqueteIdAndEstadoNotAndFechaSalidaGreaterThanEqualOrderByFechaSalidaAsc(
				paqueteId, Salida.Estado.cancelada, LocalDate.now());
	}

	public List<Salida> proximasSalidas() {
		return salidaRepository.proximasSalidas(LocalDate.now());
	}

	public int asientosDisponibles(Salida salida) {
		return salida.getCupoTotal() - reservaRepository.asientosOcupados(salida.getId());
	}

	/**
	 * Se llama tras crear, confirmar o cancelar una reserva. Si el cupo se
	 * llena, la salida pasa a `cerrada` sola (asi el personal ve de un
	 * vistazo que ya no admite mas gente sin sumar a mano). Si se libera
	 * cupo (una cancelacion) y segia disponible, vuelve a `programada`.
	 * Nunca toca una salida ya `cancelada`.
	 */
	public void actualizarCierrePorCupo(Long salidaId) {
		Salida salida = obtenerPorId(salidaId);
		if (salida.getEstado() == Salida.Estado.cancelada) return;

		int disponibles = asientosDisponibles(salida);

		if (disponibles <= 0 && salida.getEstado() == Salida.Estado.programada) {
			salida.setEstado(Salida.Estado.cerrada);
			salida.setUpdatedAt(LocalDateTime.now());
			salidaRepository.save(salida);
		} else if (disponibles > 0 && salida.getEstado() == Salida.Estado.cerrada) {
			salida.setEstado(Salida.Estado.programada);
			salida.setUpdatedAt(LocalDateTime.now());
			salidaRepository.save(salida);
		}
	}

	/** Valida que la unidad exista, no este en taller, alcance el cupo y este libre. */
	private Camioneta resolverCamionetaLibre(Long camionetaId, LocalDate desde, LocalDate hasta,
											 Long salidaIdExcluir, Integer cupoTotal) {
		Camioneta camioneta = camionetaRepository.findById(camionetaId)
				.orElseThrow(() -> new IllegalArgumentException("Camioneta no encontrada"));

		if (camioneta.getEstado() == Camioneta.Estado.en_taller) {
			throw new IllegalArgumentException("Camioneta en taller, no se puede asignar a una salida");
		}
		if (camioneta.getEstado() == Camioneta.Estado.baja) {
			throw new IllegalArgumentException("Camioneta dada de baja");
		}
		if (cupoTotal != null && camioneta.getCapacidad() != null && cupoTotal > camioneta.getCapacidad()) {
			throw new IllegalArgumentException(
					"El cupo (" + cupoTotal + ") supera la capacidad de la unidad (" + camioneta.getCapacidad() + ")");
		}

		disponibilidadUnidadService.verificarLibre(camionetaId, desde, hasta, null, salidaIdExcluir);

		return camioneta;
	}

	private Chofer resolverChoferLibre(Long choferId, LocalDate desde, LocalDate hasta, Long salidaIdExcluir) {
		Chofer chofer = choferRepository.findById(choferId)
				.orElseThrow(() -> new IllegalArgumentException("Chofer no encontrado"));

		if (Boolean.FALSE.equals(chofer.getActivo())) {
			throw new IllegalArgumentException("Chofer inactivo, no se puede asignar");
		}

		disponibilidadChoferService.verificarLibre(choferId, desde, hasta, null, salidaIdExcluir);

		return chofer;
	}

	public Optional<Salida> obtenerOpcional(Long id) {
		return salidaRepository.findById(id);
	}

}
