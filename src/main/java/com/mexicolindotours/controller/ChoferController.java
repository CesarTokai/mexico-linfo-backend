package com.mexicolindotours.controller;

import com.mexicolindotours.dto.*;
import com.mexicolindotours.model.Chofer;
import com.mexicolindotours.service.ChoferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/choferes")
public class ChoferController {

	@Autowired
	private ChoferService choferService;

	@PostMapping
	public ResponseEntity<?> crear(@RequestBody ChoferCreateRequest request) {
		try {
			Chofer ch = choferService.crear(request.getNombre(), request.getTelefono());
			return ResponseEntity.status(HttpStatus.CREATED).body(mapToDTO(ch));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	@GetMapping
	public ResponseEntity<?> obtenerActivos() {
		List<Chofer> choferes = choferService.obtenerActivos();
		return ResponseEntity.ok(choferes.stream().map(this::mapToDTO).collect(Collectors.toList()));
	}

	@GetMapping("/todos")
	public ResponseEntity<?> obtenerTodos() {
		List<Chofer> choferes = choferService.obtenerTodos();
		return ResponseEntity.ok(choferes.stream().map(this::mapToDTO).collect(Collectors.toList()));
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
		return choferService.obtenerPorId(id)
				.map(ch -> ResponseEntity.ok((Object) mapToDTO(ch)))
				.orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body("Chofer no encontrado"));
	}

	@PutMapping("/{id}")
	public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody ChoferUpdateRequest request) {
		try {
			Chofer ch = choferService.actualizar(id, request.getNombre(), request.getTelefono(), request.getLicenciaVencimiento());
			return ResponseEntity.ok(mapToDTO(ch));
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> desactivar(@PathVariable Long id) {
		try {
			choferService.desactivar(id);
			return ResponseEntity.ok("Chofer desactivado");
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
	}

	private ChoferDTO mapToDTO(Chofer ch) {
		return new ChoferDTO(ch.getId(), ch.getNombre(), ch.getTelefono(), ch.getLicenciaVencimiento(), ch.getActivo());
	}

}
