package com.mexicolindotours.repository;

import com.mexicolindotours.model.Camioneta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CamionetaRepository extends JpaRepository<Camioneta, Long> {

	List<Camioneta> findByEstado(Camioneta.Estado estado);

}
