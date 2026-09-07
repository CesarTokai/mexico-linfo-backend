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
	 * Salidas NO canceladas de una camioneta que se traslapan con un rango.
	 * Es lo que impide vender la misma unidad dos veces entre el sitio
	 * publico y las rentas privadas.
	 */
	@Query("""
			SELECT s FROM Salida s
			WHERE s.camioneta.id = :camionetaId
			  AND s.estado <> com.mexicolindotours.model.Salida$Estado.cancelada
			  AND s.fechaSalida <= :hasta
			  AND s.fechaRegreso >= :desde
			""")
	List<Salida> ocupacionDeCamioneta(@Param("camionetaId") Long camionetaId,
									  @Param("desde") LocalDate desde,
									  @Param("hasta") LocalDate hasta);

	List<Salida> findByCamionetaIdAndEstadoNot(Long camionetaId, Salida.Estado estado);

	/**
	 * Bloqueo pesimista: se usa al apartar asientos para que dos reservas
	 * simultaneas no puedan tomar el mismo ultimo lugar.
	 */
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT s FROM Salida s WHERE s.id = :id")
	Optional<Salida> findByIdBloqueando(@Param("id") Long id);

}
