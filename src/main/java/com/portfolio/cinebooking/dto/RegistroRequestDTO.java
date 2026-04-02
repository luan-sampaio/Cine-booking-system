package com.portfolio.cinebooking.dto;

import jakarta.validation.constraints.Email;
import com.portfolio.cinebooking.modelo.Perfil;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegistroRequestDTO {
    @NotBlank
    private String nome;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String senha;

    private Perfil perfil;
}
