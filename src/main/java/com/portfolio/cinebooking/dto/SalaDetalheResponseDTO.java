package com.portfolio.cinebooking.dto;

import java.util.List;
import java.util.UUID;

public record SalaDetalheResponseDTO(UUID id, String nome, List<AssentoItemResponseDTO> assentos) {
}
