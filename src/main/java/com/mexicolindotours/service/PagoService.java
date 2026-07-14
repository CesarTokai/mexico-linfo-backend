package com.mexicolindotours.service;

import com.mexicolindotours.model.Pago;
import com.mexicolindotours.model.Viaje;
import com.mexicolindotours.repository.PagoRepository;
import com.mexicolindotours.repository.ViajeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class PagoService {

	@Autowired
	private PagoRepository pagoRepository;

	@Autowired
	private ViajeRepository viajeRepository;

	public Pago crear(Long viajeId, Pago.Tipo tipo, LocalDate fecha, BigDecimal monto) {
		Viaje viaje = viajeRepository.findById(viajeId)
				.orElseThrow(() -> new IllegalArgumentException("Viaje no encontrado"));

		Pago pago = new Pago(viaje, tipo, fecha, monto);
		return pagoRepository.save(pago);
	}

	public Pago obtenerPorId(Long id) {
		return pagoRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Pago no encontrado"));
	}

	public List<Pago> obtenerPorViaje(Long viajeId) {
		return pagoRepository.findByViajeId(viajeId);
	}

	public List<Pago> obtenerTodos() {
		return pagoRepository.findAll();
	}

	public void eliminar(Long id) {
		pagoRepository.deleteById(id);
	}

}
