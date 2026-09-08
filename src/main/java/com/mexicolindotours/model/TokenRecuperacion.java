package com.mexicolindotours.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Token para restablecer la contrasena de un cliente del sitio.
 *
 * Se guarda el HASH, nunca el token en claro: si alguien lee la tabla no
 * puede secuestrar cuentas, igual que con las contrasenas.
 */
@Entity
@Table(name = "token_recuperacion")
public class TokenRecuperacion {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(optional = false)
	@JoinColumn(name = "usuario_publico_id")
	private UsuarioPublico usuarioPublico;

	@Column(name = "token_hash", nullable = false, unique = true, length = 64)
	private String tokenHash;

	@Column(name = "expira_en", nullable = false)
	private LocalDateTime expiraEn;

	@Column(nullable = false)
	private Boolean usado = false;

	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt = LocalDateTime.now();

	public TokenRecuperacion() {
	}

	public TokenRecuperacion(UsuarioPublico usuarioPublico, String tokenHash, LocalDateTime expiraEn) {
		this.usuarioPublico = usuarioPublico;
		this.tokenHash = tokenHash;
		this.expiraEn = expiraEn;
		this.usado = false;
	}

	public boolean vigente() {
		return !usado && expiraEn.isAfter(LocalDateTime.now());
	}

	public Long getId() { return id; }
	public UsuarioPublico getUsuarioPublico() { return usuarioPublico; }
	public String getTokenHash() { return tokenHash; }
	public LocalDateTime getExpiraEn() { return expiraEn; }
	public Boolean getUsado() { return usado; }
	public void setUsado(Boolean usado) { this.usado = usado; }
	public LocalDateTime getCreatedAt() { return createdAt; }

}
