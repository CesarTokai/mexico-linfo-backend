package com.mexicolindotours.controller;

import com.mexicolindotours.dto.CalendarioDTO;
import com.mexicolindotours.service.CalendarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/calendario")
public class CalendarioController {

	@Autowired
	private CalendarioService calendarioService;

	@GetMapping
	public ResponseEntity<?> obtenerCalendario(@RequestParam LocalDate desde,
											   @RequestParam LocalDate hasta) {
		List<CalendarioDTO> calendario = calendarioService.obtenerCalendario(desde, hasta);
		return ResponseEntity.ok(calendario);
	}

}
