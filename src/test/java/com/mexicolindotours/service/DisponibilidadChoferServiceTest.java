package com.mexicolindotours.service;

import com.mexicolindotours.model.*;
import com.mexicolindotours.repository.ChoferRepository;
import com.mexicolindotours.repository.DisponibilidadChoferRepository;
import com.mexicolindotours.repository.SalidaRepository;
import com.mexicolindotours.repository.ViajeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

/**
 * La `disponibilidad_chofer` es un registro OPCIONAL (regla C10: sin
 * registro, se puede asignar igual). Lo que si bloquea es el choque real
 * contra otra asignacion ya hecha, sea viaje o salida publica.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("Disponibilidad de chofer: choque contra asignaciones reales")
class DisponibilidadChoferServiceTest {

	@Mock private DisponibilidadChoferRepository disponibilidadRepository;
	@Mock private ChoferRepository choferRepository;
	@Mock private ViajeRepository viajeRepository;
	@Mock private SalidaRepository salidaRepository;

	@InjectMocks private DisponibilidadChoferService disponibilidadChoferService;

	private Viaje viajeExistente(Long choferId, int inicio, int fin, Viaje.Estado estado) {
		Cliente cliente = new Cliente();
		Camioneta camioneta = new Camioneta("Ximena", "Urvan", 14);
		Viaje v = new Viaje(cliente, camioneta, "x", LocalDate.of(2026, 12, inicio), LocalDate.of(2026, 12, fin), BigDecimal.TEN);
		v.setId(100L);
		v.setEstado(estado);
		Chofer chofer = new Chofer("Angel");
		chofer.setId(choferId);
		v.setChofer(chofer);
		return v;
	}

	private Salida salidaExistente(int inicio, int fin, Salida.Estado estado) {
		Paquete paquete = new Paquete("Tepoztlán", "tepoztlan", "Tepoztlán", BigDecimal.TEN);
		Salida s = new Salida(paquete, LocalDate.of(2026, 12, inicio), LocalDate.of(2026, 12, fin), 10);
		s.setId(200L);
		s.setEstado(estado);
		return s;
	}

	@BeforeEach
	void setUp() {
		when(salidaRepository.findByChoferIdAndEstadoNot(5L, Salida.Estado.cancelada)).thenReturn(List.of());
	}

	@Test
	@DisplayName("bloquea si el chofer ya esta en un viaje que se traslapa")
	void bloqueaPorViaje() {
		when(viajeRepository.findByChoferId(5L)).thenReturn(List.of(viajeExistente(5L, 10, 15, Viaje.Estado.apartado)));

		assertThatThrownBy(() -> disponibilidadChoferService.verificarLibre(
				5L, LocalDate.of(2026, 12, 12), LocalDate.of(2026, 12, 18), null, null))
				.hasMessageContaining("Chofer ocupado")
				.hasMessageContaining("viaje #100");
	}

	@Test
	@DisplayName("bloquea si el chofer ya opera una salida publica que se traslapa")
	void bloqueaPorSalida() {
		when(viajeRepository.findByChoferId(5L)).thenReturn(List.of());
		when(salidaRepository.findByChoferIdAndEstadoNot(5L, Salida.Estado.cancelada))
				.thenReturn(List.of(salidaExistente(10, 15, Salida.Estado.programada)));

		assertThatThrownBy(() -> disponibilidadChoferService.verificarLibre(
				5L, LocalDate.of(2026, 12, 12), LocalDate.of(2026, 12, 18), null, null))
				.hasMessageContaining("salida pública");
	}

	@Test
	@DisplayName("un viaje cancelado del mismo chofer no bloquea")
	void ignoraViajeCancelado() {
		when(viajeRepository.findByChoferId(5L)).thenReturn(List.of(viajeExistente(5L, 10, 15, Viaje.Estado.cancelado)));

		assertThatCode(() -> disponibilidadChoferService.verificarLibre(
				5L, LocalDate.of(2026, 12, 12), LocalDate.of(2026, 12, 18), null, null))
				.doesNotThrowAnyException();
	}

	@Test
	@DisplayName("excluye el propio viaje al reasignar (no choca consigo mismo)")
	void excluyeElPropioViaje() {
		when(viajeRepository.findByChoferId(5L)).thenReturn(List.of(viajeExistente(5L, 10, 15, Viaje.Estado.apartado)));

		assertThatCode(() -> disponibilidadChoferService.verificarLibre(
				5L, LocalDate.of(2026, 12, 10), LocalDate.of(2026, 12, 15), 100L, null))
				.doesNotThrowAnyException();
	}

	@Test
	@DisplayName("sin traslape, permite la asignacion")
	void permiteSinTraslape() {
		when(viajeRepository.findByChoferId(5L)).thenReturn(List.of(viajeExistente(5L, 10, 15, Viaje.Estado.apartado)));

		assertThatCode(() -> disponibilidadChoferService.verificarLibre(
				5L, LocalDate.of(2026, 12, 20), LocalDate.of(2026, 12, 22), null, null))
				.doesNotThrowAnyException();
	}

}
