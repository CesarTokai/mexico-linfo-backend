package com.mexicolindotours.service;

import com.mexicolindotours.model.CategoriaPost;
import com.mexicolindotours.model.Post;
import com.mexicolindotours.repository.CategoriaPostRepository;
import com.mexicolindotours.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PostService {

	@Autowired
	private PostRepository postRepository;

	@Autowired
	private CategoriaPostRepository categoriaPostRepository;

	@Autowired
	private SlugService slugService;

	@Autowired
	private EtiquetaService etiquetaService;

	public Post crear(String titulo, String slug, String resumen, String contenido, String imagenUrl,
					  String autor, Long categoriaId, List<Long> etiquetaIds, String estado, LocalDate fechaPublicacion) {

		if (titulo == null || titulo.isBlank()) {
			throw new IllegalArgumentException("El título es obligatorio");
		}
		if (contenido == null || contenido.isBlank()) {
			throw new IllegalArgumentException("El contenido es obligatorio");
		}
		if (autor == null || autor.isBlank()) {
			throw new IllegalArgumentException("El autor es obligatorio");
		}

		String slugFinal = (slug != null && !slug.isBlank())
				? slugService.generar(slug)
				: slugService.generarUnico(titulo, postRepository::existsBySlug);

		if (postRepository.existsBySlug(slugFinal)) {
			throw new IllegalArgumentException("Ya existe un post con el slug: " + slugFinal);
		}

		Post post = new Post(titulo, slugFinal, contenido, autor);
		post.setResumen(resumen);
		post.setImagenUrl(imagenUrl);
		post.setCategoria(resolverCategoria(categoriaId));
		post.setEtiquetas(etiquetaService.resolver(etiquetaIds));
		aplicarEstado(post, estado, fechaPublicacion);

		return postRepository.save(post);
	}

	public Post actualizar(Long id, String titulo, String slug, String resumen, String contenido, String imagenUrl,
						   String autor, Long categoriaId, List<Long> etiquetaIds, String estado, LocalDate fechaPublicacion) {

		Post post = postRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Post no encontrado"));

		if (titulo != null && !titulo.isBlank()) post.setTitulo(titulo);
		if (resumen != null) post.setResumen(resumen);
		if (contenido != null && !contenido.isBlank()) post.setContenido(contenido);
		if (imagenUrl != null) post.setImagenUrl(imagenUrl);
		if (autor != null && !autor.isBlank()) post.setAutor(autor);
		if (categoriaId != null) post.setCategoria(resolverCategoria(categoriaId));
		// null = no tocar las etiquetas; lista vacia = quitarlas todas.
		if (etiquetaIds != null) post.setEtiquetas(etiquetaService.resolver(etiquetaIds));

		if (slug != null && !slug.isBlank()) {
			String slugFinal = slugService.generar(slug);
			Optional<Post> conEseSlug = postRepository.findBySlug(slugFinal);
			if (conEseSlug.isPresent() && !conEseSlug.get().getId().equals(post.getId())) {
				throw new IllegalArgumentException("Ya existe otro post con el slug: " + slugFinal);
			}
			post.setSlug(slugFinal);
		}

		if (estado != null) {
			aplicarEstado(post, estado, fechaPublicacion);
		} else if (fechaPublicacion != null) {
			post.setFechaPublicacion(fechaPublicacion);
		}

		post.setUpdatedAt(LocalDateTime.now());
		return postRepository.save(post);
	}

	public void eliminar(Long id) {
		Post post = postRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Post no encontrado"));
		postRepository.delete(post);
	}

	/** Listado publico: solo publicados, mas recientes primero. */
	public Page<Post> listarPublicados(String categoriaSlug, String etiquetaSlug, int pagina, int tamano) {
		Pageable pageable = PageRequest.of(pagina, tamano,
				Sort.by(Sort.Direction.DESC, "fechaPublicacion").and(Sort.by(Sort.Direction.DESC, "id")));

		boolean hayCategoria = categoriaSlug != null && !categoriaSlug.isBlank();
		boolean hayEtiqueta = etiquetaSlug != null && !etiquetaSlug.isBlank();

		if (hayCategoria && hayEtiqueta) {
			return postRepository.findByEstadoAndCategoriaSlugAndEtiquetasSlug(Post.Estado.publicado, categoriaSlug, etiquetaSlug, pageable);
		}
		if (hayCategoria) {
			return postRepository.findByEstadoAndCategoriaSlug(Post.Estado.publicado, categoriaSlug, pageable);
		}
		if (hayEtiqueta) {
			return postRepository.findByEstadoAndEtiquetasSlug(Post.Estado.publicado, etiquetaSlug, pageable);
		}

		return postRepository.findByEstado(Post.Estado.publicado, pageable);
	}

	public Optional<Post> obtenerPublicadoPorSlug(String slug) {
		return postRepository.findBySlugAndEstado(slug, Post.Estado.publicado);
	}

	/** Listado administrativo: incluye borradores. */
	public Page<Post> listarTodos(int pagina, int tamano) {
		Pageable pageable = PageRequest.of(pagina, tamano, Sort.by(Sort.Direction.DESC, "id"));
		return postRepository.findAll(pageable);
	}

	public Optional<Post> obtenerPorId(Long id) {
		return postRepository.findById(id);
	}

	private CategoriaPost resolverCategoria(Long categoriaId) {
		if (categoriaId == null) return null;
		return categoriaPostRepository.findById(categoriaId)
				.orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada"));
	}

	private void aplicarEstado(Post post, String estado, LocalDate fechaPublicacion) {
		Post.Estado nuevoEstado = post.getEstado();

		if (estado != null && !estado.isBlank()) {
			try {
				nuevoEstado = Post.Estado.valueOf(estado);
			} catch (IllegalArgumentException e) {
				throw new IllegalArgumentException("Estado inválido: " + estado + " (usa borrador o publicado)");
			}
		}

		post.setEstado(nuevoEstado);

		if (fechaPublicacion != null) {
			post.setFechaPublicacion(fechaPublicacion);
		} else if (nuevoEstado == Post.Estado.publicado && post.getFechaPublicacion() == null) {
			// Al publicar por primera vez sin fecha explicita, se sella con la de hoy.
			post.setFechaPublicacion(LocalDate.now());
		}
	}

}
