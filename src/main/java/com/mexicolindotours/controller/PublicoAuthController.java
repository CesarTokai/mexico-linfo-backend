package com.mexicolindotours.controller;

import com.mexicolindotours.dto.LoginPublicoResponse;
import com.mexicolindotours.dto.LoginRequest;
import com.mexicolindotours.dto.RegistroPublicoRequest;
import com.mexicolindotours.model.UsuarioPublico;
import com.mexicolindotours.security.JwtTokenProvider;
import com.mexicolindotours.service.UsuarioPublicoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/** Registro y login de los clientes del sitio. Rol emitido: CLIENTE. */
@RestController
@RequestMapping("/publico/auth")
public class PublicoAuthController {

	/** Rol de los usuarios del sitio. NO existe en el enum del personal. */
	public static final String ROL_CLIENTE = "CLIENTE";

	@Autowired
	private UsuarioPublicoService usuarioPublicoService;

	@Autowired
	private JwtTokenProvider jwtTokenProvider;

	@PostMapping("/registro")
	public ResponseEntity<?> registro(@RequestBody RegistroPublicoRequest request) {
		try {
			UsuarioPublico u = usuarioPublicoService.registrar(
					request.getNombre(), request.getCorreo(), request.getPassword(), request.getTelefono());

			String token = jwtTokenProvider.generateToken(u.getCorreo(), ROL_CLIENTE);
			return ResponseEntity.status(HttpStatus.CREATED)
					.body(new LoginPublicoResponse(token, u.getId(), u.getNombre(), u.getCorreo()));
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody LoginRequest request) {
		UsuarioPublico u = usuarioPublicoService.obtenerPorCorreo(request.getCorreo()).orElse(null);

		if (u == null || !usuarioPublicoService.validarPassword(request.getPassword(), u.getPasswordHash())) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales inválidas");
		}

		String token = jwtTokenProvider.generateToken(u.getCorreo(), ROL_CLIENTE);
		return ResponseEntity.ok(new LoginPublicoResponse(token, u.getId(), u.getNombre(), u.getCorreo()));
	}

}
