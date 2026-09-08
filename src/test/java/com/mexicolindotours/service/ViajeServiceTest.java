package com.mexicolindotours.service;

import com.mexicolindotours.model.Camioneta;
import com.mexicolindotours.model.Cliente;
import com.mexicolindotours.model.Viaje;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("Viajes: ciclo de vida y kilometraje")
class ViajeServiceTest {

	@Mock private ViajeRepository viajeRepository;
	@Mock private ClienteRepository clienteRepository;
	@Mock private CamionetaRepository camionetaRepository;
	@Mock private ChoferRepository choferRepository;
	@Mock private PagoRepository pagoRepository;
	@Mock private GastoRepository gastoRepository;
	@Mock private DisponibilidadUnidadService disponibilidadUnidadService;

	@InjectMocks private ViajeService viajeService;

	private Viaje viaje;
	private Camioneta ximena;

	@BeforeEach
	void setUp() {
		ximena = new Camioneta("Ximena", "Urvan NV350", 14);
		ximena.setId(1L);
		ximena.setKmActual(50000);

		Cliente cliente = new Cliente();
		viaje = new Viaje(cliente, ximena, "Aeropuerto", LocalDate.now(), LocalDate.now().plusDays(1),
				new BigDecimal("5000.00"));
		viaje.setId(1L);

		when(viajeRepository.findById(1L)).thenReturn(Optional.of(viaje));
		when(viajeRepository.save(any(Viaje.class))).thenAnswer(i -> i.getArgument(0));
		when(camionetaRepository.save(any(Camioneta.class))).thenAnswer(i -> i.getArgument(0));
	}

	@Nested
	@DisplayName("al finalizar")
	class AlFinalizar {

		@Test
		@DisplayName("actualiza el odometro de la unidad")
		void actualizaOdometro() {
			viaje.setKmInicial(50000);

			Viaje r = viajeService.finalizarViaje(1L, 50350);

			assertThat(r.getEstado()).isEqualTo(Viaje.Estado.finalizado);
			assertThat(ximena.getKmActual()).isEqualTo(50350);
		}

		@Test
		@DisplayName("exige que el recorrido sea mayor que cero")
		void exigeRecorridoPositivo() {
			viaje.setKmInicial(50000);

			assertThatThrownBy(() -> viajeService.finalizarViaje(1L, 50000))
					.hasMessageContaining("mayor que km_inicial");
			assertThatThrownBy(() -> viajeService.finalizarViaje(1L, 49000))
					.hasMessageContaining("mayor que km_inicial");
		}

		@Test
		@DisplayName("no acepta un kilometraje por debajo del odometro")
		void rechazaKmMenorAlOdometro() {
			assertThatThrownBy(() -> viajeService.finalizarViaje(1L, 49500))
					.hasMessageContaining("km_actual de camioneta");
		}

		@Test
		@DisplayName("no se finaliza un viaje cancelado ni uno ya finalizado")
		void rechazaEstadosTerminales() {
			viaje.setEstado(Viaje.Estado.cancelado);
			assertThatThrownBy(() -> viajeService.finalizarViaje(1L, 60000))
					.hasMessageContaining("cancelado");

			viaje.setEstado(Viaje.Estado.finalizado);
			assertThatThrownBy(() -> viajeService.finalizarViaje(1L, 60000))
					.hasMessageContaining("ya está finalizado");
		}
	}

	@Nested
	@DisplayName("transiciones de estado")
	class Transiciones {

		@Test
		@DisplayName("apartado avanza a en_curso")
		void avanceNormal() {
			assertThat(viajeService.actualizarEstado(1L, Viaje.Estado.en_curso).getEstado())
					.isEqualTo(Viaje.Estado.en_curso);
		}

		@Test
		@DisplayName("no se puede saltar a finalizado por aqui: exige km_final")
		void noSaltaAFinalizado() {
			assertThatThrownBy(() -> viajeService.actualizarEstado(1L, Viaje.Estado.finalizado))
					.hasMessageContaining("endpoint de finalizar");
		}

		@Test
		@DisplayName("un viaje en curso no retrocede a apartado")
		void noRetrocede() {
			viaje.setEstado(Viaje.Estado.en_curso);

			assertThatThrownBy(() -> viajeService.actualizarEstado(1L, Viaje.Estado.apartado))
					.hasMessageContaining("no puede volver a apartado");
		}

		@Test
		@DisplayName("finalizado y cancelado son terminales")
		void estadosTerminales() {
			viaje.setEstado(Viaje.Estado.finalizado);
			assertThatThrownBy(() -> viajeService.actualizarEstado(1L, Viaje.Estado.en_curso))
					.hasMessageContaining("ya no cambia de estado");

			viaje.setEstado(Viaje.Estado.cancelado);
			assertThatThrownBy(() -> viajeService.actualizarEstado(1L, Viaje.Estado.en_curso))
					.hasMessageContaining("ya no cambia de estado");
		}

		@Test
		@DisplayName("se puede cancelar desde apartado o en curso")
		void seCancela() {
			assertThat(viajeService.actualizarEstado(1L, Viaje.Estado.cancelado).getEstado())
					.isEqualTo(Viaje.Estado.cancelado);
		}
	}

	@Nested
	@DisplayName("al editar")
	class AlEditar {

		@Test
		@DisplayName("un GESTOR no edita un viaje finalizado, un ADMIN si")
		void soloAdminEditaFinalizados() {
			viaje.setEstado(Viaje.Estado.finalizado);

			assertThatThrownBy(() -> viajeService.actualizarViaje(1L, "otro", null, null, null, null, null, false))
					.hasMessageContaining("No se puede editar");

			assertThat(viajeService.actualizarViaje(1L, "otro", null, null, null, null, null, true).getConcepto())
					.isEqualTo("otro");
		}

		@Test
		@DisplayName("rechaza un km_inicial por debajo del odometro")
		void rechazaKmInicialBajo() {
			assertThatThrownBy(() -> viajeService.actualizarViaje(1L, null, null, null, null, 100, null, true))
					.hasMessageContaining("no puede ser menor que el km actual");
		}

		@Test
		@DisplayName("rechaza fechas invertidas")
		void rechazaFechasInvertidas() {
			assertThatThrownBy(() -> viajeService.actualizarViaje(
					1L, null, LocalDate.of(2026, 12, 20), LocalDate.of(2026, 12, 10), null, null, null, true))
					.hasMessageContaining("no puede ser anterior");
		}
	}

}
