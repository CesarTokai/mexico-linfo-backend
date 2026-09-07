package com.mexicolindotours.repository;

import com.mexicolindotours.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

	Optional<Post> findBySlug(String slug);

	boolean existsBySlug(String slug);

	Page<Post> findByEstado(Post.Estado estado, Pageable pageable);

	Page<Post> findByEstadoAndCategoriaSlug(Post.Estado estado, String categoriaSlug, Pageable pageable);

	Page<Post> findByEstadoAndEtiquetasSlug(Post.Estado estado, String etiquetaSlug, Pageable pageable);

	Page<Post> findByEstadoAndCategoriaSlugAndEtiquetasSlug(Post.Estado estado, String categoriaSlug, String etiquetaSlug, Pageable pageable);

	List<Post> findByEtiquetasId(Long etiquetaId);

	long countByEstadoAndEtiquetasId(Post.Estado estado, Long etiquetaId);

	Optional<Post> findBySlugAndEstado(String slug, Post.Estado estado);

	long countByEstadoAndCategoriaId(Post.Estado estado, Long categoriaId);

	long countByCategoriaId(Long categoriaId);

}
