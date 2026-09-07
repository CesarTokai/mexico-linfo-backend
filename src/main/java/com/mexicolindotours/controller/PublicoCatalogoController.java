package com.mexicolindotours.controller;

import com.mexicolindotours.dto.PaqueteDetalleDTO;
import com.mexicolindotours.dto.PaqueteResumenDTO;
import com.mexicolindotours.dto.SalidaDTO;
import com.mexicolindotours.model.Paquete;
import com.mexicolindotours.model.Salida;
import com.mexicolindotours.service.PaqueteService;
import com.mexicolindotours.service.SalidaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** Catalogo publico: NO requiere token. */
@RestController
@RequestMapping("/publico")
public class PublicoCatalogoController {

	private static final int TAMANO_MAXIMO = 50;

	@Autowired
	private PaqueteService paqueteService;

	@Autowired
	private SalidaService salidaService;

	/** Busqueda por texto, ubicacion, categoria y rango de fechas. */
	@GetMapping("/paquetes")
	public ResponseEntity<?> buscar(@RequestParam(required = false) String texto,
									@RequestParam(required = false) String destino,
									@RequestParam(required = false) String categoria,
									@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
									@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
									@RequestParam(defaultValue = "0") int pagina,
									@RequestParam(defaultValue = "9") int tamano) {

		if (pagina < 0) pagina = 0;
		if (tamano < 1) tamano = 9;
		if (tamano > TAMANO_MAXIMO) tamano = TAMANO_MAXIMO;

		if (desde != null && hasta != null && hasta.isBefore(desde)) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body("La fecha 'hasta' no puede ser anterior a 'desde'");
		}

		Page<Paquete> resultado = paqueteService.buscar(texto, destino, categoria, desde, hasta, pagina, tamano);

		Map<String, Object> respuesta = new LinkedHashMap<>();
		respuesta.put("paquetes", resultado.getContent().stream().map(this::mapToResumen).collect(Collectors.toList()));
		respuesta.put("pagina", resultado.getNumber());
		respuesta.put("totalPaquetes", resultado.getTotalElements());
		respuesta.put("totalPaginas", resultado.getTotalPages());

		return ResponseEntity.ok(respuesta);
	}

	@GetMapping("/paquetes/{slug}")
	public ResponseEntity<?> detalle(@PathVariable String slug) {
		return paqueteService.obtenerPublicoPorSlug(slug)
				.map(p -> ResponseEntity.ok((Object) mapToDetalle(p)))
				.orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body("Paquete no encontrado"));
	}

	/** Proximas salidas de todo el catalogo, para la portada. */
	@GetMapping("/salidas/proximas")
	public ResponseEntity<?> proximasSalidas(@RequestParam(defaultValue = "12") int limite) {
		if (limite < 1 || limite > TAMANO_MAXIMO) limite = 12;

		List<SalidaDTO> salidas = salidaService.proximasSalidas().stream()
				.limit(limite)
				.map(this::mapSalida)
				.collect(Collectors.toList());

		return ResponseEntity.ok(salidas);
	}

	/** Opciones para poblar los filtros de busqueda. */
	@GetMapping("/filtros")
	public ResponseEntity<?> filtros() {
		Map<String, Object> filtros = new LinkedHashMap<>();
		filtros.put("destinos", paqueteService.destinosDisponibles());
		filtros.put("categorias", paqueteService.categoriasDisponibles());
		return ResponseEntity.ok(filtros);
	}

	private PaqueteResumenDTO mapToResumen(Paquete p) {
		List<Salida> proximas = salidaService.proximasDelPaquete(p.getId());
		return new PaqueteResumenDTO(
				p.getId(), p.getTitulo(), p.getSlug(), p.getResumen(), p.getDestino(), p.getCategoria(),
				p.getImagenUrl(), p.getPrecioPorPersona(), p.getDuracionDias(),
				proximas.isEmpty() ? null : proximas.get(0).getFechaSalida(),
				proximas.size(), null);
	}

	private PaqueteDetalleDTO mapToDetalle(Paquete p) {
		List<SalidaDTO> salidas = salidaService.proximasDelPaquete(p.getId()).stream()
				.map(this::mapSalida)
				.collect(Collectors.toList());

		return new PaqueteDetalleDTO(
				p.getId(), p.getTitulo(), p.getSlug(), p.getResumen(), p.getDescripcion(), p.getDestino(),
				p.getCategoria(), p.getImagenUrl(), p.getPrecioPorPersona(), p.getDuracionDias(), null, salidas);
	}

	private SalidaDTO mapSalida(Salida s) {
		return new SalidaDTO(
				s.getId(), s.getPaquete().getId(), s.getPaquete().getTitulo(), s.getPaquete().getSlug(),
				s.getPaquete().getDestino(), s.getPaquete().getImagenUrl(),
				s.getFechaSalida(), s.getFechaRegreso(), s.getCupoTotal(),
				salidaService.asientosDisponibles(s), s.precioEfectivo(), s.getEstado().toString(),
				null, null);
	}

}
