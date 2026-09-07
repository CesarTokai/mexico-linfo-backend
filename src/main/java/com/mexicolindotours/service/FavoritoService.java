package com.mexicolindotours.service;

import com.mexicolindotours.model.Favorito;
import com.mexicolindotours.model.Paquete;
import com.mexicolindotours.model.UsuarioPublico;
import com.mexicolindotours.repository.FavoritoRepository;
import com.mexicolindotours.repository.PaqueteRepository;
import com.mexicolindotours.repository.UsuarioPublicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class FavoritoService {

	@Autowired
	private FavoritoRepository favoritoRepository;

	@Autowired
	private PaqueteRepository paqueteRepository;

	@Autowired
	private UsuarioPublicoRepository usuarioPublicoRepository;

	/** Idempotente: marcar dos veces el mismo paquete no duplica ni falla. */
	public Favorito agregar(Long usuarioPublicoId, Long paqueteId) {
		UsuarioPublico usuario = usuarioPublicoRepository.findById(usuarioPublicoId)
				.orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

		Paquete paquete = paqueteRepository.findById(paqueteId)
				.orElseThrow(() -> new IllegalArgumentException("Paquete no encontrado"));

		return favoritoRepository.findByUsuarioPublicoIdAndPaqueteId(usuarioPublicoId, paqueteId)
				.orElseGet(() -> favoritoRepository.save(new Favorito(usuario, paquete)));
	}

	public void quitar(Long usuarioPublicoId, Long paqueteId) {
		favoritoRepository.findByUsuarioPublicoIdAndPaqueteId(usuarioPublicoId, paqueteId)
				.ifPresent(favoritoRepository::delete);
	}

	public List<Favorito> listar(Long usuarioPublicoId) {
		return favoritoRepository.findByUsuarioPublicoIdOrderByIdDesc(usuarioPublicoId);
	}

	public boolean esFavorito(Long usuarioPublicoId, Long paqueteId) {
		return favoritoRepository.existsByUsuarioPublicoIdAndPaqueteId(usuarioPublicoId, paqueteId);
	}

}
