package com.mexicolindotours.service;

import com.mexicolindotours.model.Cliente;
import com.mexicolindotours.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {

	@Autowired
	private ClienteRepository clienteRepository;

	public Cliente crear(String nombre, String telefono, String notas) {
		Cliente cliente = new Cliente(nombre);
		if (telefono != null) cliente.setTelefono(telefono);
		if (notas != null) cliente.setNotas(notas);
		return clienteRepository.save(cliente);
	}

	public Optional<Cliente> obtenerPorId(Long id) {
		return clienteRepository.findById(id);
	}

	public List<Cliente> obtenerTodos() {
		return clienteRepository.findAll();
	}

	public Cliente actualizar(Long id, String nombre, String telefono, String notas) {
		Cliente cliente = clienteRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado"));

		if (nombre != null) cliente.setNombre(nombre);
		if (telefono != null) cliente.setTelefono(telefono);
		if (notas != null) cliente.setNotas(notas);

		cliente.setUpdatedAt(LocalDateTime.now());
		return clienteRepository.save(cliente);
	}

}
