package com.portfolio.cinebooking.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class SessaoRequestDTO {

    @NotNull
    private UUID filmeId;

    @NotNull
    private UUID salaId;

    @NotNull
    private LocalDateTime inicio;

    @NotNull
    private LocalDateTime fim;
}
