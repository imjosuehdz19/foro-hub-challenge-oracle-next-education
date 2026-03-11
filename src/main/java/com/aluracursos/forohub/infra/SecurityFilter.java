package com.aluracursos.forohub.infra;

import com.aluracursos.forohub.domain.usuario.UsuarioRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UsuarioRepository usuarioRepository;


//    @Override
//    protected void doFilterInternal(HttpServletRequest request,
//                                    HttpServletResponse response,
//                                    FilterChain filterChain) throws ServletException, IOException {
//        var autHeader = request.getHeader("Authorization");
//
//        if (autHeader != null) {
//            var token = autHeader.replace("Bearer ", "");
//            var nombreUsuario = tokenService.getSubject(token);
//
//            if (nombreUsuario != null) {
//                var usuario = usuarioRepository.findByLogin(nombreUsuario);
//                var authentication = new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities());
//                SecurityContextHolder.getContext().setAuthentication(authentication);
//            }
//        }
//
//        filterChain.doFilter(request, response);
//    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        var authHeader = request.getHeader("Authorization");

        // 1. Verificamos que el header no sea nulo Y que empiece con "Bearer "
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            var token = authHeader.replace("Bearer ", "");

            try {
                var nombreUsuario = tokenService.getSubject(token);

                if (nombreUsuario != null) {
                    var usuario = usuarioRepository.findByLogin(nombreUsuario);
                    var authentication = new UsernamePasswordAuthenticationToken(
                            usuario, null, usuario.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (RuntimeException e) {
                // Si el token es inválido o expiró, no detenemos la app,
                // solo no autenticamos al usuario.
                System.out.println("Token inválido o expirado");
            }
        }

        filterChain.doFilter(request, response);
    }
}
