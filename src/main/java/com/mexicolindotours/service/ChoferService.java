package com.mexicolindotours.service;

import com.mexicolindotours.model.Chofer;
import com.mexicolindotours.repository.ChoferRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ChoferService {

	@Autowired
	private ChoferRepository choferRepository;

	public Chofer crear(String nombre, String telefono) {
		Chofer chofer = new Chofer(nombre);
		if (telefono != null) chofer.setTelefono(telefono);
		return choferRepository.save(chofer);
	}

	public Optional<Chofer> obtenerPorId(Long id) {
		return choferRepository.findById(id);
	}

	public List<Chofer> obtenerActivos() {
		return choferRepository.findByActivoTrue();
	}

	public List<Chofer> obtenerTodos() {
		return choferRepository.findAll();
	}

	public Chofer actualizar(Long id, String nombre, String telefono, LocalDate licenciaVencimiento) {
		Chofer chofer = choferRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Chofer no encontrado"));

		if (nombre != null) chofer.setNombre(nombre);
		if (telefono != null) chofer.setTelefono(telefono);
		if (licenciaVencimiento != null) chofer.setLicenciaVencimiento(licenciaVencimiento);

		chofer.setUpdatedAt(LocalDateTime.now());
		return choferRepository.save(chofer);
	}

	public void desactivar(Long id) {
		Chofer chofer = choferRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Chofer no encontrado"));
		chofer.setActivo(false);
		chofer.setUpdatedAt(LocalDateTime.now());
		choferRepository.save(chofer);
	}

}
