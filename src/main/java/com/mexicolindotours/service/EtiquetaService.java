package com.mexicolindotours.service;

import com.mexicolindotours.model.Etiqueta;
import com.mexicolindotours.model.Post;
import com.mexicolindotours.repository.EtiquetaRepository;
import com.mexicolindotours.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class EtiquetaService {

	@Autowired
	private EtiquetaRepository etiquetaRepository;

	@Autowired
	private PostRepository postRepository;

	@Autowired
	private SlugService slugService;

	public Etiqueta crear(String nombre, String slug) {
		if (nombre == null || nombre.isBlank()) {
			throw new IllegalArgumentException("El nombre de la etiqueta es obligatorio");
		}

		String slugFinal = (slug != null && !slug.isBlank())
				? slugService.generar(slug)
				: slugService.generarUnico(nombre, etiquetaRepository::existsBySlug);

		if (etiquetaRepository.existsBySlug(slugFinal)) {
			throw new IllegalArgumentException("Ya existe una etiqueta con el slug: " + slugFinal);
		}

		return etiquetaRepository.save(new Etiqueta(nombre, slugFinal));
	}

	public List<Etiqueta> obtenerTodas() {
		return etiquetaRepository.findAll();
	}

	public Optional<Etiqueta> obtenerPorId(Long id) {
		return etiquetaRepository.findById(id);
	}

	public Etiqueta actualizar(Long id, String nombre) {
		Etiqueta etiqueta = etiquetaRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Etiqueta no encontrada"));

		if (nombre != null && !nombre.isBlank()) etiqueta.setNombre(nombre);

		return etiquetaRepository.save(etiqueta);
	}

	/**
	 * Al borrar una etiqueta NO se bloquea aunque tenga posts: se desprende de
	 * ellos. Una etiqueta es una clasificacion suelta, no una dependencia dura
	 * como la categoria.
	 */
	public void eliminar(Long id) {
		Etiqueta etiqueta = etiquetaRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Etiqueta no encontrada"));

		List<Post> posts = postRepository.findByEtiquetasId(id);
		for (Post p : posts) {
			p.getEtiquetas().removeIf(e -> e.getId().equals(id));
			postRepository.save(p);
		}

		etiquetaRepository.delete(etiqueta);
	}

	/** Resuelve una lista de ids a entidades, fallando si alguna no existe. */
	public Set<Etiqueta> resolver(List<Long> etiquetaIds) {
		Set<Etiqueta> etiquetas = new LinkedHashSet<>();
		if (etiquetaIds == null) return etiquetas;

		for (Long id : etiquetaIds) {
			if (id == null) continue;
			etiquetas.add(etiquetaRepository.findById(id)
					.orElseThrow(() -> new IllegalArgumentException("Etiqueta no encontrada: " + id)));
		}

		return etiquetas;
	}

	public long contarPostsPublicados(Long etiquetaId) {
		return postRepository.countByEstadoAndEtiquetasId(Post.Estado.publicado, etiquetaId);
	}

}
