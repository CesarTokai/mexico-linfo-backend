package com.mexicolindotours.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "favorito", uniqueConstraints = @UniqueConstraint(columnNames = {"usuario_publico_id", "paquete_id"}))
public class Favorito {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(optional = false)
	@JoinColumn(name = "usuario_publico_id")
	private UsuarioPublico usuarioPublico;

	@ManyToOne(optional = false)
	@JoinColumn(name = "paquete_id")
	private Paquete paquete;

	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt = LocalDateTime.now();

	public Favorito() {
	}

	public Favorito(UsuarioPublico usuarioPublico, Paquete paquete) {
		this.usuarioPublico = usuarioPublico;
		this.paquete = paquete;
	}

	public Long getId() { return id; }
	public UsuarioPublico getUsuarioPublico() { return usuarioPublico; }
	public Paquete getPaquete() { return paquete; }
	public LocalDateTime getCreatedAt() { return createdAt; }

}
