package com.mexicolindotours.service;

import com.mexicolindotours.model.Salida;
import com.mexicolindotours.model.Viaje;
import com.mexicolindotours.repository.SalidaRepository;
import com.mexicolindotours.repository.ViajeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;

/**
 * Unico lugar que decide si una camioneta esta libre.
 *
 * Una unidad puede estar ocupada por DOS motivos: una renta privada
 * (`viaje`) o una salida del catalogo publico (`salida`). Antes cada mundo
 * miraba solo el suyo, asi que se podia vender la misma camioneta dos veces
 * para el mismo dia.
 */
@Service
public class DisponibilidadUnidadService {

	@Autowired
	private ViajeRepository viajeRepository;

	@Autowired
	private SalidaRepository salidaRepository;

	/**
	 * Lanza IllegalArgumentException si la unidad ya esta comprometida.
	 * Los ids a excluir sirven al editar algo que ya existe.
	 */
	public void verificarLibre(Long camionetaId, LocalDate desde, LocalDate hasta,
							   Long viajeIdExcluir, Long salidaIdExcluir) {

		if (camionetaId == null || desde == null || hasta == null) return;

		for (Viaje v : viajeRepository.findByCamionetaId(camionetaId)) {
			if (viajeIdExcluir != null && v.getId().equals(viajeIdExcluir)) continue;
			if (v.getEstado() == Viaje.Estado.cancelado) continue;

			if (hayConflicto(desde, hasta, v.getFechaInicio(), v.getFechaFin())) {
				throw new IllegalArgumentException(
						"Camioneta ocupada del " + v.getFechaInicio() + " al " + v.getFechaFin()
								+ " por el viaje #" + v.getId());
			}
		}

		for (Salida s : salidaRepository.findByCamionetaIdAndEstadoNot(camionetaId, Salida.Estado.cancelada)) {
			if (salidaIdExcluir != null && s.getId().equals(salidaIdExcluir)) continue;

			if (hayConflicto(desde, hasta, s.getFechaSalida(), s.getFechaRegreso())) {
				throw new IllegalArgumentException(
						"Camioneta ocupada del " + s.getFechaSalida() + " al " + s.getFechaRegreso()
								+ " por la salida pública #" + s.getId() + " (" + s.getPaquete().getTitulo() + ")");
			}
		}
	}

	/**
	 * Regla C8: una unidad puede REGRESAR el dia X y SALIR el mismo dia X.
	 *
	 * El contacto solo vale entre una llegada real y una salida real, y para
	 * eso ambas ocupaciones deben ser de varios dias en ese punto. Una
	 * ocupacion de un solo dia usa la unidad toda la jornada: no "regresa"
	 * ese dia, esta ahi el dia entero. Sin este matiz se colaba el caso de
	 * una renta de un dia el mismo dia que un tour de dos dias sale, lo que
	 * mandaria la camioneta a dos lugares a la vez.
	 */
	public static boolean hayConflicto(LocalDate aInicio, LocalDate aFin, LocalDate bInicio, LocalDate bFin) {
		boolean seSolapan = !(aFin.isBefore(bInicio) || aInicio.isAfter(bFin));
		if (!seSolapan) return false;

		boolean aRegresaCuandoBSale = aFin.equals(bInicio)
				&& aInicio.isBefore(aFin)
				&& bInicio.isBefore(bFin);

		boolean bRegresaCuandoASale = bFin.equals(aInicio)
				&& bInicio.isBefore(bFin)
				&& aInicio.isBefore(aFin);

		return !(aRegresaCuandoBSale || bRegresaCuandoASale);
	}

}
