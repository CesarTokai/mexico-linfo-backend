package com.mexicolindotours.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Cliente del sitio publico. Tabla SEPARADA de `usuario` (personal interno)
 * a proposito: un cliente nunca debe poder tocar la gestion interna, ni
 * siquiera si hubiera un error de permisos.
 */
@Entity
@Table(name = "usuario_publico")
public class UsuarioPublico {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 120)
	private String nombre;

	@Column(nullable = false, unique = true, length = 160)
	private String correo;

	@Column(name = "password_hash", nullable = false, length = 255)
	private String passwordHash;

	@Column(length = 30)
	private String telefono;

	@Column(nullable = false)
	private Boolean activo = true;

	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt = LocalDateTime.now();

	public UsuarioPublico() {
	}

	public UsuarioPublico(String nombre, String correo, String passwordHash, String telefono) {
		this.nombre = nombre;
		this.correo = correo;
		this.passwordHash = passwordHash;
		this.telefono = telefono;
		this.activo = true;
	}

	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }
	public String getNombre() { return nombre; }
	public void setNombre(String nombre) { this.nombre = nombre; }
	public String getCorreo() { return correo; }
	public void setCorreo(String correo) { this.correo = correo; }
	public String getPasswordHash() { return passwordHash; }
	public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
	public String getTelefono() { return telefono; }
	public void setTelefono(String telefono) { this.telefono = telefono; }
	public Boolean getActivo() { return activo; }
	public void setActivo(Boolean activo) { this.activo = activo; }
	public LocalDateTime getCreatedAt() { return createdAt; }

}
