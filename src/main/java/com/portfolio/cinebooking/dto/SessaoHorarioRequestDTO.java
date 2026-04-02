package com.portfolio.cinebooking.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class SessaoHorarioRequestDTO {

    @NotNull
    private LocalDateTime inicio;

    @NotNull
    private LocalDateTime fim;
}
