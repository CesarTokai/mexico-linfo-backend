package com.mexicolindotours.controller;

import com.mexicolindotours.dto.LoginRequest;
import com.mexicolindotours.dto.LoginResponse;
import com.mexicolindotours.model.Usuario;
import com.mexicolindotours.security.JwtTokenProvider;
import com.mexicolindotours.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

	@Autowired
	private UsuarioService usuarioService;

	@Autowired
	private JwtTokenProvider jwtTokenProvider;

	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody LoginRequest request) {
		Usuario usuario = usuarioService.obtenerPorCorreo(request.getCorreo())
				.orElse(null);

		if (usuario == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body("Credenciales inválidas");
		}

		if (!usuarioService.validarPassword(request.getPassword(), usuario.getPasswordHash())) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body("Credenciales inválidas");
		}

		String token = jwtTokenProvider.generateToken(usuario.getCorreo(), usuario.getRol().toString());
		LoginResponse response = new LoginResponse(token, usuario.getNombre(), usuario.getCorreo(), usuario.getRol().toString());

		return ResponseEntity.ok(response);
	}

	@PostMapping("/crear-usuario")
	public ResponseEntity<?> crearUsuario(@RequestParam String nombre,
										  @RequestParam String correo,
										  @RequestParam String password,
										  @RequestParam(defaultValue = "GESTOR") Usuario.Rol rol) {
		try {
			Usuario usuario = usuarioService.crearUsuario(nombre, correo, password, rol);
			return ResponseEntity.status(HttpStatus.CREATED)
					.body("Usuario creado: " + usuario.getNombre());
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(e.getMessage());
		}
	}

}
