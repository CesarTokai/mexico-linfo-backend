package com.mexicolindotours.service;

import com.mexicolindotours.model.UsuarioPublico;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * El registro rapido solo exige nombre + telefono + password (familias que
 * solo manejan WhatsApp). El correo es opcional y se completa despues.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("Registro de clientes: telefono obligatorio, correo opcional")
class UsuarioPublicoServiceTest {

	@Mock private UsuarioPublicoRepository usuarioPublicoRepository;

	private PasswordEncoder passwordEncoder;
	private UsuarioPublicoService usuarioPublicoService;

	@BeforeEach
	void setUp() {
		passwordEncoder = new BCryptPasswordEncoder();
		usuarioPublicoService = new UsuarioPublicoService();
		org.springframework.test.util.ReflectionTestUtils.setField(usuarioPublicoService, "usuarioPublicoRepository", usuarioPublicoRepository);
		org.springframework.test.util.ReflectionTestUtils.setField(usuarioPublicoService, "passwordEncoder", passwordEncoder);
		when(usuarioPublicoRepository.save(any(UsuarioPublico.class))).thenAnswer(i -> i.getArgument(0));
	}

	@Nested
	@DisplayName("registro")
	class Registro {

		@Test
		@DisplayName("funciona sin correo: solo nombre, telefono y password")
		void seRegistraSinCorreo() {
			UsuarioPublico u = usuarioPublicoService.registrar("Familia López", "7771234567", "contrasena123", null);

			assertThat(u.getTelefono()).isEqualTo("7771234567");
			assertThat(u.getCorreo()).isNull();
		}

		@Test
		@DisplayName("exige telefono")
		void exigeTelefono() {
			assertThatThrownBy(() -> usuarioPublicoService.registrar("Ana", null, "contrasena123", null))
					.hasMessageContaining("teléfono");
			assertThatThrownBy(() -> usuarioPublicoService.registrar("Ana", "  ", "contrasena123", null))
					.hasMessageContaining("teléfono");
		}

		@Test
		@DisplayName("no permite dos cuentas con el mismo telefono")
		void rechazaTelefonoDuplicado() {
			when(usuarioPublicoRepository.existsByTelefono("7771234567")).thenReturn(true);

			assertThatThrownBy(() -> usuarioPublicoService.registrar("Otra", "7771234567", "contrasena123", null))
					.hasMessageContaining("ya está registrado");
		}

		@Test
		@DisplayName("si da correo, valida formato y unicidad")
		void validaCorreoSiSeDa() {
			assertThatThrownBy(() -> usuarioPublicoService.registrar("Ana", "7771111111", "contrasena123", "no-es-correo"))
					.hasMessageContaining("inválido");

			UsuarioPublico otraCuenta = new UsuarioPublico("Otra", "7770000000", "hash", "ana@correo.com");
			otraCuenta.setId(99L);
			when(usuarioPublicoRepository.findByCorreo("ana@correo.com")).thenReturn(Optional.of(otraCuenta));

			assertThatThrownBy(() -> usuarioPublicoService.registrar("Ana", "7771111111", "contrasena123", "ana@correo.com"))
					.hasMessageContaining("correo ya está registrado");
		}
	}

	@Nested
	@DisplayName("obtenerOCrearPorTelefono (reservas manuales)")
	class ObtenerOCrear {

		@Test
		@DisplayName("reutiliza la cuenta si el telefono ya existe")
		void reutilizaExistente() {
			UsuarioPublico existente = new UsuarioPublico("Familia García", "7779999999", "hash", null);
			existente.setId(50L);
			when(usuarioPublicoRepository.findByTelefono("7779999999")).thenReturn(Optional.of(existente));

			UsuarioPublico r = usuarioPublicoService.obtenerOCrearPorTelefono(null, "7779999999", null);

			assertThat(r.getId()).isEqualTo(50L);
			verify(usuarioPublicoRepository, never()).save(any());
		}

		@Test
		@DisplayName("crea una cuenta nueva si el telefono no existe")
		void creaNueva() {
			when(usuarioPublicoRepository.findByTelefono("7778888888")).thenReturn(Optional.empty());

			UsuarioPublico r = usuarioPublicoService.obtenerOCrearPorTelefono("Familia Ruiz", "7778888888", null);

			assertThat(r.getTelefono()).isEqualTo("7778888888");
			assertThat(r.getNombre()).isEqualTo("Familia Ruiz");
			assertThat(r.getPasswordHash()).isNotBlank();
		}

		@Test
		@DisplayName("exige nombre si va a crear una cuenta nueva")
		void exigeNombreParaCrear() {
			when(usuarioPublicoRepository.findByTelefono("7770000001")).thenReturn(Optional.empty());

			assertThatThrownBy(() -> usuarioPublicoService.obtenerOCrearPorTelefono(null, "7770000001", null))
					.hasMessageContaining("nombre");
		}
	}

	@Nested
	@DisplayName("login por identificador")
	class LoginPorIdentificador {

		@Test
		@DisplayName("un identificador con @ busca por correo")
		void buscaPorCorreo() {
			usuarioPublicoService.obtenerPorIdentificador("ana@correo.com");
			verify(usuarioPublicoRepository).findByCorreoAndActivoTrue("ana@correo.com");
		}

		@Test
		@DisplayName("un identificador sin @ busca por telefono")
		void buscaPorTelefono() {
			usuarioPublicoService.obtenerPorIdentificador("7771234567");
			verify(usuarioPublicoRepository).findByTelefonoAndActivoTrue("7771234567");
		}
	}

	@Nested
	@DisplayName("completar el correo despues")
	class CompletarCorreo {

		@Test
		@DisplayName("agrega el correo a una cuenta que no tenia")
		void agregaCorreo() {
			UsuarioPublico u = new UsuarioPublico("Familia López", "7771234567", "hash", null);
			u.setId(1L);
			when(usuarioPublicoRepository.findById(1L)).thenReturn(Optional.of(u));

			UsuarioPublico actualizado = usuarioPublicoService.completarCorreo(1L, "familia@correo.com");

			assertThat(actualizado.getCorreo()).isEqualTo("familia@correo.com");
		}
	}

}
