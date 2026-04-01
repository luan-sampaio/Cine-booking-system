package com.portfolio.cinebooking.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record FilmeResponseDTO(
        UUID id,
        String titulo,
        String synopsis,
        int duracaoMinutos,
        String classificacaoEtaria,
        LocalDateTime criadoEm
) {
}
