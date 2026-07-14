package com.mexicolindotours.controller;

import com.mexicolindotours.dto.UsuarioDTO;
import com.mexicolindotours.model.Usuario;
import com.mexicolindotours.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

	@Autowired
	private UsuarioService usuarioService;

	@PostMapping
	public ResponseEntity<?> crear(@RequestParam String nombre,
								   @RequestParam String correo,
								   @RequestParam String password,
								   @RequestParam(defaultValue = "GESTOR") Usuario.Rol rol) {
		try {
			Usuario u = usuarioService.crearUsuario(nombre, correo, password, rol);
			return ResponseEntity.status(HttpStatus.CREATED).body(mapToDTO(u));
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	@GetMapping
	public ResponseEntity<?> obtenerTodos() {
		List<Usuario> usuarios = usuarioService.obtenerTodos();
		return ResponseEntity.ok(usuarios.stream().map(this::mapToDTO).collect(Collectors.toList()));
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
		return usuarioService.obtenerPorId(id)
				.map(u -> ResponseEntity.ok((Object) mapToDTO(u)))
				.orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado"));
	}

	@PutMapping("/{id}")
	public ResponseEntity<?> actualizar(@PathVariable Long id,
										@RequestParam(required = false) String nombre,
										@RequestParam(required = false) String correo,
										@RequestParam(required = false) Usuario.Rol rol) {
		try {
			Usuario u = usuarioService.actualizarUsuario(id, nombre, correo, rol);
			return ResponseEntity.ok(mapToDTO(u));
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> desactivar(@PathVariable Long id) {
		try {
			usuarioService.desactivarUsuario(id);
			return ResponseEntity.ok("Usuario desactivado");
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
	}

	private UsuarioDTO mapToDTO(Usuario u) {
		return new UsuarioDTO(u.getId(), u.getNombre(), u.getCorreo(), u.getRol().toString(), u.getActivo());
	}

}
