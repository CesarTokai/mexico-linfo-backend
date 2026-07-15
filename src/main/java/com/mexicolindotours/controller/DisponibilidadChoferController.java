package com.mexicolindotours.controller;

import com.mexicolindotours.dto.*;
import com.mexicolindotours.model.DisponibilidadChofer;
import com.mexicolindotours.service.DisponibilidadChoferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/choferes")
public class DisponibilidadChoferController {

	@Autowired
	private DisponibilidadChoferService disponibilidadService;

	@PostMapping("/{id}/disponibilidad")
	public ResponseEntity<?> crearOActualizar(@PathVariable Long id, @RequestBody DisponibilidadChoferCreateRequest request) {
		try {
			DisponibilidadChofer d = disponibilidadService.crearOActualizar(id, request.getFecha(), request.getDisponible(), request.getNotas());
			return ResponseEntity.status(HttpStatus.CREATED).body(mapToDTO(d));
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	@GetMapping("/{id}/disponibilidad")
	public ResponseEntity<?> obtenerDisponibilidad(@PathVariable Long id) {
		List<DisponibilidadChofer> disponibilidades = disponibilidadService.obtenerPorChofer(id);
		return ResponseEntity.ok(disponibilidades.stream().map(this::mapToDTO).collect(Collectors.toList()));
	}

	@DeleteMapping("/disponibilidad/{disponibilidadId}")
	public ResponseEntity<?> eliminar(@PathVariable Long disponibilidadId) {
		try {
			disponibilidadService.eliminar(disponibilidadId);
			return ResponseEntity.ok("Disponibilidad eliminada");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	private DisponibilidadChoferDTO mapToDTO(DisponibilidadChofer d) {
		return new DisponibilidadChoferDTO(d.getId(), d.getChofer().getId(), d.getFecha(), d.getDisponible(), d.getNotas());
	}

}
