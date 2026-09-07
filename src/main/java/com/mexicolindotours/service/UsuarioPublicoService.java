package com.mexicolindotours.service;

import com.mexicolindotours.model.UsuarioPublico;
import com.mexicolindotours.repository.UsuarioPublicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class UsuarioPublicoService {

	@Autowired
	private UsuarioPublicoRepository usuarioPublicoRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	public UsuarioPublico registrar(String nombre, String correo, String password, String telefono) {
		if (nombre == null || nombre.isBlank()) {
			throw new IllegalArgumentException("El nombre es obligatorio");
		}
		if (correo == null || !correo.contains("@")) {
			throw new IllegalArgumentException("Correo inválido");
		}
		if (password == null || password.length() < 8) {
			throw new IllegalArgumentException("La contraseña debe tener al menos 8 caracteres");
		}
		if (usuarioPublicoRepository.existsByCorreo(correo)) {
			throw new IllegalArgumentException("Ese correo ya está registrado");
		}

		return usuarioPublicoRepository.save(
				new UsuarioPublico(nombre, correo, passwordEncoder.encode(password), telefono));
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

}
