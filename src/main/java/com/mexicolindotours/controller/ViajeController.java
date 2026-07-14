package com.mexicolindotours.controller;

import com.mexicolindotours.dto.ViajeDTO;
import com.mexicolindotours.dto.PagoDTO;
import com.mexicolindotours.dto.GastoDTO;
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
import java.math.BigDecimal;
import java.time.LocalDate;
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
	public ResponseEntity<?> crear(@RequestParam Long clienteId,
								   @RequestParam Long camionetaId,
								   @RequestParam(required = false) Long choferId,
								   @RequestParam String concepto,
								   @RequestParam LocalDate fechaInicio,
								   @RequestParam LocalDate fechaFin,
								   @RequestParam BigDecimal costoTotal) {
		try {
			Viaje v = viajeService.crear(clienteId, camionetaId, choferId, concepto, fechaInicio, fechaFin, costoTotal);
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
	public ResponseEntity<?> actualizar(@PathVariable Long id,
										@RequestParam(required = false) String concepto,
										@RequestParam(required = false) LocalDate fechaInicio,
										@RequestParam(required = false) LocalDate fechaFin,
										@RequestParam(required = false) BigDecimal costoTotal,
										@RequestParam(required = false) Integer kmInicial,
										@RequestParam(required = false) Long choferId) {
		try {
			Viaje v = viajeService.actualizarViaje(id, concepto, fechaInicio, fechaFin, costoTotal, kmInicial, choferId);
			return ResponseEntity.ok(mapToDTO(v));
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	@PutMapping("/{id}/finalizar")
	public ResponseEntity<?> finalizar(@PathVariable Long id,
									   @RequestParam Integer kmFinal) {
		try {
			Viaje v = viajeService.finalizarViaje(id, kmFinal);
			return ResponseEntity.ok(mapToDTO(v));
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	@PutMapping("/{id}/estado")
	public ResponseEntity<?> cambiarEstado(@PathVariable Long id,
										   @RequestParam Viaje.Estado estado) {
		try {
			Viaje v = viajeService.actualizarEstado(id, estado);
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
	public ResponseEntity<?> agregarPago(@PathVariable Long id,
										 @RequestParam Pago.Tipo tipo,
										 @RequestParam LocalDate fecha,
										 @RequestParam BigDecimal monto) {
		try {
			Pago p = pagoService.crear(id, tipo, fecha, monto);
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
	public ResponseEntity<?> agregarGasto(@PathVariable Long id,
										  @RequestParam Gasto.Tipo tipo,
										  @RequestParam(required = false) String descripcion,
										  @RequestParam BigDecimal monto) {
		try {
			Gasto g = gastoService.crear(id, tipo, descripcion, monto);
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
