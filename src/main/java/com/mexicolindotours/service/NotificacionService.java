package com.mexicolindotours.service;

import com.mexicolindotours.model.Reserva;
import com.mexicolindotours.model.UsuarioPublico;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import java.time.format.DateTimeFormatter;

/**
 * Avisos por correo. Es OPCIONAL a proposito: si no esta habilitado o no hay
 * SMTP configurado, se limita a registrar en el log y la operacion sigue.
 * Un fallo de correo nunca debe tumbar una reserva ya cobrada.
 */
@Service
public class NotificacionService {

	private static final Logger log = LoggerFactory.getLogger(NotificacionService.class);
	private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

	/** Puede no existir: la app arranca igual sin configuracion de correo. */
	@Autowired(required = false)
	private JavaMailSender mailSender;

	@Value("${app.notificaciones.habilitado:false}")
	private boolean habilitado;

	@Value("${app.notificaciones.remitente:no-reply@mexicolindotours.com}")
	private String remitente;

	/** Buzon del personal que verifica las transferencias. */
	@Value("${app.notificaciones.correo-operacion:}")
	private String correoOperacion;

	/** Base del sitio para armar el enlace de restablecimiento. */
	@Value("${app.frontend.url:http://localhost:5173}")
	private String urlFrontend;

	public void recuperacionPassword(UsuarioPublico usuario, String token, long minutosVigencia) {
		String enlace = urlFrontend + "/restablecer-password?token=" + token;

		enviar(usuario.getCorreo(),
				"Restablece tu contraseña — Mexico Lindo Tours",
				"Hola " + usuario.getNombre() + ",\n\n"
						+ "Recibimos una solicitud para restablecer tu contraseña. "
						+ "Abre este enlace para elegir una nueva:\n\n"
						+ enlace + "\n\n"
						+ "El enlace vence en " + minutosVigencia + " minutos y sirve una sola vez.\n\n"
						+ "Si no fuiste tú, ignora este correo: tu contraseña no cambia.");
	}

	public void reservaCreada(Reserva r) {
		enviar(r.getUsuarioPublico().getCorreo(),
				"Apartamos tus lugares — " + r.getSalida().getPaquete().getTitulo(),
				"Hola " + r.getUsuarioPublico().getNombre() + ",\n\n"
						+ "Apartamos " + r.getNumAsientos() + " lugar(es) para "
						+ r.getSalida().getPaquete().getTitulo() + " del "
						+ r.getSalida().getFechaSalida().format(FECHA) + ".\n\n"
						+ "Total: $" + r.getMontoTotal() + "\n"
						+ "Anticipo para asegurarlos: $" + r.getMontoAnticipo() + "\n\n"
						+ "Sube tu comprobante de transferencia para confirmar. "
						+ "Si no lo recibimos a tiempo, los lugares se liberan.");

		avisarOperacion("Nueva reserva #" + r.getId(),
				r.getUsuarioPublico().getNombre() + " aparto " + r.getNumAsientos()
						+ " lugar(es) en " + r.getSalida().getPaquete().getTitulo()
						+ " (" + r.getSalida().getFechaSalida().format(FECHA) + ").");
	}

	public void comprobanteRecibido(Reserva r) {
		avisarOperacion("Comprobante por verificar — reserva #" + r.getId(),
				r.getUsuarioPublico().getNombre() + " subio un comprobante"
						+ (r.getReferenciaTransferencia() != null ? " (ref " + r.getReferenciaTransferencia() + ")" : "")
						+ ". Saldo pendiente: $" + r.saldoPendiente() + ".");
	}

	public void reservaConfirmada(Reserva r) {
		enviar(r.getUsuarioPublico().getCorreo(),
				"Reserva confirmada — " + r.getSalida().getPaquete().getTitulo(),
				"Hola " + r.getUsuarioPublico().getNombre() + ",\n\n"
						+ "Verificamos tu pago y tus " + r.getNumAsientos() + " lugar(es) estan asegurados para el "
						+ r.getSalida().getFechaSalida().format(FECHA) + ".\n\n"
						+ "Pagado: $" + r.getMontoPagado() + "\n"
						+ "Saldo por liquidar: $" + r.saldoPendiente());
	}

	public void reservaCaducada(Reserva r) {
		enviar(r.getUsuarioPublico().getCorreo(),
				"Liberamos tus lugares — " + r.getSalida().getPaquete().getTitulo(),
				"Hola " + r.getUsuarioPublico().getNombre() + ",\n\n"
						+ "No recibimos tu comprobante a tiempo, asi que liberamos los lugares "
						+ "que habias apartado para el " + r.getSalida().getFechaSalida().format(FECHA) + ".\n\n"
						+ "Puedes volver a apartar si aun hay disponibilidad.");
	}

	private void avisarOperacion(String asunto, String cuerpo) {
		if (correoOperacion == null || correoOperacion.isBlank()) return;
		enviar(correoOperacion, asunto, cuerpo);
	}

	private void enviar(String destino, String asunto, String cuerpo) {
		if (!habilitado || mailSender == null) {
			log.info("[notificacion desactivada] para={} asunto={}", destino, asunto);
			return;
		}
		if (destino == null || destino.isBlank()) return;

		try {
			SimpleMailMessage mensaje = new SimpleMailMessage();
			mensaje.setFrom(remitente);
			mensaje.setTo(destino);
			mensaje.setSubject(asunto);
			mensaje.setText(cuerpo);
			mailSender.send(mensaje);
			log.info("Notificacion enviada a {}: {}", destino, asunto);
		} catch (Exception e) {
			// Nunca propagar: el correo no puede tumbar una reserva ya cobrada.
			log.error("No se pudo enviar la notificacion a {}: {}", destino, e.getMessage());
		}
	}

}
