package com.portfolio.cinebooking.controller;

import com.portfolio.cinebooking.dto.AssentoDisponivelResponseDTO;
import com.portfolio.cinebooking.servico.SessaoConsultaServico;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/sessoes")
@RequiredArgsConstructor
@Tag(name = "Sessões", description = "Consulta de sessões e assentos (usuário autenticado)")
@SecurityRequirement(name = "bearer-jwt")
public class SessaoConsultaController {

    private final SessaoConsultaServico sessaoConsultaServico;

    @Operation(summary = "Listar assentos disponíveis", description = "Retorna apenas linhas de showtime_seats com status AVAILABLE.")
    @GetMapping("/{sessaoId}/assentos-disponiveis")
    public ResponseEntity<List<AssentoDisponivelResponseDTO>> assentosDisponiveis(@PathVariable UUID sessaoId) {
        return ResponseEntity.ok(sessaoConsultaServico.listarAssentosDisponiveis(sessaoId));
    }
}
