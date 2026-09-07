package com.mexicolindotours.config;

import com.mexicolindotours.service.AlmacenamientoImagenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/** Sirve las imagenes subidas del blog como archivos estaticos. */
@Configuration
public class WebConfig implements WebMvcConfigurer {

	@Autowired
	private AlmacenamientoImagenService almacenamientoImagenService;

	@Value("${app.uploads.url-publica:/uploads}")
	private String urlPublica;

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler(urlPublica + "/**")
				.addResourceLocations("file:" + almacenamientoImagenService.getRaiz().toString() + "/");
	}

}
