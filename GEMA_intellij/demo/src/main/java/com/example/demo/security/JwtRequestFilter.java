package com.example.demo.security;

import com.example.demo.model.Usuario;
import com.example.demo.repository.UsuarioRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        final String authorizationHeader = request.getHeader("Authorization");

        String username = null;
        String jwt = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            try {
                username = jwtUtil.extractUsername(jwt);
            } catch (io.jsonwebtoken.security.SignatureException e) {
                logger.error("JWT Error: Signature Mismatch. Secret changed or token tampered.");
                response.setStatus(401);
                response.setContentType("application/json");
                response.getWriter().write("{\"success\": false, \"message\": \"Firma de sesión inválida. Inicia sesión de nuevo.\"}");
                return;
            } catch (io.jsonwebtoken.ExpiredJwtException e) {
                logger.error("JWT Error: Token Expired.");
                response.setStatus(401);
                response.setContentType("application/json");
                response.getWriter().write("{\"success\": false, \"message\": \"Sesión expirada.\"}");
                return;
            } catch (Exception e) {
                logger.error("JWT Error: " + e.getMessage());
            }
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            Usuario usuario = usuarioRepository.findByCorreoAndActivoTrue(username).orElse(null);

            if (usuario != null && jwtUtil.validateToken(jwt, usuario.getCorreo())) {

                // Mapping 1=ROLE_ADMIN, 2=ROLE_SUPERVISOR, 3=ROLE_OPERARIO
                String roleName = "ROLE_OPERARIO";
                if (usuario.getIdRol() != null && usuario.getIdRol() == 1)
                    roleName = "ROLE_ADMIN";
                else if (usuario.getIdRol() != null && usuario.getIdRol() == 2)
                    roleName = "ROLE_SUPERVISOR";
                
                System.err.println("SEC_DEBUG: User=" + username + " | ServiceRole=" + roleName + " | ID_ROL=" + usuario.getIdRol());

                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                        usuario, null, Collections.singletonList(new SimpleGrantedAuthority(roleName)));
                usernamePasswordAuthenticationToken
                        .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        }
        chain.doFilter(request, response);
    }
}
