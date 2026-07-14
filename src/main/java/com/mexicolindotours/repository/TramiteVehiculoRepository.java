package com.mexicolindotours.repository;

import com.mexicolindotours.model.TramiteVehiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TramiteVehiculoRepository extends JpaRepository<TramiteVehiculo, Long> {

	List<TramiteVehiculo> findByCamionetaId(Long camionetaId);

}
