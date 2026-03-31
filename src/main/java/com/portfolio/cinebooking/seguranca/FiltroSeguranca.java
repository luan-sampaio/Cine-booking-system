package com.portfolio.cinebooking.seguranca;

import com.portfolio.cinebooking.repositorio.UsuarioRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class FiltroSeguranca extends OncePerRequestFilter {

    private final TokenServico tokenServico;
    private final UsuarioRepository usuarioRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var token = recuperarToken(request);
        
        if (token != null) {
            var email = tokenServico.validarToken(token);
            var usuario = usuarioRepository.findByEmail(email).orElse(null);
            
            if (usuario != null) {
                var autenticacao = new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(autenticacao);
            }
        }
        
        filterChain.doFilter(request, response);
    }

    private String recuperarToken(HttpServletRequest request) {
        var cabecalhoAutorizacao = request.getHeader("Authorization");
        if (cabecalhoAutorizacao == null) return null;
        return cabecalhoAutorizacao.replace("Bearer ", "");
    }
}
