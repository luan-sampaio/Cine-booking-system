package com.portfolio.cinebooking.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FilmeRequestDTO {

    @NotBlank
    @Size(max = 255)
    private String titulo;

    private String synopsis;

    @NotNull
    @Min(1)
    private Integer duracaoMinutos;

    @Size(max = 20)
    private String classificacaoEtaria;
}
