package com.mexicolindotours.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.nio.charset.StandardCharsets;
import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtTokenProvider {

	@Value("${jwt.secret}")
	private String jwtSecret;

	@Value("${jwt.expiration}")
	private long jwtExpiration;

	/** Minimo para HS512, que es el algoritmo que se usa al firmar. */
	private static final int LONGITUD_MINIMA_BYTES = 64;

	/**
	 * Se valida al arrancar: mas vale no levantar que levantar firmando con
	 * un secreto que esta en el repositorio o que cualquiera puede adivinar.
	 */
	@PostConstruct
	public void validarSecreto() {
		if (jwtSecret == null || jwtSecret.isBlank()) {
			throw new IllegalStateException(
					"Falta JWT_SECRET. Genera uno con: openssl rand -base64 64");
		}
		int bytes = jwtSecret.getBytes(StandardCharsets.UTF_8).length;
		if (bytes < LONGITUD_MINIMA_BYTES) {
			throw new IllegalStateException(
					"JWT_SECRET demasiado corto (" + bytes + " bytes). Se requieren al menos "
							+ LONGITUD_MINIMA_BYTES + ". Genera uno con: openssl rand -base64 64");
		}
		if (jwtSecret.contains("change_in_production")) {
			throw new IllegalStateException(
					"JWT_SECRET sigue siendo el valor de ejemplo. Genera uno propio: openssl rand -base64 64");
		}
	}

	private SecretKey getSigningKey() {
		return Keys.hmacShaKeyFor(jwtSecret.getBytes());
	}

	public String generateToken(String email, String rol) {
		Date now = new Date();
		Date expiryDate = new Date(now.getTime() + jwtExpiration);

		return Jwts.builder()
				.subject(email)
				.claim("rol", rol)
				.issuedAt(now)
				.expiration(expiryDate)
				.signWith(getSigningKey())
				.compact();
	}

	public String getEmailFromToken(String token) {
		Claims claims = Jwts.parser()
				.verifyWith(getSigningKey())
				.build()
				.parseSignedClaims(token)
				.getPayload();
		return claims.getSubject();
	}

	public String getRolFromToken(String token) {
		Claims claims = Jwts.parser()
				.verifyWith(getSigningKey())
				.build()
				.parseSignedClaims(token)
				.getPayload();
		return (String) claims.get("rol");
	}

	public boolean validateToken(String token) {
		try {
			Jwts.parser()
					.verifyWith(getSigningKey())
					.build()
					.parseSignedClaims(token);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

}
