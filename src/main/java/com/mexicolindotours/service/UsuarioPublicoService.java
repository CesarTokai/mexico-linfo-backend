package com.mexicolindotours.service;

import com.mexicolindotours.model.UsuarioPublico;
import com.mexicolindotours.repository.UsuarioPublicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Optional;

/**
 * El registro rapido solo exige nombre, telefono y contrasena: el correo
 * es opcional y se completa despues (regla del dueno: familias que solo
 * manejan WhatsApp no tienen por que dar un correo para reservar).
 */
@Service
public class UsuarioPublicoService {

	private static final SecureRandom ALEATORIO = new SecureRandom();

	@Autowired
	private UsuarioPublicoRepository usuarioPublicoRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	public UsuarioPublico registrar(String nombre, String telefono, String password, String correo) {
		if (nombre == null || nombre.isBlank()) {
			throw new IllegalArgumentException("El nombre es obligatorio");
		}
		if (telefono == null || telefono.isBlank()) {
			throw new IllegalArgumentException("El teléfono es obligatorio");
		}
		if (password == null || password.length() < 8) {
			throw new IllegalArgumentException("La contraseña debe tener al menos 8 caracteres");
		}
		if (usuarioPublicoRepository.existsByTelefono(telefono)) {
			throw new IllegalArgumentException("Ese teléfono ya está registrado");
		}
		validarCorreoDisponible(correo, null);

		return usuarioPublicoRepository.save(
				new UsuarioPublico(nombre, telefono, passwordEncoder.encode(password), normalizarCorreo(correo)));
	}

	/**
	 * Para las reservas que el personal registra a mano (WhatsApp, walk-in):
	 * si ya existe una cuenta con ese telefono se reutiliza, si no se crea
	 * una con contrasena aleatoria. El cliente no necesita esa contrasena
	 * para nada salvo que despues quiera entrar por su cuenta; en ese caso
	 * usa recuperar-contrasena si dejo un correo, o pide ayuda al personal.
	 */
	public UsuarioPublico obtenerOCrearPorTelefono(String nombre, String telefono, String correo) {
		if (telefono == null || telefono.isBlank()) {
			throw new IllegalArgumentException("El teléfono es obligatorio");
		}

		Optional<UsuarioPublico> existente = usuarioPublicoRepository.findByTelefono(telefono);
		if (existente.isPresent()) {
			return existente.get();
		}

		if (nombre == null || nombre.isBlank()) {
			throw new IllegalArgumentException("El nombre es obligatorio para dar de alta al cliente");
		}
		validarCorreoDisponible(correo, null);

		String passwordAleatoria = Base64.getUrlEncoder().encodeToString(generarBytesAleatorios());
		return usuarioPublicoRepository.save(
				new UsuarioPublico(nombre, telefono, passwordEncoder.encode(passwordAleatoria), normalizarCorreo(correo)));
	}

	/** Completar el registro despues, cuando el cliente quiera agregar su correo. */
	public UsuarioPublico completarCorreo(Long usuarioId, String correo) {
		UsuarioPublico usuario = usuarioPublicoRepository.findById(usuarioId)
				.orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

		validarCorreoDisponible(correo, usuarioId);
		usuario.setCorreo(normalizarCorreo(correo));

		return usuarioPublicoRepository.save(usuario);
	}

	/** Login por correo O por telefono, lo que el cliente haya dado. */
	public Optional<UsuarioPublico> obtenerPorIdentificador(String identificador) {
		if (identificador == null || identificador.isBlank()) return Optional.empty();

		if (identificador.contains("@")) {
			return usuarioPublicoRepository.findByCorreoAndActivoTrue(identificador);
		}
		return usuarioPublicoRepository.findByTelefonoAndActivoTrue(identificador);
	}

	public Optional<UsuarioPublico> obtenerPorCorreo(String correo) {
		return usuarioPublicoRepository.findByCorreoAndActivoTrue(correo);
	}

	public Optional<UsuarioPublico> obtenerPorId(Long id) {
		return usuarioPublicoRepository.findById(id);
	}

	public boolean validarPassword(String password, String passwordHash) {
		return passwordEncoder.matches(password, passwordHash);
	}

	private void validarCorreoDisponible(String correo, Long usuarioIdActual) {
		if (correo == null || correo.isBlank()) return;
		if (!correo.contains("@")) {
			throw new IllegalArgumentException("Correo inválido");
		}
		Optional<UsuarioPublico> conEseCorreo = usuarioPublicoRepository.findByCorreo(correo);
		if (conEseCorreo.isPresent() && !conEseCorreo.get().getId().equals(usuarioIdActual)) {
			throw new IllegalArgumentException("Ese correo ya está registrado");
		}
	}

	private String normalizarCorreo(String correo) {
		return (correo == null || correo.isBlank()) ? null : correo;
	}

	private byte[] generarBytesAleatorios() {
		byte[] bytes = new byte[24];
		ALEATORIO.nextBytes(bytes);
		return bytes;
	}

}
