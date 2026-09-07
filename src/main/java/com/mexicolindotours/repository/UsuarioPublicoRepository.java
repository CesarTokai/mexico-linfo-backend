package com.mexicolindotours.repository;

import com.mexicolindotours.model.UsuarioPublico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UsuarioPublicoRepository extends JpaRepository<UsuarioPublico, Long> {

	Optional<UsuarioPublico> findByCorreo(String correo);

	Optional<UsuarioPublico> findByCorreoAndActivoTrue(String correo);

	boolean existsByCorreo(String correo);

}
