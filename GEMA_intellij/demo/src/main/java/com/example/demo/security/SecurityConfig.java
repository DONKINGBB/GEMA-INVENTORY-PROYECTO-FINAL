package com.example.demo.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf().disable() // Desactivamos CSRF porque usamos JWT (Stateless)
                .cors().and()
                .authorizeHttpRequests()
                // --- ENDPOINTS PÚBLICOS ---
                .requestMatchers("/api/auth/**", "/uploads/**").permitAll() // Login, Registro, Google e Imágenes abiertos

                // --- SISTEMA DE NEGOCIOS (Onboarding) ---
                .requestMatchers("/api/negocios/**").authenticated() // Cualquier usuario logueado puede crear/unirse

                // --- REGLAS BASADAS EN ROLES ---
                // ID Rol 1 = ROLE_ADMIN | ID Rol 2 = ROLE_SUPERVISOR | ID Rol 3 = ROLE_OPERARIO

                // Los operarios solo pueden consultar, crear productos y hacer salidas de
                // inventario.
                .requestMatchers(HttpMethod.GET, "/api/productos/**", "/api/catalogos/**", "/api/inventario/**",
                        "/api/almacenes/**")
                .hasAnyAuthority("ROLE_ADMIN", "ROLE_SUPERVISOR", "ROLE_OPERARIO")
                .requestMatchers(HttpMethod.POST, "/api/productos/**")
                .hasAnyAuthority("ROLE_ADMIN", "ROLE_SUPERVISOR", "ROLE_OPERARIO")
                .requestMatchers(HttpMethod.POST, "/api/movimiento-inventario/**")
                .hasAnyAuthority("ROLE_ADMIN", "ROLE_SUPERVISOR", "ROLE_OPERARIO")

                // Todo lo de FINANZAS y balances es exclusivo de ADMIN y SUPERVISOR
                .requestMatchers("/api/finanzas/**", "/api/balance-financiero/**", "/api/compras/**")
                .hasAnyAuthority("ROLE_ADMIN", "ROLE_SUPERVISOR")

                // Gestionar Usuarios, Roles o Eliminación forzada (Hard delete) de catálogos
                .requestMatchers("/api/usuarios/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_SUPERVISOR")
                .requestMatchers(HttpMethod.DELETE, "/api/catalogos/**").hasAuthority("ROLE_ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/productos/**").hasAuthority("ROLE_ADMIN")

                // --- DEFAULT ---
                // Cualquier otra petición debe estar al menos autenticada
                .anyRequest().authenticated()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS); // Nunca usar sesiones en el servidor

        // Agregamos el filtro JWT ANTES del filtro predeterminado de Spring Security
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    // Como por ahora las contraseñas no parecen estar encriptadas (Bcrypt), usamos
    // NoOpPasswordEncoder.
    // TODO: A futuro, cambiar a BCryptPasswordEncoder
    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }
}
