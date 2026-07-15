package com.mexicolindotours.controller;

import com.mexicolindotours.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

	@Autowired
	private DashboardService dashboardService;

	@GetMapping("/mes")
	public ResponseEntity<?> obtenerDashboardMes(@RequestParam Integer mes, @RequestParam Integer anio) {
		try {
			Map<String, Object> dashboard = dashboardService.obtenerDashboardMes(mes, anio);
			return ResponseEntity.ok(dashboard);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@GetMapping("/anio")
	public ResponseEntity<?> obtenerDashboardAnio(@RequestParam Integer anio) {
		try {
			Map<String, Object> dashboard = dashboardService.obtenerDashboardAnio(anio);
			return ResponseEntity.ok(dashboard);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@GetMapping("/acumulado")
	public ResponseEntity<?> obtenerDashboardAcumulado() {
		try {
			Map<String, Object> dashboard = dashboardService.obtenerDashboardAcumulado();
			return ResponseEntity.ok(dashboard);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
}
