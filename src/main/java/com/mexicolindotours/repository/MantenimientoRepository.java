package com.mexicolindotours.repository;

import com.mexicolindotours.model.Mantenimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MantenimientoRepository extends JpaRepository<Mantenimiento, Long> {

	List<Mantenimiento> findByCamionetaId(Long camionetaId);

}
