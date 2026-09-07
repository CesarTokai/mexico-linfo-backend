package com.mexicolindotours.service;

import com.mexicolindotours.model.CategoriaPost;
import com.mexicolindotours.repository.CategoriaPostRepository;
import com.mexicolindotours.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class CategoriaPostService {

	@Autowired
	private CategoriaPostRepository categoriaPostRepository;

	@Autowired
	private PostRepository postRepository;

	@Autowired
	private SlugService slugService;

	public CategoriaPost crear(String nombre, String slug, String descripcion) {
		if (nombre == null || nombre.isBlank()) {
			throw new IllegalArgumentException("El nombre de la categoría es obligatorio");
		}

		String slugFinal = (slug != null && !slug.isBlank())
				? slugService.generar(slug)
				: slugService.generarUnico(nombre, categoriaPostRepository::existsBySlug);

		if (categoriaPostRepository.existsBySlug(slugFinal)) {
			throw new IllegalArgumentException("Ya existe una categoría con el slug: " + slugFinal);
		}

		return categoriaPostRepository.save(new CategoriaPost(nombre, slugFinal, descripcion));
	}

	public List<CategoriaPost> obtenerTodas() {
		return categoriaPostRepository.findAll();
	}

	public Optional<CategoriaPost> obtenerPorId(Long id) {
		return categoriaPostRepository.findById(id);
	}

	public CategoriaPost actualizar(Long id, String nombre, String descripcion) {
		CategoriaPost categoria = categoriaPostRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada"));

		if (nombre != null && !nombre.isBlank()) categoria.setNombre(nombre);
		if (descripcion != null) categoria.setDescripcion(descripcion);

		return categoriaPostRepository.save(categoria);
	}

	public void eliminar(Long id) {
		CategoriaPost categoria = categoriaPostRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada"));

		// Sin baja logica: si tiene posts, se bloquea para no dejarlos huerfanos.
		long posts = postRepository.countByCategoriaId(categoria.getId());
		if (posts > 0) {
			throw new IllegalArgumentException("No se puede eliminar: la categoría tiene " + posts + " post(s)");
		}

		categoriaPostRepository.delete(categoria);
	}

	/** Conteo de posts PUBLICADOS, que es lo que muestra el sidebar del blog. */
	public long contarPostsPublicados(Long categoriaId) {
		return postRepository.countByEstadoAndCategoriaId(com.mexicolindotours.model.Post.Estado.publicado, categoriaId);
	}

}
