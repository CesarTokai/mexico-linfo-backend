package com.mexicolindotours.service;

import com.mexicolindotours.model.Gasto;
import com.mexicolindotours.model.Viaje;
import com.mexicolindotours.repository.GastoRepository;
import com.mexicolindotours.repository.ViajeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class GastoService {

	@Autowired
	private GastoRepository gastoRepository;

	@Autowired
	private ViajeRepository viajeRepository;

	public Gasto crear(Long viajeId, Gasto.Tipo tipo, String descripcion, BigDecimal monto) {
		return crear(viajeId, tipo, LocalDate.now(), monto, descripcion, null);
	}

	public Gasto crear(Long viajeId, Gasto.Tipo tipo, LocalDate fecha, BigDecimal monto, String notas) {
		return crear(viajeId, tipo, fecha, monto, null, notas);
	}

	public Gasto crear(Long viajeId, Gasto.Tipo tipo, LocalDate fecha, BigDecimal monto, String descripcion, String notas) {
		Viaje viaje = viajeRepository.findById(viajeId)
				.orElseThrow(() -> new IllegalArgumentException("Viaje no encontrado"));

		Gasto gasto = new Gasto(viaje, tipo, fecha, monto);
		if (descripcion != null) {
			gasto.setDescripcion(descripcion);
		}
		if (notas != null) {
			gasto.setNotas(notas);
		}
		return gastoRepository.save(gasto);
	}

	public Gasto obtenerPorId(Long id) {
		return gastoRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Gasto no encontrado"));
	}

	public List<Gasto> obtenerPorViaje(Long viajeId) {
		return gastoRepository.findByViajeId(viajeId);
	}

	public List<Gasto> obtenerTodos() {
		return gastoRepository.findAll();
	}

	public void eliminar(Long id) {
		gastoRepository.deleteById(id);
	}

}
