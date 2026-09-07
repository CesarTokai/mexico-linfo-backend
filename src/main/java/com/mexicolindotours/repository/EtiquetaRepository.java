package com.mexicolindotours.repository;

import com.mexicolindotours.model.Etiqueta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface EtiquetaRepository extends JpaRepository<Etiqueta, Long> {

	Optional<Etiqueta> findBySlug(String slug);

	boolean existsBySlug(String slug);

}
