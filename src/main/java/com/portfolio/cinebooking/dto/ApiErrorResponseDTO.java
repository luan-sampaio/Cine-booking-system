package com.portfolio.cinebooking.dto;

import org.springframework.http.HttpStatus;

import java.time.OffsetDateTime;
import java.util.List;

public record ApiErrorResponseDTO(
        OffsetDateTime timestamp,
        int status,
        String erro,
        String mensagem,
        String caminho,
        List<ApiFieldErrorDTO> errosDeCampo
) {

    public static ApiErrorResponseDTO of(HttpStatus status, String mensagem, String caminho) {
        return new ApiErrorResponseDTO(
                OffsetDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                mensagem,
                caminho,
                List.of());
    }

    public static ApiErrorResponseDTO of(
            HttpStatus status,
            String mensagem,
            String caminho,
            List<ApiFieldErrorDTO> errosDeCampo) {
        return new ApiErrorResponseDTO(
                OffsetDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                mensagem,
                caminho,
                errosDeCampo);
    }
}
