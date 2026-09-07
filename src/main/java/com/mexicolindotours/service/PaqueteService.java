package com.mexicolindotours.service;

import com.mexicolindotours.model.Paquete;
import com.mexicolindotours.repository.PaqueteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PaqueteService {

	@Autowired
	private PaqueteRepository paqueteRepository;

	@Autowired
	private SlugService slugService;

	public Paquete crear(String titulo, String slug, String resumen, String descripcion, String destino,
						 String categoria, String imagenUrl, BigDecimal precioPorPersona, Integer duracionDias) {

		if (titulo == null || titulo.isBlank()) {
			throw new IllegalArgumentException("El título es obligatorio");
		}
		if (destino == null || destino.isBlank()) {
			throw new IllegalArgumentException("El destino es obligatorio");
		}
		if (precioPorPersona == null || precioPorPersona.compareTo(BigDecimal.ZERO) <= 0) {
			throw new IllegalArgumentException("El precio por persona debe ser mayor que cero");
		}

		String slugFinal = (slug != null && !slug.isBlank())
				? slugService.generar(slug)
				: slugService.generarUnico(titulo, paqueteRepository::existsBySlug);

		if (paqueteRepository.existsBySlug(slugFinal)) {
			throw new IllegalArgumentException("Ya existe un paquete con el slug: " + slugFinal);
		}

		Paquete p = new Paquete(titulo, slugFinal, destino, precioPorPersona);
		p.setResumen(resumen);
		p.setDescripcion(descripcion);
		p.setCategoria(categoria);
		p.setImagenUrl(imagenUrl);
		p.setDuracionDias(duracionDias);

		return paqueteRepository.save(p);
	}

	public Paquete actualizar(Long id, String titulo, String resumen, String descripcion, String destino,
							  String categoria, String imagenUrl, BigDecimal precioPorPersona,
							  Integer duracionDias, Boolean activo) {

		Paquete p = paqueteRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Paquete no encontrado"));

		if (titulo != null && !titulo.isBlank()) p.setTitulo(titulo);
		if (resumen != null) p.setResumen(resumen);
		if (descripcion != null) p.setDescripcion(descripcion);
		if (destino != null && !destino.isBlank()) p.setDestino(destino);
		if (categoria != null) p.setCategoria(categoria);
		if (imagenUrl != null) p.setImagenUrl(imagenUrl);
		if (duracionDias != null) p.setDuracionDias(duracionDias);
		if (activo != null) p.setActivo(activo);
		if (precioPorPersona != null) {
			if (precioPorPersona.compareTo(BigDecimal.ZERO) <= 0) {
				throw new IllegalArgumentException("El precio por persona debe ser mayor que cero");
			}
			p.setPrecioPorPersona(precioPorPersona);
		}

		p.setUpdatedAt(LocalDateTime.now());
		return paqueteRepository.save(p);
	}

	/** Busqueda del catalogo publico: texto, destino, categoria y rango de fechas. */
	public Page<Paquete> buscar(String texto, String destino, String categoria,
								LocalDate desde, LocalDate hasta, int pagina, int tamano) {
		Pageable pageable = PageRequest.of(pagina, tamano, Sort.by(Sort.Direction.ASC, "titulo"));
		return paqueteRepository.buscar(
				vacioANull(texto), vacioANull(destino), vacioANull(categoria), desde, hasta, pageable);
	}

	public Optional<Paquete> obtenerPublicoPorSlug(String slug) {
		return paqueteRepository.findBySlugAndActivoTrue(slug);
	}

	public Optional<Paquete> obtenerPorId(Long id) {
		return paqueteRepository.findById(id);
	}

	public List<Paquete> obtenerTodos() {
		return paqueteRepository.findAll();
	}

	public List<String> destinosDisponibles() {
		return paqueteRepository.destinosDisponibles();
	}

	public List<String> categoriasDisponibles() {
		return paqueteRepository.categoriasDisponibles();
	}

	private String vacioANull(String s) {
		return (s == null || s.isBlank()) ? null : s;
	}

}
