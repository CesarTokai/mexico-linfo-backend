package com.mexicolindotours.controller;

import com.mexicolindotours.service.ChoferService;
import com.mexicolindotours.service.MantenimientoService;
import com.mexicolindotours.service.TramiteVehiculoService;
import com.mexicolindotours.service.UsuarioService;
import com.mexicolindotours.repository.CamionetaRepository;
import com.mexicolindotours.repository.ChoferRepository;
import com.mexicolindotours.repository.TramiteVehiculoRepository;
import com.mexicolindotours.model.Camioneta;
import com.mexicolindotours.model.Chofer;
import com.mexicolindotours.model.TramiteVehiculo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/avisos")
public class AvisosController {

	@Autowired
	private MantenimientoService mantenimientoService;

	@Autowired
	private TramiteVehiculoService tramiteVehiculoService;

	@Autowired
	private CamionetaRepository camionetaRepository;

	@Autowired
	private TramiteVehiculoRepository tramiteVehiculoRepository;

	@Autowired
	private ChoferService choferService;

	@Autowired
	private ChoferRepository choferRepository;

	@GetMapping
	public ResponseEntity<?> obtenerAvisos() {
		Map<String, Object> avisos = new HashMap<>();

		List<Map<String, Object>> mantenimientos = calcularAvisosMantenimiento();
		List<Map<String, Object>> tramites = calcularAvisosTramites();
		List<Map<String, Object>> licencias = calcularAvisosLicencias();

		avisos.put("mantenimientos", mantenimientos);
		avisos.put("tramites", tramites);
		avisos.put("licencias", licencias);
		avisos.put("total", mantenimientos.size() + tramites.size() + licencias.size());

		return ResponseEntity.ok(avisos);
	}

	@GetMapping("/mantenimientos")
	public ResponseEntity<?> obtenerAvisosMantenimientos() {
		return ResponseEntity.ok(calcularAvisosMantenimiento());
	}

	@GetMapping("/tramites")
	public ResponseEntity<?> obtenerAvisosTramites() {
		return ResponseEntity.ok(calcularAvisosTramites());
	}

	@GetMapping("/licencias")
	public ResponseEntity<?> obtenerAvisosLicencias() {
		return ResponseEntity.ok(calcularAvisosLicencias());
	}

	/** Licencias por vencer o vencidas. Solo choferes activos. */
	private List<Map<String, Object>> calcularAvisosLicencias() {
		List<Map<String, Object>> avisos = new ArrayList<>();

		for (Chofer chofer : choferRepository.findAll()) {
			if (Boolean.FALSE.equals(chofer.getActivo())) continue;
			if (chofer.getLicenciaVencimiento() == null) continue;

			Optional<String> nivel = choferService.obtenerNivelAvisoLicencia(chofer.getId());
			Optional<Long> dias = choferService.calcularDiasParaVencimientoLicencia(chofer.getId());

			if (nivel.isEmpty() || dias.isEmpty()) continue;

			Map<String, Object> aviso = new HashMap<>();
			aviso.put("choferId", chofer.getId());
			aviso.put("choferNombre", chofer.getNombre());
			aviso.put("fechaVencimiento", chofer.getLicenciaVencimiento());
			aviso.put("diasFaltantes", dias.get());
			aviso.put("nivel", nivel.get());
			aviso.put("tipoAviso", "licencia");

			long d = dias.get();
			aviso.put("vencido", d <= 0);
			if (d <= 0) {
				aviso.put("prioridad", 0);
			} else if (d <= 5) {
				aviso.put("prioridad", 2);
			} else if (d <= 10) {
				aviso.put("prioridad", 3);
			} else if (d <= 15) {
				aviso.put("prioridad", 4);
			} else {
				aviso.put("prioridad", 5);
			}

			avisos.add(aviso);
		}

		return avisos;
	}

	private List<Map<String, Object>> calcularAvisosMantenimiento() {
		List<Map<String, Object>> avisos = new ArrayList<>();
		List<Camioneta> camionetas = camionetaRepository.findAll();

		for (Camioneta camioneta : camionetas) {
			Optional<Integer> kmsFaltantes = mantenimientoService.calcularKmsFaltantesParaProximoMantenimiento(camioneta.getId());
			Optional<String> nivel = mantenimientoService.obtenerNivelAvisoMantenimiento(camioneta.getId());

			if (nivel.isPresent() && kmsFaltantes.isPresent()) {
				Map<String, Object> aviso = new HashMap<>();
				aviso.put("camionetaId", camioneta.getId());
				aviso.put("camionetaNombre", camioneta.getNombre());
				aviso.put("kmActual", camioneta.getKmActual());
				aviso.put("kmFaltantes", kmsFaltantes.get());
				aviso.put("nivel", nivel.get());
				aviso.put("tipo", "mantenimiento");

				int kms = kmsFaltantes.get();
				aviso.put("vencido", kms <= 0);
				if (kms <= 0) {
					aviso.put("kmExcedidos", Math.abs(kms));
					aviso.put("prioridad", 0);
				} else if (kms <= 300) {
					aviso.put("prioridad", 1);
				} else if (kms <= 400) {
					aviso.put("prioridad", 2);
				} else {
					aviso.put("prioridad", 3);
				}

				avisos.add(aviso);
			}
		}

		return avisos;
	}

	private List<Map<String, Object>> calcularAvisosTramites() {
		List<Map<String, Object>> avisos = new ArrayList<>();
		List<TramiteVehiculo> tramites = tramiteVehiculoRepository.findAll();

		for (TramiteVehiculo tramite : tramites) {
			if (tramite.getFechaVencimiento() == null) continue;

			Optional<Long> diasFaltantes = tramiteVehiculoService.calcularDiasParaVencimiento(tramite.getId());
			Optional<String> nivel = tramiteVehiculoService.obtenerNivelAvisoVencimiento(tramite.getId());

			if (nivel.isPresent() && !nivel.get().isEmpty()) {
				Map<String, Object> aviso = new HashMap<>();
				aviso.put("tramiteId", tramite.getId());
				aviso.put("camionetaId", tramite.getCamioneta().getId());
				aviso.put("camionetaNombre", tramite.getCamioneta().getNombre());
				aviso.put("tipo", tramite.getTipo().toString());
				aviso.put("fechaVencimiento", tramite.getFechaVencimiento());
				aviso.put("diasFaltantes", diasFaltantes.orElse(0L));
				aviso.put("nivel", nivel.get());
				aviso.put("tipoAviso", "vencimiento");

				long dias = diasFaltantes.orElse(0L);
				if (dias <= 0) {
					aviso.put("prioridad", 1);
				} else if (dias <= 5) {
					aviso.put("prioridad", 2);
				} else if (dias <= 10) {
					aviso.put("prioridad", 3);
				} else if (dias <= 15) {
					aviso.put("prioridad", 4);
				} else {
					aviso.put("prioridad", 5);
				}

				avisos.add(aviso);
			}
		}

		return avisos;
	}
}
