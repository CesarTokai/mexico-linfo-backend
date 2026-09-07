package com.mexicolindotours.repository;

import com.mexicolindotours.model.Paquete;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface PaqueteRepository extends JpaRepository<Paquete, Long> {

	Optional<Paquete> findBySlug(String slug);

	Optional<Paquete> findBySlugAndActivoTrue(String slug);

	boolean existsBySlug(String slug);

	/**
	 * Busqueda del catalogo: texto libre, destino, categoria y rango de fechas.
	 * Un paquete entra si tiene AL MENOS UNA salida programada dentro del rango.
	 * Todos los filtros son opcionales (null = no filtra).
	 */
	@Query("""
			SELECT DISTINCT p FROM Paquete p
			LEFT JOIN Salida s ON s.paquete = p AND s.estado = com.mexicolindotours.model.Salida$Estado.programada
			WHERE p.activo = true
			  AND (:texto IS NULL OR LOWER(p.titulo) LIKE LOWER(CONCAT('%', :texto, '%'))
			                      OR LOWER(p.destino) LIKE LOWER(CONCAT('%', :texto, '%')))
			  AND (:destino IS NULL OR LOWER(p.destino) = LOWER(:destino))
			  AND (:categoria IS NULL OR LOWER(p.categoria) = LOWER(:categoria))
			  AND (:desde IS NULL OR s.fechaSalida >= :desde)
			  AND (:hasta IS NULL OR s.fechaSalida <= :hasta)
			  AND ((:desde IS NULL AND :hasta IS NULL) OR s.id IS NOT NULL)
			""")
	Page<Paquete> buscar(@Param("texto") String texto,
						 @Param("destino") String destino,
						 @Param("categoria") String categoria,
						 @Param("desde") LocalDate desde,
						 @Param("hasta") LocalDate hasta,
						 Pageable pageable);

	@Query("SELECT DISTINCT p.destino FROM Paquete p WHERE p.activo = true ORDER BY p.destino")
	java.util.List<String> destinosDisponibles();

	@Query("SELECT DISTINCT p.categoria FROM Paquete p WHERE p.activo = true AND p.categoria IS NOT NULL ORDER BY p.categoria")
	java.util.List<String> categoriasDisponibles();

}
