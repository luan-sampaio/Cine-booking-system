package com.portfolio.cinebooking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.portfolio.cinebooking.dto.ReservaRequestDTO;
import com.portfolio.cinebooking.dto.ReservaResponseDTO;
import com.portfolio.cinebooking.modelo.Perfil;
import com.portfolio.cinebooking.modelo.StatusReserva;
import com.portfolio.cinebooking.modelo.Usuario;
import com.portfolio.cinebooking.seguranca.FiltroSeguranca;
import com.portfolio.cinebooking.servico.ReservaServico;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReservaController.class)
@Import(com.portfolio.cinebooking.configuracao.SegurancaConfig.class)
class ReservaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private ReservaServico reservaServico;

    @MockitoBean
    private FiltroSeguranca filtroSeguranca;

    @BeforeEach
    void configurarFiltroPassThrough() throws Exception {
        doAnswer(invocation -> {
            ServletRequest request = invocation.getArgument(0);
            ServletResponse response = invocation.getArgument(1);
            FilterChain chain = invocation.getArgument(2);
            chain.doFilter(request, response);
            return null;
        }).when(filtroSeguranca).doFilter(any(), any(), any());
    }

    @Test
    void devePermitirReservaParaClienteAutenticado() throws Exception {
        UUID usuarioId = UUID.randomUUID();
        UUID sessaoId = UUID.randomUUID();
        UUID assentoSessaoId = UUID.randomUUID();
        UUID reservaId = UUID.randomUUID();

        Usuario cliente = novoUsuario(usuarioId, Perfil.CLIENTE);

        ReservaRequestDTO request = new ReservaRequestDTO();
        request.setSessaoId(sessaoId);
        request.setAssentoSessaoIds(List.of(assentoSessaoId));

        ReservaResponseDTO response = new ReservaResponseDTO(
                reservaId,
                usuarioId,
                sessaoId,
                StatusReserva.PENDING,
                LocalDateTime.of(2026, 4, 25, 10, 0),
                List.of(assentoSessaoId));

        when(reservaServico.reservar(eq(usuarioId), any(ReservaRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/reservas")
                        .with(SecurityMockMvcRequestPostProcessors.user(cliente))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(reservaId.toString()))
                .andExpect(jsonPath("$.usuarioId").value(usuarioId.toString()))
                .andExpect(jsonPath("$.sessaoId").value(sessaoId.toString()))
                .andExpect(jsonPath("$.status").value(StatusReserva.PENDING.name()))
                .andExpect(jsonPath("$.assentoSessaoIds[0]").value(assentoSessaoId.toString()));

        verify(reservaServico).reservar(eq(usuarioId), any(ReservaRequestDTO.class));
    }

    @Test
    void deveNegarReservaParaAdmin() throws Exception {
        UUID usuarioId = UUID.randomUUID();
        UUID sessaoId = UUID.randomUUID();
        UUID assentoSessaoId = UUID.randomUUID();

        Usuario admin = novoUsuario(usuarioId, Perfil.ADMIN);

        ReservaRequestDTO request = new ReservaRequestDTO();
        request.setSessaoId(sessaoId);
        request.setAssentoSessaoIds(List.of(assentoSessaoId));

        mockMvc.perform(post("/api/reservas")
                        .with(SecurityMockMvcRequestPostProcessors.user(admin))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());

        verify(reservaServico, never()).reservar(any(), any());
    }

    private Usuario novoUsuario(UUID id, Perfil perfil) {
        Usuario usuario = new Usuario();
        usuario.setId(id);
        usuario.setNome("Usuário Teste");
        usuario.setEmail("teste@example.com");
        usuario.setSenha("segredo");
        usuario.setPerfil(perfil);
        return usuario;
    }
}
