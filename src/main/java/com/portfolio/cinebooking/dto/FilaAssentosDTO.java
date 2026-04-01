package com.portfolio.cinebooking.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FilaAssentosDTO {

    @NotBlank
    @Size(max = 10)
    private String label;

    @NotEmpty
    private List<@Min(1) Integer> seatNumbers;
}
