package com.mexicolindotours.service;

import com.mexicolindotours.model.*;
import com.mexicolindotours.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ViajeService {

	@Autowired
	private ViajeRepository viajeRepository;

	@Autowired
	private ClienteRepository clienteRepository;

	@Autowired
	private CamionetaRepository camionetaRepository;

	@Autowired
	private ChoferRepository choferRepository;

	@Autowired
	private PagoRepository pagoRepository;

	@Autowired
	private GastoRepository gastoRepository;

	public Viaje crear(Long clienteId, Long camionetaId, Long choferId, String concepto,
					   LocalDate fechaInicio, LocalDate fechaFin, BigDecimal costoTotal) {

		Cliente cliente = clienteRepository.findById(clienteId)
				.orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado"));

		Camioneta camioneta = camionetaRepository.findById(camionetaId)
				.orElseThrow(() -> new IllegalArgumentException("Camioneta no encontrada"));

		if (choferId != null && choferRepository.findById(choferId).isEmpty()) {
			throw new IllegalArgumentException("Chofer no encontrado");
		}

		validarAntiDobleReserva(camionetaId, fechaInicio, fechaFin);

		if (camioneta.getEstado() == Camioneta.Estado.en_taller) {
			throw new IllegalArgumentException("Camioneta en taller, no disponible");
		}

		Viaje viaje = new Viaje(cliente, camioneta, concepto, fechaInicio, fechaFin, costoTotal);
		if (choferId != null) {
			viaje.setChofer(choferRepository.findById(choferId).get());
		}

		return viajeRepository.save(viaje);
	}

	public Viaje obtenerPorId(Long id) {
		return viajeRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Viaje no encontrado"));
	}

	public List<Viaje> obtenerPorCamioneta(Long camionetaId) {
		return viajeRepository.findByCamionetaId(camionetaId);
	}

	public List<Viaje> obtenerPorCliente(Long clienteId) {
		return viajeRepository.findByClienteId(clienteId);
	}

	public List<Viaje> obtenerPorEstado(Viaje.Estado estado) {
		return viajeRepository.findByEstado(estado);
	}

	public List<Viaje> obtenerTodos() {
		return viajeRepository.findAll();
	}

	public Viaje actualizarEstado(Long id, Viaje.Estado nuevoEstado) {
		Viaje viaje = obtenerPorId(id);
		viaje.setEstado(nuevoEstado);
		viaje.setUpdatedAt(LocalDateTime.now());
		return viajeRepository.save(viaje);
	}

	public Viaje finalizarViaje(Long id, Integer kmFinal) {
		Viaje viaje = obtenerPorId(id);

		if (kmFinal == null || kmFinal < 0) {
			throw new IllegalArgumentException("km_final inválido");
		}

		if (viaje.getKmInicial() != null && kmFinal < viaje.getKmInicial()) {
			throw new IllegalArgumentException("km_final no puede ser menor que km_inicial");
		}

		if (kmFinal < viaje.getCamioneta().getKmActual()) {
			throw new IllegalArgumentException("km_final no puede ser menor que km_actual de camioneta");
		}

		viaje.setKmFinal(kmFinal);
		viaje.setEstado(Viaje.Estado.finalizado);
		viaje.setUpdatedAt(LocalDateTime.now());

		Viaje viajeGuardado = viajeRepository.save(viaje);

		Camioneta camioneta = viaje.getCamioneta();
		camioneta.setKmActual(kmFinal);
		camioneta.setUpdatedAt(LocalDateTime.now());
		camionetaRepository.save(camioneta);

		return viajeGuardado;
	}

	public Viaje actualizarViaje(Long id, String concepto, LocalDate fechaInicio, LocalDate fechaFin, BigDecimal costoTotal, Integer kmInicial, Long choferId) {
		Viaje viaje = obtenerPorId(id);

		if (viaje.getEstado() == Viaje.Estado.finalizado) {
			throw new IllegalArgumentException("No se puede editar un viaje finalizado");
		}

		if (concepto != null) viaje.setConcepto(concepto);
		if (costoTotal != null) viaje.setCostoTotal(costoTotal);
		if (kmInicial != null) viaje.setKmInicial(kmInicial);

		if (fechaInicio != null || fechaFin != null) {
			LocalDate fi = fechaInicio != null ? fechaInicio : viaje.getFechaInicio();
			LocalDate ff = fechaFin != null ? fechaFin : viaje.getFechaFin();
			validarAntiDobleReserva(viaje.getCamioneta().getId(), fi, ff, id);
			viaje.setFechaInicio(fi);
			viaje.setFechaFin(ff);
		}

		if (choferId != null && choferRepository.findById(choferId).isEmpty()) {
			throw new IllegalArgumentException("Chofer no encontrado");
		}
		if (choferId != null) {
			viaje.setChofer(choferRepository.findById(choferId).get());
		}

		viaje.setUpdatedAt(LocalDateTime.now());
		return viajeRepository.save(viaje);
	}

	public void cancelarViaje(Long id) {
		Viaje viaje = obtenerPorId(id);
		viaje.setEstado(Viaje.Estado.cancelado);
		viaje.setUpdatedAt(LocalDateTime.now());
		viajeRepository.save(viaje);
	}

	private void validarAntiDobleReserva(Long camionetaId, LocalDate fechaInicio, LocalDate fechaFin) {
		validarAntiDobleReserva(camionetaId, fechaInicio, fechaFin, null);
	}

	private void validarAntiDobleReserva(Long camionetaId, LocalDate fechaInicio, LocalDate fechaFin, Long viajeIdActual) {
		List<Viaje> viajes = viajeRepository.findByCamionetaId(camionetaId);

		for (Viaje v : viajes) {
			if (viajeIdActual != null && v.getId().equals(viajeIdActual)) {
				continue;
			}

			if (v.getEstado() == Viaje.Estado.cancelado) {
				continue;
			}

			boolean traslape = !(fechaFin.isBefore(v.getFechaInicio()) || fechaInicio.isAfter(v.getFechaFin()));
			if (traslape) {
				throw new IllegalArgumentException("Camioneta ocupada en esas fechas");
			}
		}
	}

}
