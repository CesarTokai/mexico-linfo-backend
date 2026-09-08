package com.mexicolindotours.service;

import com.mexicolindotours.model.*;
import com.mexicolindotours.repository.ReservaRepository;
import com.mexicolindotours.repository.SalidaRepository;
import com.mexicolindotours.repository.UsuarioPublicoRepository;
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
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("Reservas: cupo y dinero")
class ReservaServiceTest {

	@Mock private ReservaRepository reservaRepository;
	@Mock private SalidaRepository salidaRepository;
	@Mock private UsuarioPublicoRepository usuarioPublicoRepository;
	@Mock private NotificacionService notificacionService;

	@InjectMocks private ReservaService reservaService;

	private Salida salida;
	private UsuarioPublico maria;
	private UsuarioPublico otro;

	@BeforeEach
	void setUp() {
		ReflectionTestUtils.setField(reservaService, "horasParaPagar", 48L);
		ReflectionTestUtils.setField(reservaService, "porcentajeAnticipo", 50);

		Paquete paquete = new Paquete("Tepoztlán", "tepoztlan", "Tepoztlán", new BigDecimal("850.00"));
		paquete.setId(1L);

		salida = new Salida(paquete, LocalDate.now().plusDays(30), LocalDate.now().plusDays(30), 14);
		salida.setId(10L);

		maria = new UsuarioPublico("Maria", "maria@correo.com", "hash", null);
		maria.setId(100L);
		otro = new UsuarioPublico("Otro", "otro@correo.com", "hash", null);
		otro.setId(200L);

		when(usuarioPublicoRepository.findById(100L)).thenReturn(Optional.of(maria));
		when(usuarioPublicoRepository.findById(200L)).thenReturn(Optional.of(otro));
		when(salidaRepository.findByIdBloqueando(10L)).thenReturn(Optional.of(salida));
		when(reservaRepository.save(any(Reserva.class))).thenAnswer(i -> i.getArgument(0));
	}

	@Nested
	@DisplayName("al apartar")
	class AlApartar {

		@Test
		@DisplayName("cobra el precio por asiento y separa el 50% de anticipo")
		void calculaMontos() {
			when(reservaRepository.asientosOcupados(10L)).thenReturn(0);

			Reserva r = reservaService.apartar(10L, 100L, 4, null);

			assertThat(r.getMontoTotal()).isEqualByComparingTo("3400.00");
			assertThat(r.getMontoAnticipo()).isEqualByComparingTo("1700.00");
			assertThat(r.getMontoPagado()).isEqualByComparingTo("0");
			assertThat(r.getEstado()).isEqualTo(Reserva.Estado.pendiente_pago);
		}

		@Test
		@DisplayName("no deja vender mas asientos de los que quedan")
		void impideSobreventa() {
			when(reservaRepository.asientosOcupados(10L)).thenReturn(10);

			assertThatThrownBy(() -> reservaService.apartar(10L, 100L, 5, null))
					.isInstanceOf(IllegalArgumentException.class)
					.hasMessageContaining("Solo quedan 4");
		}

		@Test
		@DisplayName("deja tomar justo los ultimos asientos")
		void permiteLosUltimos() {
			when(reservaRepository.asientosOcupados(10L)).thenReturn(10);

			assertThat(reservaService.apartar(10L, 100L, 4, null).getNumAsientos()).isEqualTo(4);
		}

		@Test
		@DisplayName("rechaza cantidades sin sentido")
		void rechazaCantidadInvalida() {
			assertThatThrownBy(() -> reservaService.apartar(10L, 100L, 0, null))
					.isInstanceOf(IllegalArgumentException.class);
			assertThatThrownBy(() -> reservaService.apartar(10L, 100L, null, null))
					.isInstanceOf(IllegalArgumentException.class);
		}

		@Test
		@DisplayName("no admite reservas en una salida cerrada ni ya pasada")
		void rechazaSalidaNoDisponible() {
			when(reservaRepository.asientosOcupados(10L)).thenReturn(0);

			salida.setEstado(Salida.Estado.cerrada);
			assertThatThrownBy(() -> reservaService.apartar(10L, 100L, 1, null))
					.isInstanceOf(IllegalArgumentException.class);

			salida.setEstado(Salida.Estado.programada);
			salida.setFechaSalida(LocalDate.now().minusDays(1));
			assertThatThrownBy(() -> reservaService.apartar(10L, 100L, 1, null))
					.hasMessageContaining("ya pasó");
		}
	}

	@Nested
	@DisplayName("al verificar el pago")
	class AlConfirmar {

		private Reserva reserva;

		@BeforeEach
		void crearReserva() {
			reserva = new Reserva(salida, maria, 4, new BigDecimal("3400.00"), new BigDecimal("1700.00"));
			reserva.setId(5L);
			reserva.setComprobanteUrl("/uploads/comprobantes/x.pdf");
			when(reservaRepository.findById(5L)).thenReturn(Optional.of(reserva));
		}

