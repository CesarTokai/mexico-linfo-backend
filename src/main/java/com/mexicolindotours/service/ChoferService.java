package com.mexicolindotours.service;

import com.mexicolindotours.model.Chofer;
import com.mexicolindotours.repository.ChoferRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ChoferService {

	@Autowired
	private ChoferRepository choferRepository;

	public Chofer crear(String nombre, String telefono, LocalDate licenciaVencimiento) {
		Chofer chofer = new Chofer(nombre);
		if (telefono != null) chofer.setTelefono(telefono);
		if (licenciaVencimiento != null) chofer.setLicenciaVencimiento(licenciaVencimiento);
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

	/** Dias para que venza la licencia. Negativo = ya vencida. */
	public Optional<Long> calcularDiasParaVencimientoLicencia(Long choferId) {
		Chofer chofer = choferRepository.findById(choferId)
				.orElseThrow(() -> new IllegalArgumentException("Chofer no encontrado"));

		if (chofer.getLicenciaVencimiento() == null) {
			return Optional.empty();
		}

		return Optional.of(ChronoUnit.DAYS.between(LocalDate.now(), chofer.getLicenciaVencimiento()));
	}

	/** Mismos umbrales que los tramites: 30/15/10/5 dias, y VENCIDO persiste. */
	public Optional<String> obtenerNivelAvisoLicencia(Long choferId) {
		Optional<Long> dias = calcularDiasParaVencimientoLicencia(choferId);
		if (dias.isEmpty()) return Optional.empty();

		long d = dias.get();
		if (d <= 0) return Optional.of("VENCIDO");
		if (d <= 5) return Optional.of("CRÍTICO");
		if (d <= 10) return Optional.of("ALTO");
		if (d <= 15) return Optional.of("MEDIO");
		if (d <= 30) return Optional.of("BAJO");
		return Optional.empty();
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
