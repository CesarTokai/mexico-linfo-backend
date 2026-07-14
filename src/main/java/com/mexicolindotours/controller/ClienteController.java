package com.mexicolindotours.controller;

import com.mexicolindotours.dto.ClienteDTO;
import com.mexicolindotours.model.Cliente;
import com.mexicolindotours.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/clientes")
public class ClienteController {

	@Autowired
	private ClienteService clienteService;

	@PostMapping
	public ResponseEntity<?> crear(@RequestParam String nombre,
								   @RequestParam(required = false) String telefono,
								   @RequestParam(required = false) String notas) {
		try {
			Cliente cl = clienteService.crear(nombre, telefono, notas);
			return ResponseEntity.status(HttpStatus.CREATED).body(mapToDTO(cl));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	@GetMapping
	public ResponseEntity<?> obtenerTodos() {
		List<Cliente> clientes = clienteService.obtenerTodos();
		return ResponseEntity.ok(clientes.stream().map(this::mapToDTO).collect(Collectors.toList()));
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
		return clienteService.obtenerPorId(id)
				.map(cl -> ResponseEntity.ok((Object) mapToDTO(cl)))
				.orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cliente no encontrado"));
	}

	@PutMapping("/{id}")
	public ResponseEntity<?> actualizar(@PathVariable Long id,
										@RequestParam(required = false) String nombre,
										@RequestParam(required = false) String telefono,
										@RequestParam(required = false) String notas) {
		try {
			Cliente cl = clienteService.actualizar(id, nombre, telefono, notas);
			return ResponseEntity.ok(mapToDTO(cl));
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
	}

	private ClienteDTO mapToDTO(Cliente cl) {
		return new ClienteDTO(cl.getId(), cl.getNombre(), cl.getTelefono(), cl.getNotas());
	}

}
