package com.mexicolindotours.repository;

import com.mexicolindotours.model.Favorito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface FavoritoRepository extends JpaRepository<Favorito, Long> {

	List<Favorito> findByUsuarioPublicoIdOrderByIdDesc(Long usuarioPublicoId);

	Optional<Favorito> findByUsuarioPublicoIdAndPaqueteId(Long usuarioPublicoId, Long paqueteId);

	boolean existsByUsuarioPublicoIdAndPaqueteId(Long usuarioPublicoId, Long paqueteId);

	long countByPaqueteId(Long paqueteId);

}
