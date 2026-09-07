package com.mexicolindotours.repository;

import com.mexicolindotours.model.Mantenimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface MantenimientoRepository extends JpaRepository<Mantenimiento, Long> {

	List<Mantenimiento> findByCamionetaId(Long camionetaId);

	/** Ultimo servicio real de la unidad. Una refaccion NO reinicia el ciclo. */
	Optional<Mantenimiento> findTopByCamionetaIdAndTipoOrderByKmAlMomentoDesc(Long camionetaId, Mantenimiento.Tipo tipo);

}
