package com.portfolio.cinebooking.dto;

public record ApiFieldErrorDTO(
        String campo,
        String mensagem
) {
}
