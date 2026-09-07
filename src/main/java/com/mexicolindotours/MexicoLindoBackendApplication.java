package com.mexicolindotours;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MexicoLindoBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(MexicoLindoBackendApplication.class, args);
	}

}
