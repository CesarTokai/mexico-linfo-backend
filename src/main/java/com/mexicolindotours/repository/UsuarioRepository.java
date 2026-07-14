package com.mexicolindotours.repository;

import com.mexicolindotours.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

	Optional<Usuario> findByCorreo(String correo);

	Optional<Usuario> findByCorreoAndActivoTrue(String correo);

}
