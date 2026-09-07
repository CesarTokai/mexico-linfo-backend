package com.mexicolindotours.controller;

import com.mexicolindotours.dto.*;
import com.mexicolindotours.model.CategoriaPost;
import com.mexicolindotours.model.Etiqueta;
import com.mexicolindotours.model.Post;
import com.mexicolindotours.service.AlmacenamientoImagenService;
import com.mexicolindotours.service.CategoriaPostService;
import com.mexicolindotours.service.EtiquetaService;
import com.mexicolindotours.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** Administracion del blog: requiere token (ADMIN o GESTOR). */
@RestController
@RequestMapping("/admin/blog")
public class BlogAdminController {

	@Autowired
	private PostService postService;

	@Autowired
	private CategoriaPostService categoriaPostService;

	@Autowired
	private EtiquetaService etiquetaService;

	@Autowired
	private AlmacenamientoImagenService almacenamientoImagenService;

	@GetMapping("/posts")
	public ResponseEntity<?> listarTodos(@RequestParam(defaultValue = "0") int pagina,
										 @RequestParam(defaultValue = "20") int tamano) {
		if (pagina < 0) pagina = 0;
		if (tamano < 1 || tamano > 100) tamano = 20;

		Page<Post> resultado = postService.listarTodos(pagina, tamano);

		Map<String, Object> respuesta = new LinkedHashMap<>();
		respuesta.put("posts", resultado.getContent().stream().map(this::mapToDTO).collect(Collectors.toList()));
		respuesta.put("pagina", resultado.getNumber());
		respuesta.put("totalPosts", resultado.getTotalElements());
		respuesta.put("totalPaginas", resultado.getTotalPages());

		return ResponseEntity.ok(respuesta);
	}

