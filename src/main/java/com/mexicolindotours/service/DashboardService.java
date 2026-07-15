package com.mexicolindotours.service;

import com.mexicolindotours.model.Camioneta;
import com.mexicolindotours.model.Viaje;
import com.mexicolindotours.repository.CamionetaRepository;
import com.mexicolindotours.repository.ViajeRepository;
import com.mexicolindotours.repository.GastoRepository;
import com.mexicolindotours.repository.PagoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.*;

@Service
public class DashboardService {

	@Autowired
	private CamionetaRepository camionetaRepository;

	@Autowired
	private ViajeRepository viajeRepository;

	@Autowired
	private GastoRepository gastoRepository;

	@Autowired
	private PagoRepository pagoRepository;

	public Map<String, Object> obtenerDashboardMes(Integer mes, Integer anio) {
		YearMonth periodo = YearMonth.of(anio, mes);
		List<Camioneta> camionetas = camionetaRepository.findAll();

		BigDecimal ingresosTotal = BigDecimal.ZERO;
		BigDecimal egresosTotal = BigDecimal.ZERO;
		List<Map<String, Object>> camionetasData = new ArrayList<>();

		for (Camioneta camioneta : camionetas) {
			Map<String, Object> data = calcularGananciaCamioneta(camioneta, periodo);
			camionetasData.add(data);

			ingresosTotal = ingresosTotal.add((BigDecimal) data.get("ingresos"));
			egresosTotal = egresosTotal.add((BigDecimal) data.get("egresos"));
		}

		BigDecimal neto = ingresosTotal.subtract(egresosTotal);

		Map<String, Object> result = new HashMap<>();
		result.put("periodo", periodo.toString());
		result.put("ingresosTotal", ingresosTotal);
		result.put("egresosTotal", egresosTotal);
		result.put("netoTotal", neto);
		result.put("camionetas", camionetasData);

		return result;
	}

	public Map<String, Object> obtenerDashboardAnio(Integer anio) {
		List<Camioneta> camionetas = camionetaRepository.findAll();

		BigDecimal ingresosTotal = BigDecimal.ZERO;
		BigDecimal egresosTotal = BigDecimal.ZERO;
		List<Map<String, Object>> camionetasData = new ArrayList<>();

		for (Camioneta camioneta : camionetas) {
			Map<String, Object> data = calcularGananciaCamionetaAnio(camioneta, anio);
			camionetasData.add(data);

			ingresosTotal = ingresosTotal.add((BigDecimal) data.get("ingresos"));
			egresosTotal = egresosTotal.add((BigDecimal) data.get("egresos"));
		}

		BigDecimal neto = ingresosTotal.subtract(egresosTotal);

		Map<String, Object> result = new HashMap<>();
		result.put("periodo", String.valueOf(anio));
		result.put("ingresosTotal", ingresosTotal);
		result.put("egresosTotal", egresosTotal);
		result.put("netoTotal", neto);
		result.put("camionetas", camionetasData);

		return result;
	}

	public Map<String, Object> obtenerDashboardAcumulado() {
		List<Camioneta> camionetas = camionetaRepository.findAll();

		BigDecimal ingresosTotal = BigDecimal.ZERO;
		BigDecimal egresosTotal = BigDecimal.ZERO;
		List<Map<String, Object>> camionetasData = new ArrayList<>();

		for (Camioneta camioneta : camionetas) {
			Map<String, Object> data = calcularGananciaCamionetaAcumulada(camioneta);
			camionetasData.add(data);

			ingresosTotal = ingresosTotal.add((BigDecimal) data.get("ingresos"));
			egresosTotal = egresosTotal.add((BigDecimal) data.get("egresos"));
		}

		BigDecimal neto = ingresosTotal.subtract(egresosTotal);

		Map<String, Object> result = new HashMap<>();
		result.put("periodo", "Acumulado");
		result.put("ingresosTotal", ingresosTotal);
		result.put("egresosTotal", egresosTotal);
		result.put("netoTotal", neto);
		result.put("camionetas", camionetasData);

		return result;
	}

