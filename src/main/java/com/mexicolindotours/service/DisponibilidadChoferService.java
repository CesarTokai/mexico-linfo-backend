package com.mexicolindotours.service;

import com.mexicolindotours.model.Chofer;
import com.mexicolindotours.model.DisponibilidadChofer;
import com.mexicolindotours.repository.ChoferRepository;
import com.mexicolindotours.repository.DisponibilidadChoferRepository;
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
