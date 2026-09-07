package com.mexicolindotours.controller;

import com.mexicolindotours.dto.CategoriaPostDTO;
import com.mexicolindotours.dto.EtiquetaDTO;
import com.mexicolindotours.dto.PostDTO;
import com.mexicolindotours.dto.PostResumenDTO;
import com.mexicolindotours.model.CategoriaPost;
import com.mexicolindotours.model.Etiqueta;
import com.mexicolindotours.model.Post;
import com.mexicolindotours.service.CategoriaPostService;
import com.mexicolindotours.service.EtiquetaService;
import com.mexicolindotours.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** Lectura publica del blog: NO requiere token. */
@RestController
@RequestMapping("/blog")
public class BlogController {

	private static final int TAMANO_MAXIMO = 50;

	@Autowired
	private PostService postService;

	@Autowired
	private CategoriaPostService categoriaPostService;

	@Autowired
	private EtiquetaService etiquetaService;

	@GetMapping("/posts")
	public ResponseEntity<?> listar(@RequestParam(required = false) String categoria,
									@RequestParam(required = false) String etiqueta,
									@RequestParam(defaultValue = "0") int pagina,
									@RequestParam(defaultValue = "9") int tamano) {

		if (pagina < 0) pagina = 0;
		if (tamano < 1) tamano = 9;
		if (tamano > TAMANO_MAXIMO) tamano = TAMANO_MAXIMO;

		Page<Post> resultado = postService.listarPublicados(categoria, etiqueta, pagina, tamano);

		Map<String, Object> respuesta = new LinkedHashMap<>();
		respuesta.put("posts", resultado.getContent().stream().map(this::mapToResumen).collect(Collectors.toList()));
		respuesta.put("pagina", resultado.getNumber());
		respuesta.put("tamano", resultado.getSize());
		respuesta.put("totalPosts", resultado.getTotalElements());
		respuesta.put("totalPaginas", resultado.getTotalPages());
		respuesta.put("esUltima", resultado.isLast());

		return ResponseEntity.ok(respuesta);
	}

	@GetMapping("/posts/{slug}")
	public ResponseEntity<?> obtenerPorSlug(@PathVariable String slug) {
		return postService.obtenerPublicadoPorSlug(slug)
				.map(p -> ResponseEntity.ok((Object) mapToDTO(p)))
				.orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body("Post no encontrado"));
	}

	@GetMapping("/categorias")
	public ResponseEntity<?> listarCategorias() {
		List<CategoriaPost> categorias = categoriaPostService.obtenerTodas();
		return ResponseEntity.ok(categorias.stream().map(this::mapCategoriaToDTO).collect(Collectors.toList()));
	}

	@GetMapping("/etiquetas")
	public ResponseEntity<?> listarEtiquetas() {
		List<Etiqueta> etiquetas = etiquetaService.obtenerTodas();
		return ResponseEntity.ok(etiquetas.stream().map(this::mapEtiquetaToDTO).collect(Collectors.toList()));
	}

	private EtiquetaDTO mapEtiquetaToDTO(Etiqueta e) {
		return new EtiquetaDTO(e.getId(), e.getNombre(), e.getSlug(), etiquetaService.contarPostsPublicados(e.getId()));
	}

	private List<EtiquetaDTO> mapEtiquetasDePost(Post p) {
		return p.getEtiquetas().stream()
				.map(e -> new EtiquetaDTO(e.getId(), e.getNombre(), e.getSlug(), null))
				.collect(Collectors.toList());
	}

	private PostResumenDTO mapToResumen(Post p) {
		return new PostResumenDTO(
				p.getId(), p.getTitulo(), p.getSlug(), p.getResumen(), p.getImagenUrl(),
				p.getAutor(), p.getFechaPublicacion(),
				p.getCategoria() != null ? p.getCategoria().getNombre() : null,
				p.getCategoria() != null ? p.getCategoria().getSlug() : null,
				mapEtiquetasDePost(p)
		);
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
