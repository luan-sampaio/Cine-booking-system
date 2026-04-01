package com.portfolio.cinebooking.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record SessaoResponseDTO(
        UUID id,
        UUID filmeId,
        String filmeTitulo,
        UUID salaId,
        String salaNome,
        LocalDateTime inicio,
        LocalDateTime fim,
        LocalDateTime criadoEm,
        long totalAssentos
) {
}
