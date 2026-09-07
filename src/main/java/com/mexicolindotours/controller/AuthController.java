package com.mexicolindotours.controller;

import com.mexicolindotours.dto.LoginRequest;
import com.mexicolindotours.dto.LoginResponse;
import com.mexicolindotours.model.Usuario;
import com.mexicolindotours.security.JwtTokenProvider;
import com.mexicolindotours.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
		boolean esBootstrap = !usuarioService.hayUsuarios();

		if (!esBootstrap && !esAdminAutenticado()) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN)
					.body("Solo ADMIN puede crear usuarios");
		}

		// El primer usuario SIEMPRE es ADMIN: si se creara como GESTOR, nadie
		// podria administrar y la instalacion quedaria bloqueada otra vez.
		Usuario.Rol rolFinal = esBootstrap ? Usuario.Rol.ADMIN : rol;

		try {
			Usuario usuario = usuarioService.crearUsuario(nombre, correo, password, rolFinal);
			return ResponseEntity.status(HttpStatus.CREATED)
					.body("Usuario creado: " + usuario.getNombre() + " (" + rolFinal + ")");
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(e.getMessage());
		}
	}

	private boolean esAdminAutenticado() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null) return false;
		return auth.getAuthorities().stream()
				.anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
	}

}
