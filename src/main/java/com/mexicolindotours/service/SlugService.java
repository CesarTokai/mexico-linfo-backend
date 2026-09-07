package com.mexicolindotours.service;

import org.springframework.stereotype.Service;
import java.text.Normalizer;
import java.util.Locale;
import java.util.function.Predicate;

/** Genera slugs limpios para URLs: "Guía de Viaje 2026" -> "guia-de-viaje-2026". */
@Service
public class SlugService {

	public String generar(String texto) {
		if (texto == null || texto.isBlank()) {
			throw new IllegalArgumentException("No se puede generar slug de un texto vacío");
		}

		String sinAcentos = Normalizer.normalize(texto, Normalizer.Form.NFD)
				.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");

		String slug = sinAcentos.toLowerCase(Locale.ROOT)
				.replaceAll("[^a-z0-9]+", "-")
				.replaceAll("^-+|-+$", "");

		if (slug.isBlank()) {
			throw new IllegalArgumentException("El título no produce un slug válido");
		}

		return slug;
	}

	/** Agrega -2, -3... hasta encontrar uno libre segun el predicado de existencia. */
	public String generarUnico(String texto, Predicate<String> yaExiste) {
		String base = generar(texto);
		String candidato = base;
		int sufijo = 2;

		while (yaExiste.test(candidato)) {
			candidato = base + "-" + sufijo;
			sufijo++;
		}

		return candidato;
	}

}
