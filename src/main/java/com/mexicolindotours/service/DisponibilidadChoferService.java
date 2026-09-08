package com.mexicolindotours.service;

import com.mexicolindotours.model.Chofer;
import com.mexicolindotours.model.DisponibilidadChofer;
import com.mexicolindotours.model.Salida;
import com.mexicolindotours.model.Viaje;
import com.mexicolindotours.repository.ChoferRepository;
import com.mexicolindotours.repository.DisponibilidadChoferRepository;
import com.mexicolindotours.repository.SalidaRepository;
import com.mexicolindotours.repository.ViajeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class DisponibilidadChoferService {

	@Autowired
	private DisponibilidadChoferRepository disponibilidadRepository;

	@Autowired
	private ChoferRepository choferRepository;

	@Autowired
	private ViajeRepository viajeRepository;

	@Autowired
	private SalidaRepository salidaRepository;

	/**
	 * Un chofer no puede estar en dos viajes/salidas que se traslapan en
	 * fecha. Es distinto de la tabla `disponibilidad_chofer`: esa es un
	 * registro OPCIONAL de "este dia si puede" que el dueno confirmo que NO
	 * bloquea nada por si sola (regla C10: "si no hay registro, se puede
	 * asignar de todas formas"). Aqui se valida el choque real contra
	 * asignaciones ya hechas.
	 */
	public void verificarLibre(Long choferId, LocalDate desde, LocalDate hasta,
							   Long viajeIdExcluir, Long salidaIdExcluir) {

		if (choferId == null || desde == null || hasta == null) return;

		for (Viaje v : viajeRepository.findByChoferId(choferId)) {
			if (viajeIdExcluir != null && v.getId().equals(viajeIdExcluir)) continue;
			if (v.getEstado() == Viaje.Estado.cancelado) continue;

			if (DisponibilidadUnidadService.hayConflicto(desde, hasta, v.getFechaInicio(), v.getFechaFin())) {
				throw new IllegalArgumentException(
						"Chofer ocupado del " + v.getFechaInicio() + " al " + v.getFechaFin()
								+ " en el viaje #" + v.getId());
			}
		}

		for (Salida s : salidaRepository.findByChoferIdAndEstadoNot(choferId, Salida.Estado.cancelada)) {
			if (salidaIdExcluir != null && s.getId().equals(salidaIdExcluir)) continue;

			if (DisponibilidadUnidadService.hayConflicto(desde, hasta, s.getFechaSalida(), s.getFechaRegreso())) {
				throw new IllegalArgumentException(
						"Chofer ocupado del " + s.getFechaSalida() + " al " + s.getFechaRegreso()
								+ " en la salida pública #" + s.getId() + " (" + s.getPaquete().getTitulo() + ")");
			}
		}
	}

	public DisponibilidadChofer crearOActualizar(Long choferId, LocalDate fecha, Boolean disponible, String notas) {
		Chofer chofer = choferRepository.findById(choferId)
				.orElseThrow(() -> new IllegalArgumentException("Chofer no encontrado"));

		Optional<DisponibilidadChofer> existente = disponibilidadRepository.findByChoferIdAndFecha(choferId, fecha);

		DisponibilidadChofer disp;
		if (existente.isPresent()) {
			disp = existente.get();
			if (disponible != null) disp.setDisponible(disponible);
			if (notas != null) disp.setNotas(notas);
		} else {
			disp = new DisponibilidadChofer(chofer, fecha, disponible != null ? disponible : true);
			if (notas != null) disp.setNotas(notas);
		}

		return disponibilidadRepository.save(disp);
	}

	public List<DisponibilidadChofer> obtenerPorChofer(Long choferId) {
		return disponibilidadRepository.findByChoferId(choferId);
	}

	public Optional<DisponibilidadChofer> obtenerPorChoferYFecha(Long choferId, LocalDate fecha) {
		return disponibilidadRepository.findByChoferIdAndFecha(choferId, fecha);
	}

	public List<DisponibilidadChofer> obtenerTodas() {
		return disponibilidadRepository.findAll();
	}

	public void eliminar(Long id) {
		disponibilidadRepository.deleteById(id);
	}

}
