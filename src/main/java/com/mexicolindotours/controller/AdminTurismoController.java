package com.mexicolindotours.controller;

import com.mexicolindotours.dto.*;
import com.mexicolindotours.model.Paquete;
import com.mexicolindotours.model.Reserva;
import com.mexicolindotours.model.Salida;
import com.mexicolindotours.service.PaqueteService;
import com.mexicolindotours.service.ReservaService;
import com.mexicolindotours.service.SalidaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/** Panel interno del sitio publico: paquetes, salidas y verificacion de pagos. */
@RestController
@RequestMapping("/admin/turismo")
public class AdminTurismoController {

	@Autowired
	private PaqueteService paqueteService;

	@Autowired
	private SalidaService salidaService;

	@Autowired
	private ReservaService reservaService;

	// ---------- paquetes ----------

	@GetMapping("/paquetes")
	public ResponseEntity<?> listarPaquetes() {
		return ResponseEntity.ok(paqueteService.obtenerTodos().stream()
				.map(this::mapPaquete).collect(Collectors.toList()));
	}

	@PostMapping("/paquetes")
	public ResponseEntity<?> crearPaquete(@RequestBody PaqueteCreateRequest r) {
		try {
			Paquete p = paqueteService.crear(r.getTitulo(), r.getSlug(), r.getResumen(), r.getDescripcion(),
					r.getDestino(), r.getCategoria(), r.getImagenUrl(), r.getPrecioPorPersona(), r.getDuracionDias());
			return ResponseEntity.status(HttpStatus.CREATED).body(mapPaquete(p));
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	@PutMapping("/paquetes/{id}")
	public ResponseEntity<?> actualizarPaquete(@PathVariable Long id, @RequestBody PaqueteCreateRequest r) {
		try {
			Paquete p = paqueteService.actualizar(id, r.getTitulo(), r.getResumen(), r.getDescripcion(),
					r.getDestino(), r.getCategoria(), r.getImagenUrl(), r.getPrecioPorPersona(),
					r.getDuracionDias(), r.getActivo());
			return ResponseEntity.ok(mapPaquete(p));
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	// ---------- salidas ----------

	@GetMapping("/paquetes/{paqueteId}/salidas")
	public ResponseEntity<?> listarSalidas(@PathVariable Long paqueteId) {
		return ResponseEntity.ok(salidaService.obtenerPorPaquete(paqueteId).stream()
				.map(this::mapSalida).collect(Collectors.toList()));
	}

	@PostMapping("/salidas")
	public ResponseEntity<?> crearSalida(@RequestBody SalidaCreateRequest r) {
		try {
			Salida s = salidaService.crear(r.getPaqueteId(), r.getFechaSalida(), r.getFechaRegreso(),
					r.getCupoTotal(), r.getPrecioPorPersona(), r.getCamionetaId(), r.getChoferId());
			return ResponseEntity.status(HttpStatus.CREATED).body(mapSalida(s));
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	@PutMapping("/salidas/{id}")
	public ResponseEntity<?> actualizarSalida(@PathVariable Long id, @RequestBody SalidaCreateRequest r) {
		try {
			Salida s = salidaService.actualizar(id, r.getFechaSalida(), r.getFechaRegreso(),
					r.getCupoTotal(), r.getPrecioPorPersona(), r.getEstado(), r.getCamionetaId(), r.getChoferId());
			return ResponseEntity.ok(mapSalida(s));
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	/** Lista de pasajeros de una salida: lo que necesita el chofer. */
	@GetMapping("/salidas/{id}/reservas")
	public ResponseEntity<?> reservasDeSalida(@PathVariable Long id) {
		return ResponseEntity.ok(reservaService.porSalida(id).stream()
				.map(this::mapReserva).collect(Collectors.toList()));
	}

	// ---------- reservas ----------

	@PostMapping("/reservas/manual")
	public ResponseEntity<?> crearReservaManual(@RequestBody ReservaManualCreateRequest r) {
		try {
			Reserva reserva = reservaService.crearManual(
					r.getSalidaId(), r.getNombreCliente(), r.getTelefonoCliente(), r.getCorreoCliente(),
					r.getNumAsientos(), r.getNotas(), r.isMarcarConfirmada(), r.getMontoRecibido());
			return ResponseEntity.status(HttpStatus.CREATED).body(mapReserva(reserva));
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	@GetMapping("/reservas")
	public ResponseEntity<?> listarReservas(@RequestParam(required = false) String estado) {
		List<Reserva> reservas;

		if (estado != null && !estado.isBlank()) {
			try {
				reservas = reservaService.porEstado(Reserva.Estado.valueOf(estado));
			} catch (IllegalArgumentException e) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body("Estado inválido: " + estado + " (pendiente_pago, en_revision, confirmada, cancelada)");
			}
		} else {
			reservas = reservaService.todas();
		}

		return ResponseEntity.ok(reservas.stream().map(this::mapReserva).collect(Collectors.toList()));
	}

	@PutMapping("/reservas/{id}/confirmar")
	public ResponseEntity<?> confirmar(@PathVariable Long id,
									   @RequestBody(required = false) ConfirmarPagoRequest request) {
		try {
			BigDecimal monto = request != null ? request.getMontoRecibido() : null;
			return ResponseEntity.ok(mapReserva(reservaService.confirmar(id, monto)));
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	@DeleteMapping("/reservas/{id}")
	public ResponseEntity<?> cancelar(@PathVariable Long id) {
		try {
			reservaService.cancelar(id, null);
			return ResponseEntity.ok("Reserva cancelada");
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
	}

	// ---------- mapeos ----------

	private PaqueteResumenDTO mapPaquete(Paquete p) {
		var proximas = salidaService.proximasDelPaquete(p.getId());
		return new PaqueteResumenDTO(
				p.getId(), p.getTitulo(), p.getSlug(), p.getResumen(), p.getDestino(), p.getCategoria(),
				p.getImagenUrl(), p.getPrecioPorPersona(), p.getDuracionDias(),
				proximas.isEmpty() ? null : proximas.get(0).getFechaSalida(), proximas.size(), null);
	}

	private SalidaDTO mapSalida(Salida s) {
		return new SalidaDTO(
				s.getId(), s.getPaquete().getId(), s.getPaquete().getTitulo(), s.getPaquete().getSlug(),
				s.getPaquete().getDestino(), s.getPaquete().getImagenUrl(),
				s.getFechaSalida(), s.getFechaRegreso(), s.getCupoTotal(),
				salidaService.asientosDisponibles(s), s.precioEfectivo(), s.getEstado().toString(),
				s.getCamioneta() != null ? s.getCamioneta().getId() : null,
				s.getCamioneta() != null ? s.getCamioneta().getNombre() : null,
				s.getChofer() != null ? s.getChofer().getId() : null,
				s.getChofer() != null ? s.getChofer().getNombre() : null);
	}

	private ReservaDTO mapReserva(Reserva r) {
		return new ReservaDTO(
				r.getId(), r.getSalida().getId(), r.getSalida().getPaquete().getTitulo(),
				r.getSalida().getPaquete().getDestino(), r.getSalida().getFechaSalida(), r.getSalida().getFechaRegreso(),
				r.getUsuarioPublico().getId(), r.getUsuarioPublico().getNombre(), r.getUsuarioPublico().getCorreo(),
				r.getUsuarioPublico().getTelefono(), r.getNumAsientos(), r.getMontoTotal(), r.getMontoAnticipo(),
				r.getMontoPagado(), r.saldoPendiente(), r.getEstado().toString(),
				r.getComprobanteUrl(), r.getReferenciaTransferencia(), r.getNotas(), r.getConfirmadaAt());
	}

}
