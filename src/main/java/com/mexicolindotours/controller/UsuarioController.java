package com.mexicolindotours.controller;

import com.mexicolindotours.dto.*;
import com.mexicolindotours.model.Usuario;
import com.mexicolindotours.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/usuarios")
@PreAuthorize("hasRole('ADMIN')")
public class UsuarioController {

	@Autowired
	private UsuarioService usuarioService;

	@PostMapping
	public ResponseEntity<?> crear(@RequestBody UsuarioCreateRequest request) {
		try {
			Usuario.Rol rol = request.getRol() != null ? Usuario.Rol.valueOf(request.getRol()) : Usuario.Rol.GESTOR;
			Usuario u = usuarioService.crearUsuario(request.getNombre(), request.getEmail(), request.getPassword(), rol);
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
	public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody UsuarioUpdateRequest request) {
		try {
			Usuario.Rol rol = request.getRol() != null ? Usuario.Rol.valueOf(request.getRol()) : null;
			Usuario u = usuarioService.actualizarUsuario(id, request.getNombre(), null, rol);
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
