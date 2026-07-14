package com.mexicolindotours.repository;

import com.mexicolindotours.model.GastoGeneral;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GastoGeneralRepository extends JpaRepository<GastoGeneral, Long> {

}
