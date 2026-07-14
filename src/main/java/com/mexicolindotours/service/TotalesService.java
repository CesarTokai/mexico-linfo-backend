package com.mexicolindotours.service;

import com.mexicolindotours.dto.TotalesDTO;
import com.mexicolindotours.model.*;
import com.mexicolindotours.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;

@Service
public class TotalesService {

	@Autowired
	private ViajeRepository viajeRepository;

	@Autowired
	private PagoRepository pagoRepository;

	@Autowired
	private GastoRepository gastoRepository;

	@Autowired
	private MantenimientoRepository mantenimientoRepository;

	@Autowired
	private TramiteVehiculoRepository tramiteVehiculoRepository;

	@Autowired
	private GastoGeneralRepository gastoGeneralRepository;

	public TotalesDTO obtenerTotalesMes(Integer mes, Integer anio) {
		YearMonth periodo = YearMonth.of(anio, mes);

		List<Viaje> viajes = viajeRepository.findAll().stream()
				.filter(v -> YearMonth.from(v.getFechaInicio()).equals(periodo))
				.toList();

		BigDecimal ingresosTotal = calcularIngresos(viajes);
		BigDecimal egresosViajes = calcularEgresosViajes(viajes);
		BigDecimal egresosCamionetas = calcularEgresosCamionetas(periodo);
		BigDecimal egresosGenerales = calcularEgresosGenerales(periodo);
		BigDecimal egresosTotal = egresosViajes.add(egresosCamionetas).add(egresosGenerales);
		BigDecimal neto = ingresosTotal.subtract(egresosTotal);
		BigDecimal pendiente = calcularPendientePorCobrar(viajes);

		return new TotalesDTO(mes, anio, ingresosTotal, egresosViajes, egresosCamionetas, egresosGenerales, egresosTotal, neto, pendiente);
	}

	public TotalesDTO obtenerTotalesAnio(Integer anio) {
		List<Viaje> viajes = viajeRepository.findAll().stream()
				.filter(v -> v.getFechaInicio().getYear() == anio)
				.toList();

		BigDecimal ingresosTotal = calcularIngresos(viajes);
		BigDecimal egresosViajes = calcularEgresosViajes(viajes);

		List<Mantenimiento> mantenimientos = mantenimientoRepository.findAll().stream()
				.filter(m -> m.getFecha().getYear() == anio)
				.toList();
		BigDecimal egresosCamionetas = mantenimientos.stream()
				.map(Mantenimiento::getCosto)
				.reduce(BigDecimal.ZERO, BigDecimal::add);

		List<TramiteVehiculo> tramites = tramiteVehiculoRepository.findAll().stream()
				.filter(t -> t.getFechaPago().getYear() == anio)
				.toList();
		egresosCamionetas = egresosCamionetas.add(
				tramites.stream()
						.map(TramiteVehiculo::getMonto)
						.reduce(BigDecimal.ZERO, BigDecimal::add)
		);

		List<GastoGeneral> gastosGen = gastoGeneralRepository.findAll().stream()
				.filter(g -> g.getFecha().getYear() == anio)
				.toList();
		BigDecimal egresosGenerales = gastosGen.stream()
				.map(GastoGeneral::getMonto)
				.reduce(BigDecimal.ZERO, BigDecimal::add);

		BigDecimal egresosTotal = egresosViajes.add(egresosCamionetas).add(egresosGenerales);
		BigDecimal neto = ingresosTotal.subtract(egresosTotal);
		BigDecimal pendiente = calcularPendientePorCobrar(viajes);

		return new TotalesDTO(null, anio, ingresosTotal, egresosViajes, egresosCamionetas, egresosGenerales, egresosTotal, neto, pendiente);
	}

	public TotalesDTO obtenerTotalesAcumulado() {
		List<Viaje> viajes = viajeRepository.findAll();

		BigDecimal ingresosTotal = calcularIngresos(viajes);
		BigDecimal egresosViajes = calcularEgresosViajes(viajes);

		List<Mantenimiento> mantenimientos = mantenimientoRepository.findAll();
		BigDecimal egresosCamionetas = mantenimientos.stream()
				.map(Mantenimiento::getCosto)
				.reduce(BigDecimal.ZERO, BigDecimal::add);

		List<TramiteVehiculo> tramites = tramiteVehiculoRepository.findAll();
		egresosCamionetas = egresosCamionetas.add(
				tramites.stream()
						.map(TramiteVehiculo::getMonto)
						.reduce(BigDecimal.ZERO, BigDecimal::add)
		);

		List<GastoGeneral> gastosGen = gastoGeneralRepository.findAll();
		BigDecimal egresosGenerales = gastosGen.stream()
				.map(GastoGeneral::getMonto)
				.reduce(BigDecimal.ZERO, BigDecimal::add);

		BigDecimal egresosTotal = egresosViajes.add(egresosCamionetas).add(egresosGenerales);
		BigDecimal neto = ingresosTotal.subtract(egresosTotal);
		BigDecimal pendiente = calcularPendientePorCobrar(viajes);

		return new TotalesDTO(null, null, ingresosTotal, egresosViajes, egresosCamionetas, egresosGenerales, egresosTotal, neto, pendiente);
	}

	private BigDecimal calcularIngresos(List<Viaje> viajes) {
		return viajes.stream()
				.map(Viaje::getCostoTotal)
				.reduce(BigDecimal.ZERO, BigDecimal::add);
	}

	private BigDecimal calcularEgresosViajes(List<Viaje> viajes) {
		return viajes.stream()
				.flatMap(v -> gastoRepository.findByViajeId(v.getId()).stream())
				.map(Gasto::getMonto)
				.reduce(BigDecimal.ZERO, BigDecimal::add);
	}

	private BigDecimal calcularEgresosCamionetas(YearMonth periodo) {
		List<Mantenimiento> mantenimientos = mantenimientoRepository.findAll().stream()
				.filter(m -> YearMonth.from(m.getFecha()).equals(periodo))
				.toList();
		BigDecimal mant = mantenimientos.stream()
				.map(Mantenimiento::getCosto)
				.reduce(BigDecimal.ZERO, BigDecimal::add);

		List<TramiteVehiculo> tramites = tramiteVehiculoRepository.findAll().stream()
				.filter(t -> YearMonth.from(t.getFechaPago()).equals(periodo))
				.toList();
		BigDecimal tram = tramites.stream()
				.map(TramiteVehiculo::getMonto)
				.reduce(BigDecimal.ZERO, BigDecimal::add);

		return mant.add(tram);
	}

	private BigDecimal calcularEgresosGenerales(YearMonth periodo) {
		return gastoGeneralRepository.findAll().stream()
				.filter(g -> YearMonth.from(g.getFecha()).equals(periodo))
				.map(GastoGeneral::getMonto)
				.reduce(BigDecimal.ZERO, BigDecimal::add);
	}

	private BigDecimal calcularPendientePorCobrar(List<Viaje> viajes) {
		return viajes.stream()
				.filter(v -> v.getEstado() != Viaje.Estado.cancelado)
				.map(v -> {
					BigDecimal pagado = pagoRepository.findByViajeId(v.getId()).stream()
							.map(Pago::getMonto)
							.reduce(BigDecimal.ZERO, BigDecimal::add);
					return v.getCostoTotal().subtract(pagado);
				})
				.reduce(BigDecimal.ZERO, BigDecimal::add);
	}

}
