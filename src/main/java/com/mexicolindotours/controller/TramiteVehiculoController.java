package com.mexicolindotours.controller;

import com.mexicolindotours.dto.TramiteVehiculoDTO;
import com.mexicolindotours.dto.TramiteVehiculoCreateRequest;
import com.mexicolindotours.model.TramiteVehiculo;
import com.mexicolindotours.service.TramiteVehiculoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/tramites")
public class TramiteVehiculoController {

	@Autowired
	private TramiteVehiculoService tramiteVehiculoService;

	@PostMapping
	public ResponseEntity<?> crear(@RequestBody TramiteVehiculoCreateRequest request) {
		try {
			TramiteVehiculo t = tramiteVehiculoService.crear(request.getCamionetaId(),
														     TramiteVehiculo.Tipo.valueOf(request.getTipo()),
														     request.getFechaPago(), request.getMonto(),
														     request.getFechaVencimiento(), request.getNotas());
			return ResponseEntity.status(HttpStatus.CREATED).body(mapToDTO(t));
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	@GetMapping
	public ResponseEntity<?> obtenerTodos() {
		List<TramiteVehiculo> tramites = tramiteVehiculoService.obtenerTodos();
		return ResponseEntity.ok(tramites.stream().map(this::mapToDTO).collect(Collectors.toList()));
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
		try {
			TramiteVehiculo t = tramiteVehiculoService.obtenerPorId(id);
			return ResponseEntity.ok(mapToDTO(t));
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
	}

	@GetMapping("/camioneta/{camionetaId}")
	public ResponseEntity<?> obtenerPorCamioneta(@PathVariable Long camionetaId) {
		List<TramiteVehiculo> tramites = tramiteVehiculoService.obtenerPorCamioneta(camionetaId);
		return ResponseEntity.ok(tramites.stream().map(this::mapToDTO).collect(Collectors.toList()));
	}

	@PutMapping("/{id}")
	public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody TramiteVehiculoUpdateRequest request) {
		try {
			TramiteVehiculo t = tramiteVehiculoService.actualizar(id, request.getFechaVencimiento(),
																 request.getMonto(), request.getNotas());
			return ResponseEntity.ok(mapToDTO(t));
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> eliminar(@PathVariable Long id) {
		try {
			tramiteVehiculoService.eliminar(id);
			return ResponseEntity.ok("Trámite eliminado");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	private TramiteVehiculoDTO mapToDTO(TramiteVehiculo t) {
		return new TramiteVehiculoDTO(t.getId(), t.getCamioneta().getId(), t.getCamioneta().getNombre(),
									  t.getTipo().toString(), t.getFechaPago(), t.getMonto(),
									  t.getFechaVencimiento(), t.getNotas());
	}
}

class TramiteVehiculoUpdateRequest {
	private java.time.LocalDate fechaVencimiento;
	private java.math.BigDecimal monto;
	private String notas;

	public TramiteVehiculoUpdateRequest() {}

	public java.time.LocalDate getFechaVencimiento() { return fechaVencimiento; }
	public void setFechaVencimiento(java.time.LocalDate fechaVencimiento) { this.fechaVencimiento = fechaVencimiento; }

	public java.math.BigDecimal getMonto() { return monto; }
	public void setMonto(java.math.BigDecimal monto) { this.monto = monto; }

	public String getNotas() { return notas; }
	public void setNotas(String notas) { this.notas = notas; }
}
