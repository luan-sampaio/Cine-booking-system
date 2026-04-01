package com.portfolio.cinebooking.controller;

import com.portfolio.cinebooking.dto.AssentosLayoutRequestDTO;
import com.portfolio.cinebooking.dto.SalaDetalheResponseDTO;
import com.portfolio.cinebooking.dto.SalaRequestDTO;
import com.portfolio.cinebooking.dto.SalaResumoResponseDTO;
import com.portfolio.cinebooking.servico.AdminSalaServico;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/salas")
@RequiredArgsConstructor
@Tag(name = "Admin — Salas", description = "Cadastro de salas e layout de assentos (JWT com perfil ADMIN)")
@SecurityRequirement(name = "bearer-jwt")
public class AdminSalaController {

    private final AdminSalaServico adminSalaServico;

    @Operation(summary = "Criar sala")
    @PostMapping
    public ResponseEntity<SalaResumoResponseDTO> criar(@RequestBody @Valid SalaRequestDTO dto) {
        SalaResumoResponseDTO criada = adminSalaServico.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(criada);
    }

    @Operation(summary = "Listar salas")
    @GetMapping
    public ResponseEntity<List<SalaResumoResponseDTO>> listar() {
        return ResponseEntity.ok(adminSalaServico.listar());
    }

    @Operation(summary = "Detalhar sala e assentos")
    @GetMapping("/{id}")
    public ResponseEntity<SalaDetalheResponseDTO> buscar(@PathVariable UUID id) {
        return ResponseEntity.ok(adminSalaServico.buscar(id));
    }

    @Operation(
            summary = "Definir layout de assentos",
            description = "Substitui todos os assentos da sala pelo layout enviado (filas × números por fila)."
    )
    @PutMapping("/{id}/assentos/layout")
    public ResponseEntity<SalaDetalheResponseDTO> definirLayout(
            @PathVariable UUID id,
            @RequestBody @Valid AssentosLayoutRequestDTO dto) {
        return ResponseEntity.ok(adminSalaServico.definirLayoutAssentos(id, dto));
    }
}
