package com.mexicolindotours.service;

import com.mexicolindotours.model.Paquete;
import com.mexicolindotours.model.Salida;
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

	public Salida crear(Long paqueteId, LocalDate fechaSalida, LocalDate fechaRegreso,
						Integer cupoTotal, BigDecimal precioPorPersona) {

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

		return salidaRepository.save(s);
	}

	public Salida actualizar(Long id, LocalDate fechaSalida, LocalDate fechaRegreso, Integer cupoTotal,
							 BigDecimal precioPorPersona, String estado) {

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

	public Optional<Salida> obtenerOpcional(Long id) {
		return salidaRepository.findById(id);
	}

}
