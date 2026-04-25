package com.portfolio.cinebooking.seguranca;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;

@Component
public class ApiSecurityExceptionHandler implements AuthenticationEntryPoint, AccessDeniedHandler {

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            org.springframework.security.core.AuthenticationException authException) throws IOException {
        writeError(response, HttpStatus.UNAUTHORIZED, "Autenticação necessária para acessar este recurso", request);
    }

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException) throws IOException {
        writeError(response, HttpStatus.FORBIDDEN, "Acesso negado para este recurso", request);
    }

    private void writeError(
            HttpServletResponse response,
            HttpStatus status,
            String mensagem,
            HttpServletRequest request) throws IOException {
        response.setStatus(status.value());
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write("""
                {"timestamp":"%s","status":%d,"erro":"%s","mensagem":"%s","caminho":"%s","errosDeCampo":[]}
                """.formatted(
                OffsetDateTime.now(),
                status.value(),
                escape(status.getReasonPhrase()),
                escape(mensagem),
                escape(request.getRequestURI())));
    }

    private String escape(String value) {
        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"");
    }
}
