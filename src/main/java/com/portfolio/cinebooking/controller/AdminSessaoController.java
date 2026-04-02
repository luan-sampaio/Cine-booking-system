package com.portfolio.cinebooking.controller;

import com.portfolio.cinebooking.dto.SessaoHorarioRequestDTO;
import com.portfolio.cinebooking.dto.SessaoRequestDTO;
import com.portfolio.cinebooking.dto.SessaoResponseDTO;
import com.portfolio.cinebooking.servico.AdminSessaoServico;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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
@RequestMapping("/api/admin/sessoes")
@RequiredArgsConstructor
@Tag(name = "Admin — Sessões", description = "Agendamento de sessões; evita sobreposição na mesma sala (JWT ADMIN)")
@SecurityRequirement(name = "bearer-jwt")
public class AdminSessaoController {

    private final AdminSessaoServico adminSessaoServico;

    @Operation(summary = "Listar sessões")
    @GetMapping
    public ResponseEntity<List<SessaoResponseDTO>> listar() {
        return ResponseEntity.ok(adminSessaoServico.listar());
    }

    @Operation(summary = "Buscar sessão")
    @GetMapping("/{id}")
    public ResponseEntity<SessaoResponseDTO> buscar(@PathVariable UUID id) {
        return ResponseEntity.ok(adminSessaoServico.buscar(id));
    }

    @Operation(
            summary = "Criar sessão",
            description = "Cria a sessão e gera preemptivamente showtime_seats (AVAILABLE) para cada assento do layout atual da sala. A sala precisa ter assentos configurados."
    )
    @PostMapping
    public ResponseEntity<SessaoResponseDTO> criar(@RequestBody @Valid SessaoRequestDTO dto) {
        SessaoResponseDTO criada = adminSessaoServico.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(criada);
    }

    @Operation(summary = "Atualizar horários da sessão", description = "Filme e sala fixos; apenas início e fim podem mudar, sem sobrepor outra sessão na mesma sala.")
    @PutMapping("/{id}")
    public ResponseEntity<SessaoResponseDTO> atualizar(
            @PathVariable UUID id,
            @RequestBody @Valid SessaoHorarioRequestDTO dto) {
        return ResponseEntity.ok(adminSessaoServico.atualizar(id, dto));
    }

    @Operation(summary = "Remover sessão")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable UUID id) {
        adminSessaoServico.remover(id);
        return ResponseEntity.noContent().build();
    }
}
