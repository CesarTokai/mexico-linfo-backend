package com.mexicolindotours.service;

import com.mexicolindotours.model.Camioneta;
import com.mexicolindotours.model.Mantenimiento;
import com.mexicolindotours.repository.CamionetaRepository;
import com.mexicolindotours.repository.MantenimientoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class MantenimientoService {

	@Autowired
	private MantenimientoRepository mantenimientoRepository;

	@Autowired
	private CamionetaRepository camionetaRepository;

	public Mantenimiento crear(Long camionetaId, LocalDate fecha, Mantenimiento.Tipo tipo, BigDecimal costo) {
		return crear(camionetaId, fecha, tipo, costo, null);
	}

	public Mantenimiento crear(Long camionetaId, LocalDate fecha, Mantenimiento.Tipo tipo, BigDecimal costo, String descripcion) {
		Camioneta camioneta = camionetaRepository.findById(camionetaId)
				.orElseThrow(() -> new IllegalArgumentException("Camioneta no encontrada"));

		Mantenimiento mant = new Mantenimiento(camioneta, fecha, tipo, costo);
		mant.setKmAlMomento(camioneta.getKmActual());
		if (descripcion != null) {
			mant.setDescripcion(descripcion);
		}

		Mantenimiento saved = mantenimientoRepository.save(mant);

		camioneta.setKmActual(camioneta.getKmActual());
		camionetaRepository.save(camioneta);

		return saved;
	}

	public Mantenimiento obtenerPorId(Long id) {
		return mantenimientoRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Mantenimiento no encontrado"));
	}

	public List<Mantenimiento> obtenerPorCamioneta(Long camionetaId) {
		return mantenimientoRepository.findByCamionetaId(camionetaId);
	}

	public List<Mantenimiento> obtenerTodos() {
		return mantenimientoRepository.findAll();
	}

	public Mantenimiento actualizar(Long id, LocalDate fecha, BigDecimal costo, String descripcion) {
		Mantenimiento mant = obtenerPorId(id);
		if (fecha != null) mant.setFecha(fecha);
		if (costo != null) mant.setCosto(costo);
		if (descripcion != null) mant.setDescripcion(descripcion);
		return mantenimientoRepository.save(mant);
	}

	public void eliminar(Long id) {
		mantenimientoRepository.deleteById(id);
	}

	public Optional<Integer> calcularKmsFaltantesParaProximoMantenimiento(Long camionetaId) {
		Camioneta camioneta = camionetaRepository.findById(camionetaId)
				.orElseThrow(() -> new IllegalArgumentException("Camioneta no encontrada"));

		int kmActual = camioneta.getKmActual() != null ? camioneta.getKmActual() : 0;
		int intervaloMantenimiento = camioneta.getKmMantenimiento() != null ? camioneta.getKmMantenimiento() : 10000;
		int proximoKmMantenimiento = ((kmActual / intervaloMantenimiento) + 1) * intervaloMantenimiento;

		int kmsFaltantes = proximoKmMantenimiento - kmActual;
		return Optional.of(kmsFaltantes);
	}

	public boolean debeAvisarMantenimiento(Long camionetaId) {
		Optional<Integer> kmsFaltantes = calcularKmsFaltantesParaProximoMantenimiento(camionetaId);
		if (kmsFaltantes.isEmpty()) return false;

		int kms = kmsFaltantes.get();
		return kms <= 500;
	}

	public Optional<String> obtenerNivelAvisoMantenimiento(Long camionetaId) {
		Optional<Integer> kmsFaltantes = calcularKmsFaltantesParaProximoMantenimiento(camionetaId);
		if (kmsFaltantes.isEmpty()) return Optional.empty();

		int kms = kmsFaltantes.get();
		if (kms <= 300) return Optional.of("CRÍTICO");
		if (kms <= 400) return Optional.of("ALTO");
		if (kms <= 500) return Optional.of("MEDIO");
		return Optional.empty();
	}
}
