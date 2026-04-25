package com.portfolio.cinebooking.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class ReservaRequestDTO {

    @NotNull
    private UUID sessaoId;

    @NotEmpty
    private List<UUID> assentoSessaoIds;
}
