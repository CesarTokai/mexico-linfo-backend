package com.mexicolindotours.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Generacion de slugs")
class SlugServiceTest {

	private SlugService slugService;

	@BeforeEach
	void setUp() {
		slugService = new SlugService();
	}

	@Test
	@DisplayName("quita acentos y pasa a minusculas")
	void quitaAcentos() {
		assertThat(slugService.generar("Guías de Viaje")).isEqualTo("guias-de-viaje");
		assertThat(slugService.generar("Tepoztlán Mágico")).isEqualTo("tepoztlan-magico");
		assertThat(slugService.generar("Cañón del Sumidero")).isEqualTo("canon-del-sumidero");
	}

	@Test
	@DisplayName("colapsa signos y espacios sobrantes")
	void colapsaSeparadores() {
		assertThat(slugService.generar("  ¡Hola,   mundo!  ")).isEqualTo("hola-mundo");
		assertThat(slugService.generar("A --- B")).isEqualTo("a-b");
	}

	@Test
	@DisplayName("conserva los numeros")
	void conservaNumeros() {
		assertThat(slugService.generar("Ruta 66 en 2026")).isEqualTo("ruta-66-en-2026");
	}

	@Test
	@DisplayName("rechaza lo que no produce un slug util")
	void rechazaVacios() {
		assertThatThrownBy(() -> slugService.generar(null)).isInstanceOf(IllegalArgumentException.class);
		assertThatThrownBy(() -> slugService.generar("   ")).isInstanceOf(IllegalArgumentException.class);
		assertThatThrownBy(() -> slugService.generar("¿¡!?")).isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	@DisplayName("desambigua agregando un sufijo")
	void desambigua() {
		Set<String> tomados = Set.of("tepoztlan", "tepoztlan-2");
		assertThat(slugService.generarUnico("Tepoztlán", tomados::contains)).isEqualTo("tepoztlan-3");
	}

	@Test
	@DisplayName("si esta libre no agrega sufijo")
	void sinSufijoSiEstaLibre() {
		assertThat(slugService.generarUnico("Tepoztlán", s -> false)).isEqualTo("tepoztlan");
	}

}
