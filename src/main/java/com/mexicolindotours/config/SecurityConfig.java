package com.mexicolindotours.config;

import com.mexicolindotours.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    /** Origenes permitidos, configurables por entorno (CORS_ORIGENES). */
    @org.springframework.beans.factory.annotation.Value("${app.cors.origenes:http://localhost:*}")
    private String origenesPermitidos;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // ---------- personal interno ----------
                .requestMatchers("/auth/login").permitAll()
                // El alta se abre SOLO si no existe ningun usuario (bootstrap).
                // Con la base ya poblada, AuthController exige ADMIN.
                .requestMatchers("/auth/crear-usuario").permitAll()

                // ---------- abierto al publico ----------
                .requestMatchers("/publico/auth/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/publico/paquetes/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/publico/salidas/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/publico/filtros").permitAll()
                .requestMatchers(HttpMethod.GET, "/blog/**").permitAll()

                // Los comprobantes de pago NO son contenido publico: llevan
                // datos bancarios. Exigen sesion aunque su nombre sea aleatorio.
                .requestMatchers(HttpMethod.GET, "/uploads/comprobantes/**").authenticated()
                .requestMatchers(HttpMethod.GET, "/uploads/**").permitAll()

                // ---------- cuenta del cliente ----------
                .requestMatchers("/publico/mi/**").hasRole("CLIENTE")

                // ---------- todo lo demas es operacion interna ----------
                // Explicito a proposito: con `authenticated()` a secas, un
                // cliente con cuenta podria entrar a viajes, cuentas y clientes.
                .anyRequest().hasAnyRole("ADMIN", "GESTOR")
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(
                Arrays.stream(origenesPermitidos.split(","))
                        .map(String::trim)
                        .filter(o -> !o.isEmpty())
                        .toList());
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
