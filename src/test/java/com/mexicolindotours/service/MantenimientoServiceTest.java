package com.mexicolindotours.service;

import com.mexicolindotours.model.Camioneta;
import com.mexicolindotours.model.Mantenimiento;
import com.mexicolindotours.repository.CamionetaRepository;
import com.mexicolindotours.repository.MantenimientoRepository;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * El aviso de mantenimiento se apagaba justo al pasarse el intervalo, que es
 * cuando mas importa. Estos tests fijan ese comportamiento.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("Aviso de mantenimiento")
class MantenimientoServiceTest {

	@Mock private MantenimientoRepository mantenimientoRepository;
	@Mock private CamionetaRepository camionetaRepository;

	@InjectMocks private MantenimientoService mantenimientoService;

	private Camioneta ximena;

	@BeforeEach
	void setUp() {
		ximena = new Camioneta("Ximena", "Urvan NV350", 14);
		ximena.setId(1L);
		ximena.setIntervaloMantenimientoKm(10000);
		when(camionetaRepository.findById(1L)).thenReturn(Optional.of(ximena));
	}

	private void sinServiciosPrevios() {
		when(mantenimientoRepository.findTopByCamionetaIdAndTipoOrderByKmAlMomentoDesc(1L, Mantenimiento.Tipo.mantenimiento))
				.thenReturn(Optional.empty());
	}

	private void ultimoServicioA(int km) {
		Mantenimiento m = new Mantenimiento(ximena, LocalDate.now(), Mantenimiento.Tipo.mantenimiento, BigDecimal.TEN);
		m.setKmAlMomento(km);
		when(mantenimientoRepository.findTopByCamionetaIdAndTipoOrderByKmAlMomentoDesc(1L, Mantenimiento.Tipo.mantenimiento))
				.thenReturn(Optional.of(m));
	}

	@Test
	@DisplayName("unidad nueva: faltan los 10,000 completos")
	void unidadNueva() {
		ximena.setKmActual(0);
		sinServiciosPrevios();

		assertThat(mantenimientoService.calcularKmsFaltantesParaProximoMantenimiento(1L)).contains(10000);
		assertThat(mantenimientoService.obtenerNivelAvisoMantenimiento(1L)).isEmpty();
	}

	@Test
	@DisplayName("avisa escalonado a 500, 400 y 300 km")
	void avisosEscalonados() {
		sinServiciosPrevios();

		ximena.setKmActual(9500);
		assertThat(mantenimientoService.obtenerNivelAvisoMantenimiento(1L)).contains("MEDIO");

		ximena.setKmActual(9600);
		assertThat(mantenimientoService.obtenerNivelAvisoMantenimiento(1L)).contains("ALTO");

		ximena.setKmActual(9700);
		assertThat(mantenimientoService.obtenerNivelAvisoMantenimiento(1L)).contains("CRÍTICO");
	}

	@Test
	@DisplayName("pasado el intervalo el aviso PERSISTE, no desaparece")
	void avisoPersisteVencido() {
		// Era el bug: a 10,500 km el objetivo saltaba a 20,000 y el aviso se
		// apagaba justo cuando el servicio ya estaba vencido.
		ximena.setKmActual(10500);
		sinServiciosPrevios();

		assertThat(mantenimientoService.calcularKmsFaltantesParaProximoMantenimiento(1L)).contains(-500);
		assertThat(mantenimientoService.obtenerNivelAvisoMantenimiento(1L)).contains("VENCIDO");
	}

	@Test
	@DisplayName("cuenta desde el ultimo servicio, no desde multiplos del odometro")
	void cuentaDesdeElUltimoServicio() {
		// Servicio hecho antes de tiempo, a 9,500: el proximo toca a 19,500.
		ximena.setKmActual(10200);
		ultimoServicioA(9500);

		assertThat(mantenimientoService.calcularKmsFaltantesParaProximoMantenimiento(1L)).contains(9300);
		assertThat(mantenimientoService.obtenerNivelAvisoMantenimiento(1L)).isEmpty();
	}

	@Test
	@DisplayName("registrar el servicio apaga el aviso")
	void registrarServicioApagaElAviso() {
		ximena.setKmActual(10500);
		ultimoServicioA(10500);

		assertThat(mantenimientoService.obtenerNivelAvisoMantenimiento(1L)).isEmpty();
	}

	@Test
	@DisplayName("respeta un intervalo distinto por unidad")
	void intervaloPersonalizado() {
		ximena.setIntervaloMantenimientoKm(5000);
		ximena.setKmActual(4800);
		sinServiciosPrevios();

		assertThat(mantenimientoService.calcularKmsFaltantesParaProximoMantenimiento(1L)).contains(200);
		assertThat(mantenimientoService.obtenerNivelAvisoMantenimiento(1L)).contains("CRÍTICO");
	}

}
