package com.mexicolindotours.service;

import com.mexicolindotours.model.Camioneta;
import com.mexicolindotours.model.TramiteVehiculo;
import com.mexicolindotours.repository.CamionetaRepository;
import com.mexicolindotours.repository.TramiteVehiculoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
public class TramiteVehiculoService {

	@Autowired
	private TramiteVehiculoRepository tramiteVehiculoRepository;

	@Autowired
	private CamionetaRepository camionetaRepository;

	public TramiteVehiculo crear(Long camionetaId, TramiteVehiculo.Tipo tipo, LocalDate fechaPago, BigDecimal monto) {
		return crear(camionetaId, tipo, fechaPago, monto, null, null);
	}

	public TramiteVehiculo crear(Long camionetaId, TramiteVehiculo.Tipo tipo, LocalDate fechaPago,
								 BigDecimal monto, LocalDate fechaVencimiento, String notas) {
		Camioneta camioneta = camionetaRepository.findById(camionetaId)
				.orElseThrow(() -> new IllegalArgumentException("Camioneta no encontrada"));

		TramiteVehiculo tramite = new TramiteVehiculo(camioneta, tipo, fechaPago, monto);
		if (fechaVencimiento != null) {
			tramite.setFechaVencimiento(fechaVencimiento);
		}
		if (notas != null) {
			tramite.setNotas(notas);
		}
		return tramiteVehiculoRepository.save(tramite);
	}

	public TramiteVehiculo obtenerPorId(Long id) {
		return tramiteVehiculoRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Trámite no encontrado"));
	}

	public List<TramiteVehiculo> obtenerPorCamioneta(Long camionetaId) {
		return tramiteVehiculoRepository.findByCamionetaId(camionetaId);
	}

	public List<TramiteVehiculo> obtenerTodos() {
		return tramiteVehiculoRepository.findAll();
	}

	public TramiteVehiculo actualizar(Long id, LocalDate fechaVencimiento, BigDecimal monto, String notas) {
		TramiteVehiculo tramite = obtenerPorId(id);
		if (fechaVencimiento != null) tramite.setFechaVencimiento(fechaVencimiento);
		if (monto != null) tramite.setMonto(monto);
		if (notas != null) tramite.setNotas(notas);
		return tramiteVehiculoRepository.save(tramite);
	}

	public void eliminar(Long id) {
		tramiteVehiculoRepository.deleteById(id);
	}

	public Optional<Long> calcularDiasParaVencimiento(Long tramiteId) {
		TramiteVehiculo tramite = obtenerPorId(tramiteId);
		if (tramite.getFechaVencimiento() == null) {
			return Optional.empty();
		}

		long diasFaltantes = ChronoUnit.DAYS.between(LocalDate.now(), tramite.getFechaVencimiento());
		return Optional.of(diasFaltantes);
	}

	public boolean debeAvisarVencimiento(Long tramiteId) {
		Optional<Long> diasFaltantes = calcularDiasParaVencimiento(tramiteId);
		if (diasFaltantes.isEmpty()) return false;

		long dias = diasFaltantes.get();
		return dias <= 30;
	}

	public Optional<String> obtenerNivelAvisoVencimiento(Long tramiteId) {
		Optional<Long> diasFaltantes = calcularDiasParaVencimiento(tramiteId);
		if (diasFaltantes.isEmpty()) return Optional.empty();

		long dias = diasFaltantes.get();
		if (dias <= 0) return Optional.of("VENCIDO");
		if (dias <= 5) return Optional.of("CRÍTICO");
		if (dias <= 10) return Optional.of("ALTO");
		if (dias <= 15) return Optional.of("MEDIO");
		if (dias <= 30) return Optional.of("BAJO");
		return Optional.empty();
	}
}
