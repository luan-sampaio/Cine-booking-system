package com.portfolio.cinebooking.controller;

import com.portfolio.cinebooking.dto.FilmeResponseDTO;
import com.portfolio.cinebooking.servico.FilmeServico;
import io.swagger.v3.oas.annotations.Operation;
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
@RequestMapping("/api/filmes")
@RequiredArgsConstructor
@Tag(name = "Filmes", description = "Catálogo para exibição (leitura pública)")
public class FilmeController {

    private final FilmeServico filmeServico;

    @Operation(summary = "Listar filmes")
    @GetMapping
    public ResponseEntity<List<FilmeResponseDTO>> listar() {
        return ResponseEntity.ok(filmeServico.listarParaExibicao());
    }

    @Operation(summary = "Detalhar filme")
    @GetMapping("/{id}")
    public ResponseEntity<FilmeResponseDTO> buscar(@PathVariable UUID id) {
        return ResponseEntity.ok(filmeServico.buscar(id));
    }
}
