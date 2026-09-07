package com.mexicolindotours.service;

import com.mexicolindotours.model.Camioneta;
import com.mexicolindotours.model.Paquete;
import com.mexicolindotours.model.Salida;
import com.mexicolindotours.repository.CamionetaRepository;
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

	public Salida crear(Long paqueteId, LocalDate fechaSalida, LocalDate fechaRegreso,
						Integer cupoTotal, BigDecimal precioPorPersona, Long camionetaId) {

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

		return salidaRepository.save(s);
	}

	public Salida actualizar(Long id, LocalDate fechaSalida, LocalDate fechaRegreso, Integer cupoTotal,
							 BigDecimal precioPorPersona, String estado, Long camionetaId) {

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

	/** Solo las salidas que el publico puede apartar: programadas y futuras. */
	public List<Salida> proximasDelPaquete(Long paqueteId) {
		return salidaRepository.findByPaqueteIdAndEstadoAndFechaSalidaGreaterThanEqualOrderByFechaSalidaAsc(
				paqueteId, Salida.Estado.programada, LocalDate.now());
	}

	public List<Salida> proximasSalidas() {
		return salidaRepository.proximasSalidas(LocalDate.now());
	}

	public int asientosDisponibles(Salida salida) {
		return salida.getCupoTotal() - reservaRepository.asientosOcupados(salida.getId());
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

	public Optional<Salida> obtenerOpcional(Long id) {
		return salidaRepository.findById(id);
	}

}
