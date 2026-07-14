package com.mexicolindotours.service;

import com.mexicolindotours.model.Camioneta;
import com.mexicolindotours.repository.CamionetaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CamionetaService {

	@Autowired
	private CamionetaRepository camionetaRepository;

	public Camioneta crear(String nombre, String modelo, Integer capacidad) {
		Camioneta camioneta = new Camioneta(nombre, modelo, capacidad);
		return camionetaRepository.save(camioneta);
	}

	public Optional<Camioneta> obtenerPorId(Long id) {
		return camionetaRepository.findById(id);
	}

	public List<Camioneta> obtenerTodas() {
		return camionetaRepository.findAll();
	}

	public Camioneta actualizar(Long id, String nombre, String modelo, Integer capacidad, Camioneta.Estado estado, Integer intervaloMantenimiento) {
		Camioneta camioneta = camionetaRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Camioneta no encontrada"));

		if (nombre != null) camioneta.setNombre(nombre);
		if (modelo != null) camioneta.setModelo(modelo);
		if (capacidad != null) camioneta.setCapacidad(capacidad);
		if (estado != null) camioneta.setEstado(estado);
		if (intervaloMantenimiento != null) camioneta.setIntervaloMantenimientoKm(intervaloMantenimiento);

		camioneta.setUpdatedAt(LocalDateTime.now());
		return camionetaRepository.save(camioneta);
	}

	public Camioneta actualizarKm(Long id, Integer kmFinal) {
		Camioneta camioneta = camionetaRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Camioneta no encontrada"));
		camioneta.setKmActual(kmFinal);
		camioneta.setUpdatedAt(LocalDateTime.now());
		return camionetaRepository.save(camioneta);
	}

	public void desactivar(Long id) {
		Camioneta camioneta = camionetaRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Camioneta no encontrada"));
		camioneta.setEstado(Camioneta.Estado.baja);
		camioneta.setUpdatedAt(LocalDateTime.now());
		camionetaRepository.save(camioneta);
	}

}
