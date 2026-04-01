package com.portfolio.cinebooking.controller;

import com.portfolio.cinebooking.dto.FilmeRequestDTO;
import com.portfolio.cinebooking.dto.FilmeResponseDTO;
import com.portfolio.cinebooking.servico.FilmeServico;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/admin/filmes")
@RequiredArgsConstructor
@Tag(name = "Admin — Filmes", description = "CRUD de filmes (JWT com perfil ADMIN)")
@SecurityRequirement(name = "bearer-jwt")
public class AdminFilmeController {

    private final FilmeServico filmeServico;

    @Operation(summary = "Cadastrar filme")
    @PostMapping
    public ResponseEntity<FilmeResponseDTO> criar(@RequestBody @Valid FilmeRequestDTO dto) {
        FilmeResponseDTO criado = filmeServico.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(criado);
    }

    @Operation(summary = "Atualizar filme")
    @PutMapping("/{id}")
    public ResponseEntity<FilmeResponseDTO> atualizar(
            @PathVariable UUID id,
            @RequestBody @Valid FilmeRequestDTO dto) {
        return ResponseEntity.ok(filmeServico.atualizar(id, dto));
    }

    @Operation(summary = "Remover filme")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable UUID id) {
        filmeServico.remover(id);
        return ResponseEntity.noContent().build();
    }
}
