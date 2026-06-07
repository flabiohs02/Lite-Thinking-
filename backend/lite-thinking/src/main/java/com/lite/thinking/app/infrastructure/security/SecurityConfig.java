package com.lite.thinking.app.infrastructure.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final CustomUserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/api/v1/auth/**",
                    "/swagger-ui/**",
                    "/v3/api-docs/**",
                    "/swagger-ui.html",
                    "/api-docs/**"
                ).permitAll()
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/v1/catalog", "/api/v1/catalog/**").permitAll()
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/v1/companies", "/api/v1/companies/**").permitAll()
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/v1/products", "/api/v1/products/**").hasAnyRole("ADMIN", "CLIENT", "EXTERNAL")
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/v1/categories", "/api/v1/categories/**").hasAnyRole("ADMIN", "CLIENT", "EXTERNAL")
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/v1/inventories", "/api/v1/inventories/**").hasAnyRole("ADMIN", "CLIENT", "EXTERNAL")
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/v1/users/identification/**").hasAnyRole("ADMIN", "CLIENT", "EXTERNAL")
                .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/v1/orders").hasAnyRole("ADMIN", "CLIENT", "EXTERNAL")
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/v1/orders/user/**").hasAnyRole("ADMIN", "CLIENT", "EXTERNAL")
                .requestMatchers("/api/v1/dashboard/**").hasAnyRole("ADMIN", "CLIENT", "EXTERNAL")
                .requestMatchers("/api/v1/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Collections.singletonList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Cache-Control"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
