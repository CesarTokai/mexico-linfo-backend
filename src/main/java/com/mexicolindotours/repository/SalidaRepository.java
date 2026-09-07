package com.mexicolindotours.repository;

import com.mexicolindotours.model.Salida;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface SalidaRepository extends JpaRepository<Salida, Long> {

	List<Salida> findByPaqueteIdOrderByFechaSalidaAsc(Long paqueteId);

	List<Salida> findByPaqueteIdAndEstadoAndFechaSalidaGreaterThanEqualOrderByFechaSalidaAsc(
			Long paqueteId, Salida.Estado estado, LocalDate desde);

	/** Proximas salidas de todo el catalogo, para la portada. */
	@Query("""
			SELECT s FROM Salida s
			WHERE s.estado = com.mexicolindotours.model.Salida$Estado.programada
			  AND s.paquete.activo = true
			  AND s.fechaSalida >= :desde
			ORDER BY s.fechaSalida ASC
			""")
	List<Salida> proximasSalidas(@Param("desde") LocalDate desde);

	/**
	 * Bloqueo pesimista: se usa al apartar asientos para que dos reservas
	 * simultaneas no puedan tomar el mismo ultimo lugar.
	 */
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT s FROM Salida s WHERE s.id = :id")
	Optional<Salida> findByIdBloqueando(@Param("id") Long id);

}
