package com.mexicolindotours.controller;

import com.mexicolindotours.dto.TotalesDTO;
import com.mexicolindotours.service.TotalesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/totales")
public class TotalesController {

	@Autowired
	private TotalesService totalesService;

	@GetMapping
	public ResponseEntity<?> obtenerTotales(@RequestParam(required = false) Integer mes,
											@RequestParam(required = false) Integer anio) {
		TotalesDTO totales;

		if (mes != null && anio != null) {
			totales = totalesService.obtenerTotalesMes(mes, anio);
		} else if (anio != null) {
			totales = totalesService.obtenerTotalesAnio(anio);
		} else {
			totales = totalesService.obtenerTotalesAcumulado();
		}

		return ResponseEntity.ok(totales);
	}

}
