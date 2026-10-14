package com.landguard.security;

import org.springframework.beans.factory.annotation.Value;
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
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;
    private final List<String> allowedOrigins;

    public SecurityConfig(
            JwtAuthenticationFilter jwtFilter,
            @Value("${app.cors.allowed-origins}") String allowedOriginsStr) {
        this.jwtFilter = jwtFilter;
        this.allowedOrigins = Arrays.stream(allowedOriginsStr.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Public Actuator Health Check
                .requestMatchers("/actuator/health", "/actuator/info").permitAll()

                // Public Authentication Endpoints
                .requestMatchers("/api/v1/auth/login", "/api/v1/auth/register", "/api/v1/auth/forgot-password", "/api/v1/auth/reset-password").permitAll()
                .requestMatchers("/api/auth/**").permitAll() // legacy compatibility

                // Public Read / Property Search
                .requestMatchers(HttpMethod.GET, "/api/v1/properties/search-capabilities").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/v1/properties/search").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/properties/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/v1/properties/*/compare").permitAll()
                .requestMatchers("/api/search-parcels", "/api/survey-records", "/api/survey-detail", "/api/calculate", "/api/dtcp-calculator", "/api/validate-link").permitAll()

                // Public Sources Read & Portals
                .requestMatchers(HttpMethod.GET, "/api/v1/sources", "/api/v1/sources/**").permitAll()
                .requestMatchers("/api/portals").permitAll()

                // Static Web Assets & Thymeleaf (if accessed directly on server)
                .requestMatchers("/", "/index.html", "/favicon.ico", "/assets/**", "/css/**", "/js/**", "/img/**").permitAll()

                // Protected Admin Endpoints - Strict RBAC
                .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")

                // All other requests require authentication
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(allowedOrigins);
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept", "X-Requested-With", "Origin"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