	private Map<String, Object> calcularGananciaCamioneta(Camioneta camioneta, YearMonth periodo) {
		List<Viaje> viajes = viajeRepository.findByCamionetaId(camioneta.getId()).stream()
				.filter(v -> !v.getEstado().equals(Viaje.Estado.cancelado))
				.filter(v -> YearMonth.from(v.getFechaInicio()).equals(periodo))
				.toList();

		BigDecimal ingresos = viajes.stream()
				.map(Viaje::getCostoTotal)
				.reduce(BigDecimal.ZERO, BigDecimal::add);

		BigDecimal egresos = viajes.stream()
				.flatMap(v -> gastoRepository.findByViajeId(v.getId()).stream())
				.map(g -> g.getMonto())
				.reduce(BigDecimal.ZERO, BigDecimal::add);

		BigDecimal neto = ingresos.subtract(egresos);
		double porcentajeUtil = viajes.isEmpty() ? 0 : (viajes.size() / 30.0) * 100;

		Map<String, Object> result = new HashMap<>();
		result.put("camionetaId", camioneta.getId());
		result.put("camionetaNombre", camioneta.getNombre());
		result.put("ingresos", ingresos);
		result.put("egresos", egresos);
		result.put("neto", neto);
		result.put("viajesCompletados", viajes.size());
		result.put("porcentajeUtilizacion", Math.round(porcentajeUtil * 100.0) / 100.0);

		return result;
	}

	private Map<String, Object> calcularGananciaCamionetaAnio(Camioneta camioneta, Integer anio) {
		List<Viaje> viajes = viajeRepository.findByCamionetaId(camioneta.getId()).stream()
				.filter(v -> !v.getEstado().equals(Viaje.Estado.cancelado))
				.filter(v -> v.getFechaInicio().getYear() == anio)
				.toList();

		BigDecimal ingresos = viajes.stream()
				.map(Viaje::getCostoTotal)
				.reduce(BigDecimal.ZERO, BigDecimal::add);

		BigDecimal egresos = viajes.stream()
				.flatMap(v -> gastoRepository.findByViajeId(v.getId()).stream())
				.map(g -> g.getMonto())
				.reduce(BigDecimal.ZERO, BigDecimal::add);

		BigDecimal neto = ingresos.subtract(egresos);

		Map<String, Object> result = new HashMap<>();
		result.put("camionetaId", camioneta.getId());
		result.put("camionetaNombre", camioneta.getNombre());
		result.put("ingresos", ingresos);
		result.put("egresos", egresos);
		result.put("neto", neto);
		result.put("viajesCompletados", viajes.size());

		return result;
	}

	private Map<String, Object> calcularGananciaCamionetaAcumulada(Camioneta camioneta) {
		List<Viaje> viajes = viajeRepository.findByCamionetaId(camioneta.getId()).stream()
				.filter(v -> !v.getEstado().equals(Viaje.Estado.cancelado))
				.toList();

		BigDecimal ingresos = viajes.stream()
				.map(Viaje::getCostoTotal)
				.reduce(BigDecimal.ZERO, BigDecimal::add);

		BigDecimal egresos = viajes.stream()
				.flatMap(v -> gastoRepository.findByViajeId(v.getId()).stream())
				.map(g -> g.getMonto())
				.reduce(BigDecimal.ZERO, BigDecimal::add);

		BigDecimal neto = ingresos.subtract(egresos);

		Map<String, Object> result = new HashMap<>();
		result.put("camionetaId", camioneta.getId());
		result.put("camionetaNombre", camioneta.getNombre());
		result.put("ingresos", ingresos);
		result.put("egresos", egresos);
		result.put("neto", neto);
		result.put("viajesCompletados", viajes.size());

		return result;
	}
}
