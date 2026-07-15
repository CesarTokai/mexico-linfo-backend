package com.mexicolindotours.controller;

import com.mexicolindotours.dto.MantenimientoDTO;
import com.mexicolindotours.dto.MantenimientoCreateRequest;
import com.mexicolindotours.model.Mantenimiento;
import com.mexicolindotours.service.MantenimientoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/mantenimientos")
public class MantenimientoController {

	@Autowired
	private MantenimientoService mantenimientoService;

	@PostMapping
	public ResponseEntity<?> crear(@RequestBody MantenimientoCreateRequest request) {
		try {
			Mantenimiento m = mantenimientoService.crear(request.getCamionetaId(), request.getFecha(),
														  Mantenimiento.Tipo.valueOf(request.getTipo()),
														  request.getCosto(), request.getDescripcion());
			return ResponseEntity.status(HttpStatus.CREATED).body(mapToDTO(m));
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	@GetMapping
	public ResponseEntity<?> obtenerTodos() {
		List<Mantenimiento> mantenimientos = mantenimientoService.obtenerTodos();
		return ResponseEntity.ok(mantenimientos.stream().map(this::mapToDTO).collect(Collectors.toList()));
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
		try {
			Mantenimiento m = mantenimientoService.obtenerPorId(id);
			return ResponseEntity.ok(mapToDTO(m));
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
	}

	@GetMapping("/camioneta/{camionetaId}")
	public ResponseEntity<?> obtenerPorCamioneta(@PathVariable Long camionetaId) {
		List<Mantenimiento> mantenimientos = mantenimientoService.obtenerPorCamioneta(camionetaId);
		return ResponseEntity.ok(mantenimientos.stream().map(this::mapToDTO).collect(Collectors.toList()));
	}

	@PutMapping("/{id}")
	public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody MantenimientoUpdateRequest request) {
		try {
			Mantenimiento m = mantenimientoService.actualizar(id, request.getFecha(), request.getCosto(), request.getDescripcion());
			return ResponseEntity.ok(mapToDTO(m));
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> eliminar(@PathVariable Long id) {
		try {
			mantenimientoService.eliminar(id);
			return ResponseEntity.ok("Mantenimiento eliminado");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	private MantenimientoDTO mapToDTO(Mantenimiento m) {
		return new MantenimientoDTO(m.getId(), m.getCamioneta().getId(), m.getCamioneta().getNombre(),
									m.getFecha(), m.getKmAlMomento(), m.getTipo().toString(),
									m.getDescripcion(), m.getCosto());
	}
}

class MantenimientoUpdateRequest {
	private java.time.LocalDate fecha;
	private java.math.BigDecimal costo;
	private String descripcion;

	public MantenimientoUpdateRequest() {}

	public java.time.LocalDate getFecha() { return fecha; }
	public void setFecha(java.time.LocalDate fecha) { this.fecha = fecha; }

	public java.math.BigDecimal getCosto() { return costo; }
	public void setCosto(java.math.BigDecimal costo) { this.costo = costo; }

	public String getDescripcion() { return descripcion; }
	public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
}
