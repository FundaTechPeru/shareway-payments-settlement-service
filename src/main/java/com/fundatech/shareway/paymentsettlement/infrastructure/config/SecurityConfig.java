package com.fundatech.shareway.paymentsettlement.infrastructure.config;

import com.fundatech.shareway.paymentsettlement.infrastructure.security.JwtAuthenticationFilter;
import com.fundatech.shareway.paymentsettlement.infrastructure.security.JwtService;
import com.fundatech.shareway.paymentsettlement.infrastructure.security.SecurityErrorWriter;
import com.fundatech.shareway.paymentsettlement.infrastructure.security.UserRole;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuración de Spring Security: API sin estado, cerrada por defecto salvo los endpoints públicos.
 */
@Configuration
public class SecurityConfig {

    private static final String[] PUBLIC_PATHS = {
            "/actuator/health", "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", "/error"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, SecurityErrorWriter errorWriter,
                                                   JwtService jwtService) {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(handling -> handling
                        .authenticationEntryPoint(errorWriter)
                        .accessDeniedHandler(errorWriter))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PUBLIC_PATHS).permitAll()
                        .requestMatchers("/api/v1/payment-methods/**").hasRole(UserRole.PASSENGER.name())
                        .anyRequest().authenticated())
                .addFilterBefore(new JwtAuthenticationFilter(jwtService, errorWriter),
                        UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
