package com.mexicolindotours.service;

import com.mexicolindotours.dto.CalendarioDTO;
import com.mexicolindotours.dto.CalendarioDTO.OcupacionDTO;
import com.mexicolindotours.model.Camioneta;
import com.mexicolindotours.model.Salida;
import com.mexicolindotours.model.Viaje;
import com.mexicolindotours.repository.CamionetaRepository;
import com.mexicolindotours.repository.SalidaRepository;
import com.mexicolindotours.repository.ViajeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CalendarioService {

	@Autowired
	private CamionetaRepository camionetaRepository;

	@Autowired
	private ViajeRepository viajeRepository;

	@Autowired
	private SalidaRepository salidaRepository;

	public List<CalendarioDTO> obtenerCalendario(LocalDate desde, LocalDate hasta) {
		List<Camioneta> camionetas = camionetaRepository.findAll();

		return camionetas.stream()
				.map(c -> construirCalendarioCamioneta(c, desde, hasta))
				.collect(Collectors.toList());
	}

	private CalendarioDTO construirCalendarioCamioneta(Camioneta camioneta, LocalDate desde, LocalDate hasta) {
		List<Viaje> todosLosViajes = viajeRepository.findByCamionetaId(camioneta.getId());

		List<OcupacionDTO> ocupaciones = new ArrayList<>(todosLosViajes.stream()
				.filter(v -> !v.getEstado().equals(Viaje.Estado.cancelado))
				.filter(v -> tieneTraslape(v.getFechaInicio(), v.getFechaFin(), desde, hasta))
				.map(v -> new OcupacionDTO(
						v.getId(),
						v.getFechaInicio(),
						v.getFechaFin(),
						v.getCliente().getId(),
						v.getCliente().getNombre(),
						v.getConcepto(),
						v.getEstado().toString()
				))
				.collect(Collectors.toList()));

		// Las salidas publicas ocupan la unidad igual que una renta privada:
		// si no aparecieran aqui, el personal creeria que esta libre.
		for (Salida sal : salidaRepository.findByCamionetaIdAndEstadoNot(camioneta.getId(), Salida.Estado.cancelada)) {
			if (!tieneTraslape(sal.getFechaSalida(), sal.getFechaRegreso(), desde, hasta)) continue;

			ocupaciones.add(new OcupacionDTO(
					sal.getId(),
					sal.getFechaSalida(),
					sal.getFechaRegreso(),
					null,
					"Salida pública: " + sal.getPaquete().getTitulo(),
					sal.getPaquete().getDestino(),
					"salida_" + sal.getEstado()
			));
		}

		CalendarioDTO dto = new CalendarioDTO(
				camioneta.getId(),
				camioneta.getNombre(),
				camioneta.getEstado().toString(),
				ocupaciones
		);

		return dto;
	}

	private boolean tieneTraslape(LocalDate vi1, LocalDate vf1, LocalDate vi2, LocalDate vf2) {
		return !(vf1.isBefore(vi2) || vi1.isAfter(vf2));
	}

}
