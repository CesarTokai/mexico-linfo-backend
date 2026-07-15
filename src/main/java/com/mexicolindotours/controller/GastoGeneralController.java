package com.mexicolindotours.controller;

import com.mexicolindotours.dto.GastoGeneralDTO;
import com.mexicolindotours.dto.GastoGeneralCreateRequest;
import com.mexicolindotours.model.GastoGeneral;
import com.mexicolindotours.service.GastoGeneralService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/gastos-generales")
public class GastoGeneralController {

	@Autowired
	private GastoGeneralService gastoGeneralService;

	@PostMapping
	public ResponseEntity<?> crear(@RequestBody GastoGeneralCreateRequest request) {
		try {
			GastoGeneral gg = gastoGeneralService.crear(request.getFecha(), request.getDescripcion(), request.getMonto());
			return ResponseEntity.status(HttpStatus.CREATED).body(mapToDTO(gg));
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	@GetMapping
	public ResponseEntity<?> obtenerTodos() {
		List<GastoGeneral> gastos = gastoGeneralService.obtenerTodos();
		return ResponseEntity.ok(gastos.stream().map(this::mapToDTO).collect(Collectors.toList()));
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
		try {
			GastoGeneral gg = gastoGeneralService.obtenerPorId(id);
			return ResponseEntity.ok(mapToDTO(gg));
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
	}

	@GetMapping("/mes")
	public ResponseEntity<?> obtenerPorMes(@RequestParam Integer mes, @RequestParam Integer anio) {
		List<GastoGeneral> gastos = gastoGeneralService.obtenerPorMes(mes, anio);
		return ResponseEntity.ok(gastos.stream().map(this::mapToDTO).collect(Collectors.toList()));
	}

	@GetMapping("/anio")
	public ResponseEntity<?> obtenerPorAnio(@RequestParam Integer anio) {
		List<GastoGeneral> gastos = gastoGeneralService.obtenerPorAnio(anio);
		return ResponseEntity.ok(gastos.stream().map(this::mapToDTO).collect(Collectors.toList()));
	}

	@PutMapping("/{id}")
	public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody GastoGeneralUpdateRequest request) {
		try {
			GastoGeneral gg = gastoGeneralService.actualizar(id, request.getFecha(), request.getDescripcion(), request.getMonto());
			return ResponseEntity.ok(mapToDTO(gg));
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> eliminar(@PathVariable Long id) {
		try {
			gastoGeneralService.eliminar(id);
			return ResponseEntity.ok("Gasto general eliminado");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	private GastoGeneralDTO mapToDTO(GastoGeneral gg) {
		return new GastoGeneralDTO(gg.getId(), gg.getFecha(), gg.getDescripcion(), gg.getMonto());
	}
}

class GastoGeneralUpdateRequest {
	private java.time.LocalDate fecha;
	private String descripcion;
	private java.math.BigDecimal monto;

	public GastoGeneralUpdateRequest() {}

	public java.time.LocalDate getFecha() { return fecha; }
	public void setFecha(java.time.LocalDate fecha) { this.fecha = fecha; }

	public String getDescripcion() { return descripcion; }
	public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

	public java.math.BigDecimal getMonto() { return monto; }
	public void setMonto(java.math.BigDecimal monto) { this.monto = monto; }
}
