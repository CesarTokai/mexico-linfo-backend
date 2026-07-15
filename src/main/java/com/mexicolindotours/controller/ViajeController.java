package com.mexicolindotours.controller;

import com.mexicolindotours.dto.*;
import com.mexicolindotours.model.Viaje;
import com.mexicolindotours.model.Pago;
import com.mexicolindotours.model.Gasto;
import com.mexicolindotours.service.ViajeService;
import com.mexicolindotours.service.PagoService;
import com.mexicolindotours.service.GastoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/viajes")
public class ViajeController {

	@Autowired
	private ViajeService viajeService;

	@Autowired
	private PagoService pagoService;

	@Autowired
	private GastoService gastoService;

	@PostMapping
	public ResponseEntity<?> crear(@RequestBody ViajeCreateRequest request) {
		try {
			Viaje v = viajeService.crear(request.getClienteId(), request.getCamionetaId(),
										request.getChoferId(), request.getConcepto(),
										request.getFechaInicio(), request.getFechaFin(),
										request.getCostoTotal());
			if (request.getNotas() != null) {
				v.setNotas(request.getNotas());
			}
			return ResponseEntity.status(HttpStatus.CREATED).body(mapToDTO(v));
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	@GetMapping
	public ResponseEntity<?> obtenerTodos() {
		List<Viaje> viajes = viajeService.obtenerTodos();
		return ResponseEntity.ok(viajes.stream().map(this::mapToDTO).collect(Collectors.toList()));
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
		try {
			Viaje v = viajeService.obtenerPorId(id);
			return ResponseEntity.ok(mapToDTO(v));
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
	}

	@GetMapping("/camioneta/{camionetaId}")
	public ResponseEntity<?> obtenerPorCamioneta(@PathVariable Long camionetaId) {
		List<Viaje> viajes = viajeService.obtenerPorCamioneta(camionetaId);
		return ResponseEntity.ok(viajes.stream().map(this::mapToDTO).collect(Collectors.toList()));
	}

	@GetMapping("/cliente/{clienteId}")
	public ResponseEntity<?> obtenerPorCliente(@PathVariable Long clienteId) {
		List<Viaje> viajes = viajeService.obtenerPorCliente(clienteId);
		return ResponseEntity.ok(viajes.stream().map(this::mapToDTO).collect(Collectors.toList()));
	}

	@PutMapping("/{id}")
	public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody ViajeUpdateRequest request) {
		try {
			Viaje v = viajeService.actualizarViaje(id, request.getConcepto(), request.getFechaInicio(),
													request.getFechaFin(), request.getCostoTotal(),
													request.getKmInicial(), request.getChoferId());
			if (request.getNotas() != null) {
				v.setNotas(request.getNotas());
			}
			return ResponseEntity.ok(mapToDTO(v));
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	@PutMapping("/{id}/finalizar")
	public ResponseEntity<?> finalizar(@PathVariable Long id, @RequestBody ViajeFinalizarRequest request) {
		try {
			Viaje v = viajeService.finalizarViaje(id, request.getKmFinal());
			return ResponseEntity.ok(mapToDTO(v));
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	@PutMapping("/{id}/estado")
	public ResponseEntity<?> cambiarEstado(@PathVariable Long id, @RequestBody ViajeCambiarEstadoRequest request) {
		try {
			Viaje v = viajeService.actualizarEstado(id, Viaje.Estado.valueOf(request.getEstado()));
			return ResponseEntity.ok(mapToDTO(v));
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	@DeleteMapping("/{id}/cancelar")
	public ResponseEntity<?> cancelar(@PathVariable Long id) {
		try {
			viajeService.cancelarViaje(id);
			return ResponseEntity.ok("Viaje cancelado");
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	// Pagos
	@PostMapping("/{id}/pagos")
	public ResponseEntity<?> agregarPago(@PathVariable Long id, @RequestBody PagoCreateRequest request) {
		try {
			Pago p = pagoService.crear(id, Pago.Tipo.valueOf(request.getTipo()),
									   request.getFechaPago(), request.getMonto(),
									   request.getMetodo(), request.getNotas());
			return ResponseEntity.status(HttpStatus.CREATED).body(mapPagoToDTO(p));
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	@GetMapping("/{id}/pagos")
	public ResponseEntity<List<PagoDTO>> obtenerPagos(@PathVariable Long id) {
		List<Pago> pagos = pagoService.obtenerPorViaje(id);
		return ResponseEntity.ok(pagos.stream().map(this::mapPagoToDTO).collect(Collectors.toList()));
	}

	@DeleteMapping("/pagos/{pagoId}")
	public ResponseEntity<?> eliminarPago(@PathVariable Long pagoId) {
		try {
			pagoService.eliminar(pagoId);
			return ResponseEntity.ok("Pago eliminado");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	// Gastos
	@PostMapping("/{id}/gastos")
	public ResponseEntity<?> agregarGasto(@PathVariable Long id, @RequestBody GastoCreateRequest request) {
		try {
			Gasto g = gastoService.crear(id, Gasto.Tipo.valueOf(request.getTipo()),
										request.getFecha(), request.getMonto(), request.getNotas());
			return ResponseEntity.status(HttpStatus.CREATED).body(mapGastoToDTO(g));
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	@GetMapping("/{id}/gastos")
	public ResponseEntity<List<GastoDTO>> obtenerGastos(@PathVariable Long id) {
		List<Gasto> gastos = gastoService.obtenerPorViaje(id);
		return ResponseEntity.ok(gastos.stream().map(this::mapGastoToDTO).collect(Collectors.toList()));
	}

	@DeleteMapping("/gastos/{gastoId}")
	public ResponseEntity<?> eliminarGasto(@PathVariable Long gastoId) {
		try {
			gastoService.eliminar(gastoId);
			return ResponseEntity.ok("Gasto eliminado");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	private ViajeDTO mapToDTO(Viaje v) {
		ViajeDTO dto = new ViajeDTO();
		dto.setId(v.getId());
		dto.setClienteId(v.getCliente().getId());
		dto.setClienteNombre(v.getCliente().getNombre());
		dto.setCamionetaId(v.getCamioneta().getId());
		dto.setCamionetaNombre(v.getCamioneta().getNombre());
		if (v.getChofer() != null) {
			dto.setChoferId(v.getChofer().getId());
			dto.setChoferNombre(v.getChofer().getNombre());
		}
		dto.setConcepto(v.getConcepto());
		dto.setFechaInicio(v.getFechaInicio());
		dto.setFechaFin(v.getFechaFin());
		dto.setKmInicial(v.getKmInicial());
		dto.setKmFinal(v.getKmFinal());
		dto.setCostoTotal(v.getCostoTotal());
		dto.setEstado(v.getEstado().toString());
		dto.setNotas(v.getNotas());
		return dto;
	}

	private PagoDTO mapPagoToDTO(Pago p) {
		return new PagoDTO(p.getId(), p.getViaje().getId(), p.getTipo().toString(), p.getFecha(), p.getMonto(), p.getNotas());
	}

	private GastoDTO mapGastoToDTO(Gasto g) {
		return new GastoDTO(g.getId(), g.getViaje().getId(), g.getTipo().toString(), g.getDescripcion(), g.getMonto());
	}

}
