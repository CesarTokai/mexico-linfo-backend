package com.mexicolindotours.controller;

import com.mexicolindotours.dto.HistorialChoferDTO;
import com.mexicolindotours.dto.HistorialClienteDTO;
import com.mexicolindotours.dto.HistorialCamionetaDTO;
import com.mexicolindotours.service.HistorialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HistorialController {

	@Autowired
	private HistorialService historialService;

	@GetMapping("/choferes/{id}/historial")
	public ResponseEntity<?> obtenerHistorialChofer(@PathVariable Long id) {
		try {
			HistorialChoferDTO historial = historialService.obtenerHistorialChofer(id);
			return ResponseEntity.ok(historial);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
	}

	@GetMapping("/clientes/{id}/historial")
	public ResponseEntity<?> obtenerHistorialCliente(@PathVariable Long id) {
		try {
			HistorialClienteDTO historial = historialService.obtenerHistorialCliente(id);
			return ResponseEntity.ok(historial);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
	}

	@GetMapping("/camionetas/{id}/historial")
	public ResponseEntity<?> obtenerHistorialCamioneta(@PathVariable Long id) {
		try {
			HistorialCamionetaDTO historial = historialService.obtenerHistorialCamioneta(id);
			return ResponseEntity.ok(historial);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
	}

}
