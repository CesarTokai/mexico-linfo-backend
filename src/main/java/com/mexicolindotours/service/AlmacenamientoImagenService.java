package com.mexicolindotours.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * Guarda las imagenes destacadas del blog en disco.
 * El nombre original del archivo NUNCA se usa: se genera uno aleatorio para
 * evitar path traversal, colisiones y nombres con caracteres raros.
 */
@Service
public class AlmacenamientoImagenService {

	private static final List<String> EXTENSIONES_PERMITIDAS = List.of("jpg", "jpeg", "png", "webp", "gif");
	private static final List<String> TIPOS_PERMITIDOS = List.of("image/jpeg", "image/png", "image/webp", "image/gif");

	@Value("${app.uploads.dir:uploads}")
	private String directorioUploads;

	@Value("${app.uploads.url-publica:/uploads}")
	private String urlPublica;

	private Path raiz;

	@PostConstruct
	public void inicializar() {
		this.raiz = Paths.get(directorioUploads).toAbsolutePath().normalize();
		try {
			Files.createDirectories(raiz);
		} catch (IOException e) {
			throw new IllegalStateException("No se pudo crear el directorio de uploads: " + raiz, e);
		}
	}

	private static final List<String> EXT_COMPROBANTE = List.of("jpg", "jpeg", "png", "webp", "pdf");
	private static final List<String> TIPOS_COMPROBANTE = List.of("image/jpeg", "image/png", "image/webp", "application/pdf");

	/**
	 * Comprobantes de transferencia: admite tambien PDF. Se guardan en una
	 * subcarpeta propia porque NO son contenido publico como las del blog.
	 */
	public String guardarComprobante(MultipartFile archivo) {
		return guardarEn(archivo, "comprobantes", TIPOS_COMPROBANTE, EXT_COMPROBANTE);
	}

	/** Devuelve la URL publica con la que el post debe referenciar la imagen. */
	public String guardar(MultipartFile archivo) {
		if (archivo == null || archivo.isEmpty()) {
			throw new IllegalArgumentException("No se recibió ningún archivo");
		}

		String tipo = archivo.getContentType();
		if (tipo == null || !TIPOS_PERMITIDOS.contains(tipo.toLowerCase(Locale.ROOT))) {
			throw new IllegalArgumentException("Tipo de archivo no permitido: " + tipo + " (solo imágenes JPG, PNG, WEBP o GIF)");
		}

		String extension = extraerExtension(archivo.getOriginalFilename());
		if (!EXTENSIONES_PERMITIDAS.contains(extension)) {
			throw new IllegalArgumentException("Extensión no permitida: " + extension);
		}

		String nombreGenerado = UUID.randomUUID().toString().replace("-", "") + "." + extension;
		Path destino = raiz.resolve(nombreGenerado).normalize();

		// Cinturon de seguridad: el destino debe quedar dentro de la raiz.
		if (!destino.startsWith(raiz)) {
			throw new IllegalArgumentException("Ruta de destino inválida");
		}

		try (InputStream in = archivo.getInputStream()) {
			Files.copy(in, destino, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			throw new IllegalStateException("No se pudo guardar la imagen: " + e.getMessage(), e);
		}

		return urlPublica + "/" + nombreGenerado;
	}

	private String guardarEn(MultipartFile archivo, String subcarpeta,
							 List<String> tiposPermitidos, List<String> extensionesPermitidas) {
		if (archivo == null || archivo.isEmpty()) {
			throw new IllegalArgumentException("No se recibió ningún archivo");
		}

		String tipo = archivo.getContentType();
		if (tipo == null || !tiposPermitidos.contains(tipo.toLowerCase(Locale.ROOT))) {
			throw new IllegalArgumentException("Tipo de archivo no permitido: " + tipo);
		}

		String extension = extraerExtension(archivo.getOriginalFilename());
		if (!extensionesPermitidas.contains(extension)) {
			throw new IllegalArgumentException("Extensión no permitida: " + extension);
		}

		Path carpeta = raiz.resolve(subcarpeta).normalize();
		if (!carpeta.startsWith(raiz)) {
			throw new IllegalArgumentException("Ruta de destino inválida");
		}

		try {
			Files.createDirectories(carpeta);
		} catch (IOException e) {
			throw new IllegalStateException("No se pudo preparar la carpeta: " + e.getMessage(), e);
		}

		String nombreGenerado = UUID.randomUUID().toString().replace("-", "") + "." + extension;
		Path destino = carpeta.resolve(nombreGenerado).normalize();
		if (!destino.startsWith(carpeta)) {
			throw new IllegalArgumentException("Ruta de destino inválida");
		}

		try (InputStream in = archivo.getInputStream()) {
			Files.copy(in, destino, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			throw new IllegalStateException("No se pudo guardar el archivo: " + e.getMessage(), e);
		}

		return urlPublica + "/" + subcarpeta + "/" + nombreGenerado;
	}

	public boolean eliminar(String nombreArchivo) {
		if (nombreArchivo == null || nombreArchivo.isBlank()) return false;

		// Solo se acepta un nombre simple, nunca una ruta.
		if (nombreArchivo.contains("/") || nombreArchivo.contains("\\") || nombreArchivo.contains("..")) {
			throw new IllegalArgumentException("Nombre de archivo inválido");
		}

		Path objetivo = raiz.resolve(nombreArchivo).normalize();
		if (!objetivo.startsWith(raiz)) {
			throw new IllegalArgumentException("Ruta de destino inválida");
		}

		try {
			return Files.deleteIfExists(objetivo);
		} catch (IOException e) {
			throw new IllegalStateException("No se pudo eliminar la imagen: " + e.getMessage(), e);
		}
	}

	public Path getRaiz() {
		return raiz;
	}

	private String extraerExtension(String nombreOriginal) {
		if (nombreOriginal == null) return "";
		int punto = nombreOriginal.lastIndexOf('.');
		if (punto < 0 || punto == nombreOriginal.length() - 1) return "";
		return nombreOriginal.substring(punto + 1).toLowerCase(Locale.ROOT);
	}

}
