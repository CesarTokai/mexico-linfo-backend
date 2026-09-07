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

	@Autowired
	private DisponibilidadUnidadService disponibilidadUnidadService;

	public Viaje crear(Long clienteId, Long camionetaId, Long choferId, String concepto,
					   LocalDate fechaInicio, LocalDate fechaFin, BigDecimal costoTotal) {

		Cliente cliente = clienteRepository.findById(clienteId)
				.orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado"));

		Camioneta camioneta = camionetaRepository.findById(camionetaId)
				.orElseThrow(() -> new IllegalArgumentException("Camioneta no encontrada"));

		if (choferId != null && choferRepository.findById(choferId).isEmpty()) {
			throw new IllegalArgumentException("Chofer no encontrado");
		}

		validarFechas(fechaInicio, fechaFin);
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
		validarTransicion(viaje.getEstado(), nuevoEstado);
		viaje.setEstado(nuevoEstado);
		viaje.setUpdatedAt(LocalDateTime.now());
		return viajeRepository.save(viaje);
	}

	/**
	 * Ciclo de vida: apartado -> en_curso -> finalizado, o cancelado desde
	 * apartado/en_curso. Un viaje finalizado o cancelado es terminal.
	 * Se prohibe saltar a finalizado por aqui: eso va por finalizarViaje(),
	 * que exige km_final y actualiza el odometro de la camioneta.
	 */
	private void validarTransicion(Viaje.Estado actual, Viaje.Estado nuevo) {
		if (nuevo == null) {
			throw new IllegalArgumentException("Estado destino requerido");
		}
		if (actual == nuevo) {
			return;
		}
		if (actual == Viaje.Estado.finalizado || actual == Viaje.Estado.cancelado) {
			throw new IllegalArgumentException("Un viaje " + actual + " ya no cambia de estado");
		}
		if (nuevo == Viaje.Estado.finalizado) {
			throw new IllegalArgumentException("Para finalizar usa el endpoint de finalizar viaje (requiere km_final)");
		}
		if (nuevo == Viaje.Estado.apartado && actual == Viaje.Estado.en_curso) {
			throw new IllegalArgumentException("Un viaje en curso no puede volver a apartado");
		}
	}

	public Viaje finalizarViaje(Long id, Integer kmFinal) {
		Viaje viaje = obtenerPorId(id);

		if (viaje.getEstado() == Viaje.Estado.cancelado) {
			throw new IllegalArgumentException("No se puede finalizar un viaje cancelado");
		}

		if (viaje.getEstado() == Viaje.Estado.finalizado) {
			throw new IllegalArgumentException("El viaje ya está finalizado");
		}

		if (kmFinal == null || kmFinal < 0) {
			throw new IllegalArgumentException("km_final inválido");
		}

		// Regla 11: el recorrido debe ser estrictamente mayor que cero.
		if (viaje.getKmInicial() != null && kmFinal <= viaje.getKmInicial()) {
			throw new IllegalArgumentException("km_final debe ser mayor que km_inicial");
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

	public Viaje actualizarViaje(Long id, String concepto, LocalDate fechaInicio, LocalDate fechaFin, BigDecimal costoTotal, Integer kmInicial, Long choferId, boolean esAdmin) {
		Viaje viaje = obtenerPorId(id);

		if (viaje.getEstado() == Viaje.Estado.finalizado && !esAdmin) {
			throw new IllegalArgumentException("No se puede editar un viaje finalizado");
		}

		if (concepto != null) viaje.setConcepto(concepto);
		if (costoTotal != null) viaje.setCostoTotal(costoTotal);
		if (kmInicial != null) {
			validarKmInicial(kmInicial, viaje.getCamioneta());
			viaje.setKmInicial(kmInicial);
		}

		if (fechaInicio != null || fechaFin != null) {
			LocalDate fi = fechaInicio != null ? fechaInicio : viaje.getFechaInicio();
			LocalDate ff = fechaFin != null ? fechaFin : viaje.getFechaFin();
			validarFechas(fi, ff);
			if (viaje.getCamioneta().getEstado() == Camioneta.Estado.en_taller) {
				throw new IllegalArgumentException("Camioneta en taller, no se pueden mover las fechas del viaje");
			}
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

	private void validarFechas(LocalDate fechaInicio, LocalDate fechaFin) {
		if (fechaInicio == null || fechaFin == null) {
			throw new IllegalArgumentException("Las fechas de inicio y fin son obligatorias");
		}
		if (fechaFin.isBefore(fechaInicio)) {
			throw new IllegalArgumentException("La fecha de fin no puede ser anterior a la de inicio");
		}
	}

	/** Regla 11: el viaje no puede arrancar por debajo del odometro de la unidad. */
	private void validarKmInicial(Integer kmInicial, Camioneta camioneta) {
		if (kmInicial == null) return;
		if (kmInicial < 0) {
			throw new IllegalArgumentException("km_inicial inválido");
		}
		Integer kmActual = camioneta.getKmActual();
		if (kmActual != null && kmInicial < kmActual) {
			throw new IllegalArgumentException(
					"km_inicial (" + kmInicial + ") no puede ser menor que el km actual de la camioneta (" + kmActual + ")");
		}
	}

	private void validarAntiDobleReserva(Long camionetaId, LocalDate fechaInicio, LocalDate fechaFin) {
		validarAntiDobleReserva(camionetaId, fechaInicio, fechaFin, null);
	}

	/** Mira viajes Y salidas publicas: la unidad es una sola. */
	private void validarAntiDobleReserva(Long camionetaId, LocalDate fechaInicio, LocalDate fechaFin, Long viajeIdActual) {
		disponibilidadUnidadService.verificarLibre(camionetaId, fechaInicio, fechaFin, viajeIdActual, null);
	}

}
