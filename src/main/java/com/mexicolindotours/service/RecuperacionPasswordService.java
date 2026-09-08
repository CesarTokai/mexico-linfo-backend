package com.mexicolindotours.service;

import com.mexicolindotours.model.TokenRecuperacion;
import com.mexicolindotours.model.UsuarioPublico;
import com.mexicolindotours.repository.TokenRecuperacionRepository;
import com.mexicolindotours.repository.UsuarioPublicoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Service
public class RecuperacionPasswordService {

	private static final Logger log = LoggerFactory.getLogger(RecuperacionPasswordService.class);
	private static final SecureRandom ALEATORIO = new SecureRandom();

	@Autowired
	private TokenRecuperacionRepository tokenRepository;

	@Autowired
	private UsuarioPublicoRepository usuarioPublicoRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private NotificacionService notificacionService;

	@Value("${app.recuperacion.minutos-vigencia:60}")
	private long minutosVigencia;

	/** Minutos que hay que esperar antes de poder pedir otro correo. */
	@Value("${app.recuperacion.minutos-entre-solicitudes:2}")
	private long minutosEntreSolicitudes;

	/**
	 * Genera el token y manda el correo.
	 *
	 * No revela si el correo existe: siempre se responde igual. Decir "ese
	 * correo no esta registrado" le regala a cualquiera una forma de
	 * averiguar quien tiene cuenta.
	 */
	@Transactional
	public void solicitar(String correo) {
		if (correo == null || correo.isBlank()) return;

		Optional<UsuarioPublico> posible = usuarioPublicoRepository.findByCorreoAndActivoTrue(correo);
		if (posible.isEmpty()) {
			log.info("Solicitud de recuperacion para un correo sin cuenta; no se hace nada");
			return;
		}

		UsuarioPublico usuario = posible.get();

		long recientes = tokenRepository.emitidosRecientes(
				usuario.getId(), LocalDateTime.now().minusMinutes(minutosEntreSolicitudes));
		if (recientes > 0) {
			log.info("Ya se envio un enlace de recuperacion hace poco a {}; no se reenvia", usuario.getId());
			return;
		}

		// Un token nuevo invalida los anteriores.
		invalidarPendientes(usuario.getId());

		String token = generarToken();
		TokenRecuperacion registro = new TokenRecuperacion(
				usuario, hashear(token), LocalDateTime.now().plusMinutes(minutosVigencia));
		tokenRepository.save(registro);

		notificacionService.recuperacionPassword(usuario, token, minutosVigencia);
	}

	/** Cambia la contrasena si el token es valido, no se uso y no expiro. */
	@Transactional
	public void restablecer(String token, String nuevaPassword) {
		if (token == null || token.isBlank()) {
			throw new IllegalArgumentException("Enlace inválido");
		}
		if (nuevaPassword == null || nuevaPassword.length() < 8) {
			throw new IllegalArgumentException("La contraseña debe tener al menos 8 caracteres");
		}

		TokenRecuperacion registro = tokenRepository.findByTokenHash(hashear(token))
				.orElseThrow(() -> new IllegalArgumentException("Enlace inválido o ya utilizado"));

		if (!registro.vigente()) {
			throw new IllegalArgumentException("El enlace expiró o ya se utilizó. Solicita uno nuevo.");
		}

		UsuarioPublico usuario = registro.getUsuarioPublico();
		usuario.setPasswordHash(passwordEncoder.encode(nuevaPassword));
		usuarioPublicoRepository.save(usuario);

		registro.setUsado(true);
		tokenRepository.save(registro);

		// Cualquier otro enlace pendiente deja de servir.
		invalidarPendientes(usuario.getId());

		log.info("Contrasena restablecida para el usuario publico {}", usuario.getId());
	}

	private void invalidarPendientes(Long usuarioId) {
		List<TokenRecuperacion> pendientes = tokenRepository.findByUsuarioPublicoIdAndUsadoFalse(usuarioId);
		for (TokenRecuperacion t : pendientes) {
			t.setUsado(true);
		}
		tokenRepository.saveAll(pendientes);
	}

	/** Limpieza diaria de tokens ya vencidos. */
	@Scheduled(fixedDelayString = "${app.recuperacion.intervalo-limpieza-ms:86400000}")
	@Transactional
	public void limpiarExpirados() {
		int borrados = tokenRepository.borrarExpirados(LocalDateTime.now().minusDays(1));
		if (borrados > 0) {
			log.info("Tokens de recuperacion expirados eliminados: {}", borrados);
		}
	}

	private String generarToken() {
		byte[] bytes = new byte[32];
		ALEATORIO.nextBytes(bytes);
		return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
	}

	private String hashear(String token) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
			StringBuilder sb = new StringBuilder(hash.length * 2);
			for (byte b : hash) {
				sb.append(String.format("%02x", b));
			}
			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException("SHA-256 no disponible", e);
		}
	}

}
