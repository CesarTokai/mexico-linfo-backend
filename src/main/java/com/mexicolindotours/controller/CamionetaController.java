package com.mexicolindotours.controller;

import com.mexicolindotours.dto.CamionetaDTO;
import com.mexicolindotours.model.Camioneta;
import com.mexicolindotours.service.CamionetaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/camionetas")
public class CamionetaController {

	@Autowired
	private CamionetaService camionetaService;

	@PostMapping
	public ResponseEntity<?> crear(@RequestParam String nombre,
								   @RequestParam String modelo,
								   @RequestParam Integer capacidad) {
		try {
			Camioneta c = camionetaService.crear(nombre, modelo, capacidad);
			return ResponseEntity.status(HttpStatus.CREATED).body(mapToDTO(c));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	@GetMapping
	public ResponseEntity<?> obtenerTodas() {
		List<Camioneta> camionetas = camionetaService.obtenerTodas();
		return ResponseEntity.ok(camionetas.stream().map(this::mapToDTO).collect(Collectors.toList()));
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
		return camionetaService.obtenerPorId(id)
				.map(c -> ResponseEntity.ok((Object) mapToDTO(c)))
				.orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body("Camioneta no encontrada"));
	}

	@PutMapping("/{id}")
	public ResponseEntity<?> actualizar(@PathVariable Long id,
										@RequestParam(required = false) String nombre,
										@RequestParam(required = false) String modelo,
										@RequestParam(required = false) Integer capacidad,
										@RequestParam(required = false) Camioneta.Estado estado,
										@RequestParam(required = false) Integer intervaloMantenimiento) {
		try {
			Camioneta c = camionetaService.actualizar(id, nombre, modelo, capacidad, estado, intervaloMantenimiento);
			return ResponseEntity.ok(mapToDTO(c));
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> desactivar(@PathVariable Long id) {
		try {
			camionetaService.desactivar(id);
			return ResponseEntity.ok("Camioneta desactivada");
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
	}

	private CamionetaDTO mapToDTO(Camioneta c) {
		return new CamionetaDTO(c.getId(), c.getNombre(), c.getModelo(), c.getCapacidad(),
				c.getKmActual(), c.getIntervaloMantenimientoKm(), c.getEstado().toString());
	}

}
