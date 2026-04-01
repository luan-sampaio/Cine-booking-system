package com.portfolio.cinebooking.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SalaRequestDTO {

    @NotBlank
    @Size(max = 100)
    private String nome;
}
