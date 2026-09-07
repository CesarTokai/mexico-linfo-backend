package com.mexicolindotours.controller;

import com.mexicolindotours.dto.PaqueteResumenDTO;
import com.mexicolindotours.dto.ReservaCreateRequest;
import com.mexicolindotours.dto.ReservaDTO;
import com.mexicolindotours.model.Favorito;
import com.mexicolindotours.model.Paquete;
import com.mexicolindotours.model.Reserva;
import com.mexicolindotours.model.UsuarioPublico;
import com.mexicolindotours.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** Cuenta del cliente: favoritos y reservas. Requiere token con rol CLIENTE. */
@RestController
@RequestMapping("/publico/mi")
public class PublicoCuentaController {

	@Autowired
	private UsuarioPublicoService usuarioPublicoService;

	@Autowired
	private FavoritoService favoritoService;

	@Autowired
	private ReservaService reservaService;

	@Autowired
	private SalidaService salidaService;

	@Autowired
	private AlmacenamientoImagenService almacenamientoImagenService;

	// ---------- favoritos ----------

	@GetMapping("/favoritos")
	public ResponseEntity<?> misFavoritos() {
		Long uid = usuarioActualId();
		if (uid == null) return noAutenticado();

		List<PaqueteResumenDTO> favoritos = favoritoService.listar(uid).stream()
				.map(Favorito::getPaquete)
				.map(this::mapPaquete)
				.collect(Collectors.toList());

		return ResponseEntity.ok(favoritos);
	}

	@PostMapping("/favoritos/{paqueteId}")
	public ResponseEntity<?> agregarFavorito(@PathVariable Long paqueteId) {
		Long uid = usuarioActualId();
		if (uid == null) return noAutenticado();

		try {
			favoritoService.agregar(uid, paqueteId);
			return ResponseEntity.status(HttpStatus.CREATED).body("Agregado a favoritos");
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
	}

	@DeleteMapping("/favoritos/{paqueteId}")
	public ResponseEntity<?> quitarFavorito(@PathVariable Long paqueteId) {
		Long uid = usuarioActualId();
		if (uid == null) return noAutenticado();

		favoritoService.quitar(uid, paqueteId);
		return ResponseEntity.ok("Quitado de favoritos");
	}

	// ---------- reservas ----------

	@GetMapping("/reservas")
	public ResponseEntity<?> misReservas() {
		Long uid = usuarioActualId();
		if (uid == null) return noAutenticado();

		return ResponseEntity.ok(reservaService.misReservas(uid).stream()
				.map(this::mapReserva).collect(Collectors.toList()));
	}

	@PostMapping("/reservas")
	public ResponseEntity<?> apartar(@RequestBody ReservaCreateRequest request) {
		Long uid = usuarioActualId();
		if (uid == null) return noAutenticado();

		try {
			Reserva r = reservaService.apartar(request.getSalidaId(), uid, request.getNumAsientos(), request.getNotas());

			Map<String, Object> respuesta = new LinkedHashMap<>();
			respuesta.put("reserva", mapReserva(r));
			respuesta.put("mensaje", "Asientos apartados. Sube tu comprobante de transferencia para confirmar.");

			return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	/** Sube el comprobante de la transferencia y deja la reserva en revision. */
	@PostMapping("/reservas/{id}/comprobante")
	public ResponseEntity<?> subirComprobante(@PathVariable Long id,
											  @RequestParam("archivo") MultipartFile archivo,
											  @RequestParam(required = false) String referencia) {
		Long uid = usuarioActualId();
		if (uid == null) return noAutenticado();

		try {
			String url = almacenamientoImagenService.guardarComprobante(archivo);
			Reserva r = reservaService.registrarComprobante(id, uid, url, referencia);
			return ResponseEntity.ok(mapReserva(r));
		} catch (ReservaService.SeguridadException e) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	@DeleteMapping("/reservas/{id}")
	public ResponseEntity<?> cancelar(@PathVariable Long id) {
		Long uid = usuarioActualId();
		if (uid == null) return noAutenticado();

		try {
			reservaService.cancelar(id, uid);
			return ResponseEntity.ok("Reserva cancelada");
		} catch (ReservaService.SeguridadException e) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
	}

	// ---------- apoyo ----------

	/** El subject del token es el correo; de ahi se resuelve el usuario publico. */
	private Long usuarioActualId() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null || auth.getName() == null) return null;

		return usuarioPublicoService.obtenerPorCorreo(auth.getName())
				.map(UsuarioPublico::getId)
				.orElse(null);
	}

	private ResponseEntity<?> noAutenticado() {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Sesión no válida");
	}

	private PaqueteResumenDTO mapPaquete(Paquete p) {
		var proximas = salidaService.proximasDelPaquete(p.getId());
		return new PaqueteResumenDTO(
				p.getId(), p.getTitulo(), p.getSlug(), p.getResumen(), p.getDestino(), p.getCategoria(),
				p.getImagenUrl(), p.getPrecioPorPersona(), p.getDuracionDias(),
				proximas.isEmpty() ? null : proximas.get(0).getFechaSalida(), proximas.size(), true);
	}

	private ReservaDTO mapReserva(Reserva r) {
		return new ReservaDTO(
				r.getId(), r.getSalida().getId(), r.getSalida().getPaquete().getTitulo(),
				r.getSalida().getPaquete().getDestino(), r.getSalida().getFechaSalida(), r.getSalida().getFechaRegreso(),
				r.getUsuarioPublico().getId(), r.getUsuarioPublico().getNombre(), r.getUsuarioPublico().getCorreo(),
				r.getUsuarioPublico().getTelefono(), r.getNumAsientos(), r.getMontoTotal(), r.getEstado().toString(),
				r.getComprobanteUrl(), r.getReferenciaTransferencia(), r.getNotas(), r.getConfirmadaAt());
	}

}
