package com.mexicolindotours.service;

import com.mexicolindotours.dto.HistorialChoferDTO;
import com.mexicolindotours.dto.HistorialClienteDTO;
import com.mexicolindotours.dto.HistorialCamionetaDTO;
import com.mexicolindotours.model.*;
import com.mexicolindotours.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;

@Service
public class HistorialService {

	@Autowired
	private ChoferRepository choferRepository;

	@Autowired
	private ClienteRepository clienteRepository;

	@Autowired
	private CamionetaRepository camionetaRepository;

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

	public HistorialChoferDTO obtenerHistorialChofer(Long choferId) {
		Chofer chofer = choferRepository.findById(choferId)
				.orElseThrow(() -> new IllegalArgumentException("Chofer no encontrado"));

		List<Viaje> viajes = viajeRepository.findByChoferId(choferId);

		int totalViajes = viajes.size();
		int kmManejados = viajes.stream()
				.filter(v -> v.getKmFinal() != null && v.getKmInicial() != null)
				.mapToInt(v -> v.getKmFinal() - v.getKmInicial())
				.sum();

		BigDecimal totalPagado = viajes.stream()
				.flatMap(v -> gastoRepository.findByViajeId(v.getId()).stream()
						.filter(g -> g.getTipo() == Gasto.Tipo.chofer))
				.map(Gasto::getMonto)
				.reduce(BigDecimal.ZERO, BigDecimal::add);

		return new HistorialChoferDTO(choferId, chofer.getNombre(), totalViajes, kmManejados, totalPagado);
	}

	public HistorialClienteDTO obtenerHistorialCliente(Long clienteId) {
		Cliente cliente = clienteRepository.findById(clienteId)
				.orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado"));

		List<Viaje> viajes = viajeRepository.findByClienteId(clienteId);

		int totalViajes = viajes.size();

		BigDecimal totalPagado = viajes.stream()
				.flatMap(v -> pagoRepository.findByViajeId(v.getId()).stream())
				.map(Pago::getMonto)
				.reduce(BigDecimal.ZERO, BigDecimal::add);

		BigDecimal pendiente = viajes.stream()
				.filter(v -> v.getEstado() != Viaje.Estado.cancelado)
				.map(v -> {
					BigDecimal pagado = pagoRepository.findByViajeId(v.getId()).stream()
							.map(Pago::getMonto)
							.reduce(BigDecimal.ZERO, BigDecimal::add);
					return v.getCostoTotal().subtract(pagado);
				})
				.reduce(BigDecimal.ZERO, BigDecimal::add);

		return new HistorialClienteDTO(clienteId, cliente.getNombre(), totalViajes, totalPagado, pendiente);
	}

	public HistorialCamionetaDTO obtenerHistorialCamioneta(Long camionetaId) {
		Camioneta camioneta = camionetaRepository.findById(camionetaId)
				.orElseThrow(() -> new IllegalArgumentException("Camioneta no encontrada"));

		List<Viaje> viajes = viajeRepository.findByCamionetaId(camionetaId);
		List<Mantenimiento> mantenimientos = mantenimientoRepository.findByCamionetaId(camionetaId);
		List<TramiteVehiculo> tramites = tramiteVehiculoRepository.findByCamionetaId(camionetaId);

		int totalViajes = viajes.size();
		int kmActual = camioneta.getKmActual();

		BigDecimal costosMantenimiento = mantenimientos.stream()
				.map(Mantenimiento::getCosto)
				.reduce(BigDecimal.ZERO, BigDecimal::add);

		BigDecimal costosTramites = tramites.stream()
				.map(TramiteVehiculo::getMonto)
				.reduce(BigDecimal.ZERO, BigDecimal::add);

		return new HistorialCamionetaDTO(camionetaId, camioneta.getNombre(), totalViajes, kmActual, costosMantenimiento, costosTramites);
	}

}
