package com.mexicolindotours.service;

import com.mexicolindotours.model.*;
import com.mexicolindotours.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("Salidas: cierre automatico y asignacion de unidad/chofer")
class SalidaServiceTest {

	@Mock private SalidaRepository salidaRepository;
	@Mock private PaqueteRepository paqueteRepository;
	@Mock private ReservaRepository reservaRepository;
	@Mock private CamionetaRepository camionetaRepository;
	@Mock private DisponibilidadUnidadService disponibilidadUnidadService;
	@Mock private ChoferRepository choferRepository;
	@Mock private DisponibilidadChoferService disponibilidadChoferService;

	@InjectMocks private SalidaService salidaService;

	private Salida salida;

	@BeforeEach
	void setUp() {
		Paquete paquete = new Paquete("Tepoztlán", "tepoztlan", "Tepoztlán", new BigDecimal("850.00"));
		paquete.setId(1L);

		salida = new Salida(paquete, LocalDate.now().plusDays(10), LocalDate.now().plusDays(10), 14);
		salida.setId(10L);

		when(salidaRepository.findById(10L)).thenReturn(Optional.of(salida));
		when(salidaRepository.save(any(Salida.class))).thenAnswer(i -> i.getArgument(0));
	}

	@Nested
	@DisplayName("cierre automatico por cupo")
	class CierreAutomatico {

		@Test
		@DisplayName("se llena el cupo: pasa de programada a cerrada")
		void seCierraAlLlenarse() {
			when(reservaRepository.asientosOcupados(10L)).thenReturn(14);

			salidaService.actualizarCierrePorCupo(10L);

			assertThat(salida.getEstado()).isEqualTo(Salida.Estado.cerrada);
		}

		@Test
		@DisplayName("se libera un asiento: reabre a programada")
		void reabreAlLiberarCupo() {
			salida.setEstado(Salida.Estado.cerrada);
			when(reservaRepository.asientosOcupados(10L)).thenReturn(10);

			salidaService.actualizarCierrePorCupo(10L);

			assertThat(salida.getEstado()).isEqualTo(Salida.Estado.programada);
		}

		@Test
		@DisplayName("con cupo libre no toca una salida programada")
		void sinCambiosSiHayCupo() {
			when(reservaRepository.asientosOcupados(10L)).thenReturn(5);

			salidaService.actualizarCierrePorCupo(10L);

			assertThat(salida.getEstado()).isEqualTo(Salida.Estado.programada);
		}

		@Test
		@DisplayName("nunca reabre ni cierra una salida cancelada")
		void noTocaCancelada() {
			salida.setEstado(Salida.Estado.cancelada);
			when(reservaRepository.asientosOcupados(10L)).thenReturn(0);

			salidaService.actualizarCierrePorCupo(10L);

			assertThat(salida.getEstado()).isEqualTo(Salida.Estado.cancelada);
		}
	}

	@Nested
	@DisplayName("al asignar camioneta")
	class AsignarCamioneta {

		@Test
		@DisplayName("rechaza una unidad en taller")
		void rechazaEnTaller() {
			Camioneta enTaller = new Camioneta("Libertad", "Urvan", 14);
			enTaller.setId(2L);
			enTaller.setEstado(Camioneta.Estado.en_taller);
			when(camionetaRepository.findById(2L)).thenReturn(Optional.of(enTaller));
			when(paqueteRepository.findById(1L)).thenReturn(Optional.of(salida.getPaquete()));

			assertThatThrownBy(() -> salidaService.crear(1L, LocalDate.now().plusDays(5),
					LocalDate.now().plusDays(5), 10, null, 2L, null))
					.hasMessageContaining("taller");
		}

		@Test
		@DisplayName("rechaza un cupo mayor a la capacidad de la unidad")
		void rechazaSobrecupo() {
			Camioneta ximena = new Camioneta("Ximena", "Urvan", 14);
			ximena.setId(3L);
			when(camionetaRepository.findById(3L)).thenReturn(Optional.of(ximena));
			when(paqueteRepository.findById(1L)).thenReturn(Optional.of(salida.getPaquete()));

			assertThatThrownBy(() -> salidaService.crear(1L, LocalDate.now().plusDays(5),
					LocalDate.now().plusDays(5), 20, null, 3L, null))
					.hasMessageContaining("supera la capacidad");
		}
	}

}
