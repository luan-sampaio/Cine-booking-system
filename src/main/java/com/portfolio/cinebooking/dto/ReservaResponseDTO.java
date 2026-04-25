package com.portfolio.cinebooking.dto;

import com.portfolio.cinebooking.modelo.StatusReserva;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record ReservaResponseDTO(
        UUID id,
        UUID usuarioId,
        UUID sessaoId,
        StatusReserva status,
        LocalDateTime criadoEm,
        List<UUID> assentoSessaoIds
) {
}
