package com.portfolio.cinebooking.dto;

import java.util.UUID;

public record AssentoDisponivelResponseDTO(
        UUID showtimeSeatId,
        String fila,
        int numero
) {
}
