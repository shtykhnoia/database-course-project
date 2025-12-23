package com.example.ticketingsystem.security;

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

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**", "/swagger-resources/**", "/webjars/**").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/events/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/venues/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/organizers/**").permitAll()

                        .requestMatchers("/api/users/**").hasAuthority("ROLE_ADMIN")

                        // Управление организаторами - только ADMIN
                        .requestMatchers(HttpMethod.POST, "/api/organizers/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/organizers/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/organizers/**").hasAuthority("ROLE_ADMIN")

                        // Мероприятия - ORGANIZER и ADMIN
                        .requestMatchers(HttpMethod.POST, "/api/events/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_ORGANIZER")
                        .requestMatchers(HttpMethod.PUT, "/api/events/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_ORGANIZER")
                        .requestMatchers(HttpMethod.PATCH, "/api/events/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_ORGANIZER")
                        .requestMatchers(HttpMethod.DELETE, "/api/events/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_ORGANIZER")

                        // Остальные DELETE - ORGANIZER и ADMIN
                        .requestMatchers(HttpMethod.DELETE, "/api/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_ORGANIZER")

                        .requestMatchers("/api/orders/**").authenticated()
                        .requestMatchers("/api/tickets/**").authenticated()
                        .requestMatchers("/api/payments/**").authenticated()

                        .requestMatchers("/api/statistics/**").authenticated()

                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
