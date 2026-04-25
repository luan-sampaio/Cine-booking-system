package com.portfolio.cinebooking.controller;

import com.portfolio.cinebooking.dto.ReservaRequestDTO;
import com.portfolio.cinebooking.dto.ReservaResponseDTO;
import com.portfolio.cinebooking.modelo.Usuario;
import com.portfolio.cinebooking.servico.ReservaServico;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reservas")
@RequiredArgsConstructor
@Tag(name = "Reservas", description = "Reserva de assentos para clientes autenticados")
@SecurityRequirement(name = "bearer-jwt")
public class ReservaController {

    private final ReservaServico reservaServico;

    @Operation(
            summary = "Criar reserva",
            description = "Reserva assentos de uma sessão para o cliente autenticado. O payload usa IDs de showtime_seats (assentoSessaoIds), que representam assentos dentro de uma sessão específica."
    )
    @PostMapping
    public ResponseEntity<ReservaResponseDTO> criar(
            @AuthenticationPrincipal Usuario usuario,
            @RequestBody @Valid ReservaRequestDTO dto) {
        ReservaResponseDTO reserva = reservaServico.reservar(usuario.getId(), dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(reserva);
    }
}
