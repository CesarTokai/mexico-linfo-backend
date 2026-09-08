package com.mexicolindotours.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * La regla de traslape ya se equivoco dos veces, y las dos en los bordes:
 * primero rechazando el dia de contacto que el dueno autorizo, y despues
 * dejando salir dos veces la misma camioneta el mismo dia.
 */
@DisplayName("Disponibilidad de una unidad")
class DisponibilidadUnidadServiceTest {

	private static LocalDate d(int dia) {
		return LocalDate.of(2026, 12, dia);
	}

	private boolean choca(int aInicio, int aFin, int bInicio, int bFin) {
		return DisponibilidadUnidadService.hayConflicto(d(aInicio), d(aFin), d(bInicio), d(bFin));
	}

	@Nested
	@DisplayName("permite el contacto que autorizo el dueno (regla C8)")
	class ContactoPermitido {

		@Test
		@DisplayName("regresa el dia 15 y sale el mismo dia 15")
		void regresarYSalirElMismoDia() {
			assertThat(choca(10, 15, 15, 20)).isFalse();
		}

		@Test
		@DisplayName("da igual el orden en que se comparen")
		void esSimetrica() {
			assertThat(choca(15, 20, 10, 15)).isFalse();
		}

		@Test
		@DisplayName("una ocupacion de un dia el dia en que la otra regresa")
		void unDiaElDiaDelRegreso() {
			// El tour regresa el 16; una renta que arranca el 16 y dura hasta el 18 cabe.
			assertThat(choca(16, 18, 15, 16)).isFalse();
		}

		@Test
		@DisplayName("sin ningun dia en comun")
		void sinContacto() {
			assertThat(choca(1, 5, 15, 16)).isFalse();
		}
	}

	@Nested
	@DisplayName("bloquea los choques reales")
	class ChoquesReales {

		@Test
		@DisplayName("una renta de UN dia el mismo dia en que un tour de varios sale")
		void unDiaContraSalidaDeVariosDias() {
			// Fue el bug: un viaje de un solo dia no "regresa" ese dia, ocupa
			// la jornada entera, asi que la camioneta saldria dos veces.
			assertThat(choca(15, 15, 15, 16)).isTrue();
		}

		@Test
		@DisplayName("una renta de UN dia el dia en que el tour regresa")
		void unDiaContraRegreso() {
			assertThat(choca(16, 16, 15, 16)).isTrue();
		}

		@Test
		@DisplayName("dos ocupaciones de un dia en la misma fecha")
		void dosDeUnDiaElMismoDia() {
			assertThat(choca(15, 15, 15, 15)).isTrue();
		}

		@Test
		@DisplayName("rangos identicos")
		void identicos() {
			assertThat(choca(15, 16, 15, 16)).isTrue();
		}

		@Test
		@DisplayName("una contenida dentro de la otra")
		void contenida() {
			assertThat(choca(11, 14, 10, 15)).isTrue();
		}

		@Test
		@DisplayName("una envuelve a la otra")
		void envuelve() {
			assertThat(choca(14, 17, 15, 16)).isTrue();
		}

		@Test
		@DisplayName("traslape parcial")
		void traslapeParcial() {
			assertThat(choca(12, 18, 10, 15)).isTrue();
		}
	}

}
