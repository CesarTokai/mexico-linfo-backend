package com.mexicolindotours.service;

import com.mexicolindotours.model.GastoGeneral;
import com.mexicolindotours.repository.GastoGeneralRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
public class GastoGeneralService {

	@Autowired
	private GastoGeneralRepository gastoGeneralRepository;

	public GastoGeneral crear(LocalDate fecha, String descripcion, BigDecimal monto) {
		GastoGeneral gasto = new GastoGeneral(fecha, descripcion, monto);
		return gastoGeneralRepository.save(gasto);
	}

	public GastoGeneral obtenerPorId(Long id) {
		return gastoGeneralRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Gasto general no encontrado"));
	}

	public List<GastoGeneral> obtenerTodos() {
		return gastoGeneralRepository.findAll();
	}

	public List<GastoGeneral> obtenerPorMes(Integer mes, Integer anio) {
		YearMonth periodo = YearMonth.of(anio, mes);
		return gastoGeneralRepository.findAll().stream()
				.filter(g -> YearMonth.from(g.getFecha()).equals(periodo))
				.toList();
	}

	public List<GastoGeneral> obtenerPorAnio(Integer anio) {
		return gastoGeneralRepository.findAll().stream()
				.filter(g -> g.getFecha().getYear() == anio)
				.toList();
	}

	public GastoGeneral actualizar(Long id, LocalDate fecha, String descripcion, BigDecimal monto) {
		GastoGeneral gasto = obtenerPorId(id);
		if (fecha != null) gasto.setFecha(fecha);
		if (descripcion != null) gasto.setDescripcion(descripcion);
		if (monto != null) gasto.setMonto(monto);
		return gastoGeneralRepository.save(gasto);
	}

	public void eliminar(Long id) {
		gastoGeneralRepository.deleteById(id);
	}

	public BigDecimal obtenerTotalPorMes(Integer mes, Integer anio) {
		return obtenerPorMes(mes, anio).stream()
				.map(GastoGeneral::getMonto)
				.reduce(BigDecimal.ZERO, BigDecimal::add);
	}

	public BigDecimal obtenerTotalPorAnio(Integer anio) {
		return obtenerPorAnio(anio).stream()
				.map(GastoGeneral::getMonto)
				.reduce(BigDecimal.ZERO, BigDecimal::add);
	}

	public BigDecimal obtenerTotalAcumulado() {
		return obtenerTodos().stream()
				.map(GastoGeneral::getMonto)
				.reduce(BigDecimal.ZERO, BigDecimal::add);
	}
}
