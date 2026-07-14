package com.mexicolindotours.service;

import com.mexicolindotours.model.Usuario;
import com.mexicolindotours.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	public Usuario crearUsuario(String nombre, String correo, String password, Usuario.Rol rol) {
		if (usuarioRepository.findByCorreo(correo).isPresent()) {
			throw new IllegalArgumentException("Correo ya registrado");
		}

		String passwordHash = passwordEncoder.encode(password);
		Usuario usuario = new Usuario(nombre, correo, passwordHash, rol);
		return usuarioRepository.save(usuario);
	}

	public Optional<Usuario> obtenerPorCorreo(String correo) {
		return usuarioRepository.findByCorreoAndActivoTrue(correo);
	}

	public Optional<Usuario> obtenerPorId(Long id) {
		return usuarioRepository.findById(id);
	}

	public List<Usuario> obtenerTodos() {
		return usuarioRepository.findAll();
	}

	public Usuario actualizarUsuario(Long id, String nombre, String correo, Usuario.Rol rol) {
		Usuario usuario = usuarioRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

		if (nombre != null) usuario.setNombre(nombre);
		if (correo != null) {
			if (usuarioRepository.findByCorreo(correo).isPresent() && !usuario.getCorreo().equals(correo)) {
				throw new IllegalArgumentException("Correo ya registrado");
			}
			usuario.setCorreo(correo);
		}
		if (rol != null) usuario.setRol(rol);

		usuario.setUpdatedAt(LocalDateTime.now());
		return usuarioRepository.save(usuario);
	}

	public boolean validarPassword(String password, String passwordHash) {
		return passwordEncoder.matches(password, passwordHash);
	}

	public Usuario desactivarUsuario(Long id) {
		Usuario usuario = usuarioRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
		usuario.setActivo(false);
		usuario.setUpdatedAt(LocalDateTime.now());
		return usuarioRepository.save(usuario);
	}

}
