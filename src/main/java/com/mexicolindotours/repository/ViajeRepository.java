package com.mexicolindotours.repository;

import com.mexicolindotours.model.Viaje;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ViajeRepository extends JpaRepository<Viaje, Long> {

	List<Viaje> findByCamionetaId(Long camionetaId);

	List<Viaje> findByClienteId(Long clienteId);

	List<Viaje> findByChoferId(Long choferId);

	List<Viaje> findByEstado(Viaje.Estado estado);

	List<Viaje> findByCamionetaIdAndFechaInicioAndFechaFin(Long camionetaId, LocalDate fechaInicio, LocalDate fechaFin);

}
