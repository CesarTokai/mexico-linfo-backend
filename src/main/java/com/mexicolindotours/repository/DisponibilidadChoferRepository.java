package com.mexicolindotours.repository;

import com.mexicolindotours.model.DisponibilidadChofer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DisponibilidadChoferRepository extends JpaRepository<DisponibilidadChofer, Long> {

	List<DisponibilidadChofer> findByChoferId(Long choferId);

	Optional<DisponibilidadChofer> findByChoferIdAndFecha(Long choferId, LocalDate fecha);

}
