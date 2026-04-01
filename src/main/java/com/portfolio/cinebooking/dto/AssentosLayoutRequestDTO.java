package com.portfolio.cinebooking.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AssentosLayoutRequestDTO {

    @NotEmpty
    @Valid
    private List<FilaAssentosDTO> rows;
}