	@GetMapping("/posts/{id}")
	public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
		return postService.obtenerPorId(id)
				.map(p -> ResponseEntity.ok((Object) mapToDTO(p)))
				.orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body("Post no encontrado"));
	}

	@PostMapping("/posts")
	public ResponseEntity<?> crear(@RequestBody PostCreateRequest request) {
		try {
			Post post = postService.crear(request.getTitulo(), request.getSlug(), request.getResumen(),
					request.getContenido(), request.getImagenUrl(), request.getAutor(),
					request.getCategoriaId(), request.getEtiquetaIds(), request.getEstado(), request.getFechaPublicacion());
			return ResponseEntity.status(HttpStatus.CREATED).body(mapToDTO(post));
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	@PutMapping("/posts/{id}")
	public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody PostUpdateRequest request) {
		try {
			Post post = postService.actualizar(id, request.getTitulo(), request.getSlug(), request.getResumen(),
					request.getContenido(), request.getImagenUrl(), request.getAutor(),
					request.getCategoriaId(), request.getEtiquetaIds(), request.getEstado(), request.getFechaPublicacion());
			return ResponseEntity.ok(mapToDTO(post));
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	@DeleteMapping("/posts/{id}")
	public ResponseEntity<?> eliminar(@PathVariable Long id) {
		try {
			postService.eliminar(id);
			return ResponseEntity.ok("Post eliminado");
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
	}

	@PostMapping("/imagenes")
	public ResponseEntity<?> subirImagen(@RequestParam("archivo") MultipartFile archivo) {
		try {
			String url = almacenamientoImagenService.guardar(archivo);
			Map<String, String> respuesta = new LinkedHashMap<>();
			respuesta.put("imagenUrl", url);
			return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		} catch (IllegalStateException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		}
	}

	@DeleteMapping("/imagenes/{nombreArchivo}")
	public ResponseEntity<?> eliminarImagen(@PathVariable String nombreArchivo) {
		try {
			boolean borrada = almacenamientoImagenService.eliminar(nombreArchivo);
			return borrada
					? ResponseEntity.ok("Imagen eliminada")
					: ResponseEntity.status(HttpStatus.NOT_FOUND).body("Imagen no encontrada");
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	@GetMapping("/categorias")
	public ResponseEntity<?> listarCategorias() {
		List<CategoriaPost> categorias = categoriaPostService.obtenerTodas();
		return ResponseEntity.ok(categorias.stream().map(this::mapCategoriaToDTO).collect(Collectors.toList()));
	}

	@PostMapping("/categorias")
	public ResponseEntity<?> crearCategoria(@RequestBody CategoriaPostCreateRequest request) {
		try {
			CategoriaPost categoria = categoriaPostService.crear(request.getNombre(), request.getSlug(), request.getDescripcion());
			return ResponseEntity.status(HttpStatus.CREATED).body(mapCategoriaToDTO(categoria));
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	@PutMapping("/categorias/{id}")
	public ResponseEntity<?> actualizarCategoria(@PathVariable Long id, @RequestBody CategoriaPostCreateRequest request) {
		try {
			CategoriaPost categoria = categoriaPostService.actualizar(id, request.getNombre(), request.getDescripcion());
			return ResponseEntity.ok(mapCategoriaToDTO(categoria));
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
	}

	@DeleteMapping("/categorias/{id}")
	public ResponseEntity<?> eliminarCategoria(@PathVariable Long id) {
		try {
			categoriaPostService.eliminar(id);
			return ResponseEntity.ok("Categoría eliminada");
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	@GetMapping("/etiquetas")
	public ResponseEntity<?> listarEtiquetas() {
		List<Etiqueta> etiquetas = etiquetaService.obtenerTodas();
		return ResponseEntity.ok(etiquetas.stream().map(this::mapEtiquetaToDTO).collect(Collectors.toList()));
	}

	@PostMapping("/etiquetas")
	public ResponseEntity<?> crearEtiqueta(@RequestBody EtiquetaCreateRequest request) {
		try {
			Etiqueta etiqueta = etiquetaService.crear(request.getNombre(), request.getSlug());
			return ResponseEntity.status(HttpStatus.CREATED).body(mapEtiquetaToDTO(etiqueta));
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	@PutMapping("/etiquetas/{id}")
	public ResponseEntity<?> actualizarEtiqueta(@PathVariable Long id, @RequestBody EtiquetaCreateRequest request) {
		try {
			Etiqueta etiqueta = etiquetaService.actualizar(id, request.getNombre());
			return ResponseEntity.ok(mapEtiquetaToDTO(etiqueta));
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
	}

	@DeleteMapping("/etiquetas/{id}")
	public ResponseEntity<?> eliminarEtiqueta(@PathVariable Long id) {
		try {
			etiquetaService.eliminar(id);
			return ResponseEntity.ok("Etiqueta eliminada");
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
	}

	private EtiquetaDTO mapEtiquetaToDTO(Etiqueta e) {
		return new EtiquetaDTO(e.getId(), e.getNombre(), e.getSlug(), etiquetaService.contarPostsPublicados(e.getId()));
	}

	private List<EtiquetaDTO> mapEtiquetasDePost(Post p) {
		return p.getEtiquetas().stream()
				.map(e -> new EtiquetaDTO(e.getId(), e.getNombre(), e.getSlug(), null))
				.collect(Collectors.toList());
	}

	private PostDTO mapToDTO(Post p) {
		return new PostDTO(
				p.getId(), p.getTitulo(), p.getSlug(), p.getResumen(), p.getContenido(), p.getImagenUrl(),
				p.getAutor(), p.getFechaPublicacion(), p.getEstado().toString(),
				p.getCategoria() != null ? p.getCategoria().getId() : null,
				p.getCategoria() != null ? p.getCategoria().getNombre() : null,
				p.getCategoria() != null ? p.getCategoria().getSlug() : null,
				mapEtiquetasDePost(p)
		);
	}

	private CategoriaPostDTO mapCategoriaToDTO(CategoriaPost c) {
		return new CategoriaPostDTO(c.getId(), c.getNombre(), c.getSlug(), c.getDescripcion(),
				categoriaPostService.contarPostsPublicados(c.getId()));
	}

}