		@Test
		@DisplayName("con el anticipo cubierto asegura los lugares")
		void confirmaConAnticipo() {
			Reserva r = reservaService.confirmar(5L, new BigDecimal("1700.00"));

			assertThat(r.getEstado()).isEqualTo(Reserva.Estado.confirmada);
			assertThat(r.saldoPendiente()).isEqualByComparingTo("1700.00");
			assertThat(r.getConfirmadaAt()).isNotNull();
		}

		@Test
		@DisplayName("rechaza un pago que no alcanza el anticipo, sin dejar la reserva a medias")
		void rechazaPagoInsuficiente() {
			assertThatThrownBy(() -> reservaService.confirmar(5L, new BigDecimal("500.00")))
					.hasMessageContaining("no se cubre el anticipo");

			// Lo importante: la entidad no quedo modificada.
			assertThat(reserva.getMontoPagado()).isEqualByComparingTo("0");
			assertThat(reserva.getEstado()).isEqualTo(Reserva.Estado.pendiente_pago);
		}

		@Test
		@DisplayName("acumula los pagos hasta liquidar")
		void acumulaPagos() {
			reservaService.confirmar(5L, new BigDecimal("1700.00"));
			Reserva r = reservaService.confirmar(5L, new BigDecimal("1700.00"));

			assertThat(r.getMontoPagado()).isEqualByComparingTo("3400.00");
			assertThat(r.saldoPendiente()).isEqualByComparingTo("0");
			assertThat(r.liquidada()).isTrue();
		}

		@Test
		@DisplayName("no permite cobrar mas del saldo")
		void rechazaCobroExcesivo() {
			reservaService.confirmar(5L, new BigDecimal("3400.00"));

			assertThatThrownBy(() -> reservaService.confirmar(5L, new BigDecimal("100.00")))
					.hasMessageContaining("supera el saldo");
		}

		@Test
		@DisplayName("sin comprobante no hay nada que verificar")
		void exigeComprobante() {
			reserva.setComprobanteUrl(null);

			assertThatThrownBy(() -> reservaService.confirmar(5L, null))
					.hasMessageContaining("No hay comprobante");
		}

		@Test
		@DisplayName("no se confirma una reserva cancelada")
		void rechazaCancelada() {
			reserva.setEstado(Reserva.Estado.cancelada);

			assertThatThrownBy(() -> reservaService.confirmar(5L, new BigDecimal("1700.00")))
					.hasMessageContaining("cancelada");
		}

		@Test
		@DisplayName("solo avisa al cliente la primera vez que se confirma")
		void avisaUnaSolaVez() {
			reservaService.confirmar(5L, new BigDecimal("1700.00"));
			reservaService.confirmar(5L, new BigDecimal("1700.00"));

			verify(notificacionService, times(1)).reservaConfirmada(any());
		}
	}

	@Nested
	@DisplayName("propiedad de la reserva")
	class Propiedad {

		private Reserva reserva;

		@BeforeEach
		void crearReserva() {
			reserva = new Reserva(salida, maria, 2, new BigDecimal("1700.00"), new BigDecimal("850.00"));
			reserva.setId(7L);
			when(reservaRepository.findById(7L)).thenReturn(Optional.of(reserva));
		}

		@Test
		@DisplayName("un cliente no puede cancelar la reserva de otro")
		void noCancelaAjena() {
			assertThatThrownBy(() -> reservaService.cancelar(7L, 200L))
					.isInstanceOf(ReservaService.SeguridadException.class);
		}

		@Test
		@DisplayName("el dueno si puede cancelar la suya")
		void cancelaLaPropia() {
			assertThat(reservaService.cancelar(7L, 100L).getEstado()).isEqualTo(Reserva.Estado.cancelada);
		}

		@Test
		@DisplayName("el personal cancela cualquiera")
		void personalCancelaCualquiera() {
			assertThat(reservaService.cancelar(7L, null).getEstado()).isEqualTo(Reserva.Estado.cancelada);
		}

		@Test
		@DisplayName("un cliente no puede subir comprobante a una reserva ajena")
		void noSubeComprobanteAjeno() {
			assertThatThrownBy(() -> reservaService.registrarComprobante(7L, 200L, "/x.pdf", "ref"))
					.isInstanceOf(ReservaService.SeguridadException.class);
		}
	}

	@Nested
	@DisplayName("caducidad de las que no pagan")
	class Caducidad {

		@Test
		@DisplayName("cancela las pendientes vencidas y deja constancia")
		void caducaPendientes() {
			Reserva vencida = new Reserva(salida, maria, 3, new BigDecimal("2550.00"), new BigDecimal("1275.00"));
			vencida.setId(9L);
			when(reservaRepository.pendientesVencidas(any())).thenReturn(List.of(vencida));

			assertThat(reservaService.caducarPendientes()).isEqualTo(1);
			assertThat(vencida.getEstado()).isEqualTo(Reserva.Estado.cancelada);
			assertThat(vencida.getNotas()).contains("automáticamente");
			verify(notificacionService).reservaCaducada(vencida);
		}

		@Test
		@DisplayName("si no hay vencidas no toca nada")
		void sinVencidas() {
			when(reservaRepository.pendientesVencidas(any())).thenReturn(List.of());

			assertThat(reservaService.caducarPendientes()).isZero();
			verify(reservaRepository, never()).save(any());
		}
	}

}
