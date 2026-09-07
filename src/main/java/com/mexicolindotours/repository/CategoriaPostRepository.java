package com.mexicolindotours.repository;

import com.mexicolindotours.model.CategoriaPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CategoriaPostRepository extends JpaRepository<CategoriaPost, Long> {

	Optional<CategoriaPost> findBySlug(String slug);

	boolean existsBySlug(String slug);

}
