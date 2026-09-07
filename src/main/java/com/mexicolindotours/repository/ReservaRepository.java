package com.mexicolindotours.repository;

import com.mexicolindotours.model.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {

	List<Reserva> findByUsuarioPublicoIdOrderByIdDesc(Long usuarioPublicoId);

	List<Reserva> findBySalidaId(Long salidaId);

	List<Reserva> findByEstadoOrderByIdDesc(Reserva.Estado estado);

	/**
	 * Reservas ya cobradas (confirmadas) cuya salida cae en un rango.
	 * Es el ingreso real de la venta por asiento.
	 */
	@Query("""
			SELECT r FROM Reserva r
			WHERE r.estado = com.mexicolindotours.model.Reserva$Estado.confirmada
			  AND r.salida.fechaSalida BETWEEN :desde AND :hasta
			""")
	List<Reserva> confirmadasEntre(@Param("desde") java.time.LocalDate desde,
								   @Param("hasta") java.time.LocalDate hasta);

	List<Reserva> findByEstado(Reserva.Estado estado);

	/** Reservas sin pagar creadas antes de un momento dado. */
	@Query("""
			SELECT r FROM Reserva r
			WHERE r.estado = com.mexicolindotours.model.Reserva$Estado.pendiente_pago
			  AND r.createdAt < :limite
			""")
	List<Reserva> pendientesVencidas(@Param("limite") java.time.LocalDateTime limite);

	/** Asientos ya comprometidos en una salida (todo lo que no esta cancelado). */
	@Query("""
			SELECT COALESCE(SUM(r.numAsientos), 0) FROM Reserva r
			WHERE r.salida.id = :salidaId
			  AND r.estado <> com.mexicolindotours.model.Reserva$Estado.cancelada
			""")
	int asientosOcupados(@Param("salidaId") Long salidaId);

}
