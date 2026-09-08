package com.mexicolindotours.repository;

import com.mexicolindotours.model.TokenRecuperacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRecuperacionRepository extends JpaRepository<TokenRecuperacion, Long> {

	Optional<TokenRecuperacion> findByTokenHash(String tokenHash);

	List<TokenRecuperacion> findByUsuarioPublicoIdAndUsadoFalse(Long usuarioPublicoId);

	/** Para no permitir que alguien pida un correo tras otro. */
	@Query("""
			SELECT COUNT(t) FROM TokenRecuperacion t
			WHERE t.usuarioPublico.id = :usuarioId
			  AND t.usado = false
			  AND t.createdAt > :desde
			""")
	long emitidosRecientes(@Param("usuarioId") Long usuarioId, @Param("desde") LocalDateTime desde);

	@Modifying
	@Query("DELETE FROM TokenRecuperacion t WHERE t.expiraEn < :limite")
	int borrarExpirados(@Param("limite") LocalDateTime limite);

}
