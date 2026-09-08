package com.mexicolindotours.controller;

import com.mexicolindotours.dto.LoginPublicoRequest;
import com.mexicolindotours.dto.LoginPublicoResponse;
import com.mexicolindotours.dto.RegistroPublicoRequest;
import com.mexicolindotours.model.UsuarioPublico;
import com.mexicolindotours.security.JwtTokenProvider;
import com.mexicolindotours.service.RecuperacionPasswordService;
import com.mexicolindotours.service.UsuarioPublicoService;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Registro y login de los clientes del sitio. Rol emitido: CLIENTE.
 *
 * El registro rapido solo exige nombre, telefono y contrasena (las familias
 * que solo manejan WhatsApp no tienen correo que dar). El correo es
 * opcional y se completa despues desde el perfil.
 */
@RestController
@RequestMapping("/publico/auth")
public class PublicoAuthController {

	/** Rol de los usuarios del sitio. NO existe en el enum del personal. */
	public static final String ROL_CLIENTE = "CLIENTE";

	@Autowired
	private UsuarioPublicoService usuarioPublicoService;

	@Autowired
	private JwtTokenProvider jwtTokenProvider;

	@Autowired
	private RecuperacionPasswordService recuperacionPasswordService;

	@PostMapping("/registro")
	public ResponseEntity<?> registro(@RequestBody RegistroPublicoRequest request) {
		try {
			UsuarioPublico u = usuarioPublicoService.registrar(
					request.getNombre(), request.getTelefono(), request.getPassword(), request.getCorreo());

			// El sujeto del token es el telefono: es lo unico garantizado
			// presente (el correo puede faltar).
			String token = jwtTokenProvider.generateToken(u.getTelefono(), ROL_CLIENTE);
			return ResponseEntity.status(HttpStatus.CREATED)
					.body(new LoginPublicoResponse(token, u.getId(), u.getNombre(), u.getCorreo()));
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody LoginPublicoRequest request) {
		UsuarioPublico u = usuarioPublicoService.obtenerPorIdentificador(request.getIdentificador()).orElse(null);

		if (u == null || !usuarioPublicoService.validarPassword(request.getPassword(), u.getPasswordHash())) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales inválidas");
		}

		String token = jwtTokenProvider.generateToken(u.getTelefono(), ROL_CLIENTE);
		return ResponseEntity.ok(new LoginPublicoResponse(token, u.getId(), u.getNombre(), u.getCorreo()));
	}

	/**
	 * Pide el enlace para restablecer. SIEMPRE responde lo mismo, exista o no
	 * el correo: si dijera "no registrado" cualquiera podria averiguar quien
	 * tiene cuenta probando correos.
	 *
	 * Solo funciona si el cliente tiene correo registrado: sin eso no hay
	 * forma de mandarle un enlace (no se envian SMS). Si nunca dio correo,
	 * necesita ayuda del personal para recuperar el acceso.
	 */
	@PostMapping("/recuperar")
	public ResponseEntity<?> recuperar(@RequestBody Map<String, String> body) {
		recuperacionPasswordService.solicitar(body.get("correo"));
		return ResponseEntity.ok(
				"Si el correo está registrado, te enviamos un enlace para restablecer tu contraseña.");
	}

	@PostMapping("/restablecer")
	public ResponseEntity<?> restablecer(@RequestBody Map<String, String> body) {
		try {
			recuperacionPasswordService.restablecer(body.get("token"), body.get("password"));
			return ResponseEntity.ok("Contraseña actualizada. Ya puedes iniciar sesión.");
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

}
