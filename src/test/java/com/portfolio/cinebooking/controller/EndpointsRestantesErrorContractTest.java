package com.portfolio.cinebooking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.portfolio.cinebooking.configuracao.ApiExceptionHandler;
import com.portfolio.cinebooking.dto.AssentosLayoutRequestDTO;
import com.portfolio.cinebooking.dto.AssentoDisponivelResponseDTO;
import com.portfolio.cinebooking.dto.FilaAssentosDTO;
import com.portfolio.cinebooking.dto.FilmeRequestDTO;
import com.portfolio.cinebooking.dto.FilmeResponseDTO;
import com.portfolio.cinebooking.dto.SessaoRequestDTO;
import com.portfolio.cinebooking.modelo.Perfil;
import com.portfolio.cinebooking.modelo.Usuario;
import com.portfolio.cinebooking.seguranca.ApiSecurityExceptionHandler;
import com.portfolio.cinebooking.seguranca.FiltroSeguranca;
import com.portfolio.cinebooking.servico.AdminSalaServico;
import com.portfolio.cinebooking.servico.AdminSessaoServico;
import com.portfolio.cinebooking.servico.FilmeServico;
import com.portfolio.cinebooking.servico.SessaoConsultaServico;
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
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({
        AdminFilmeController.class,
        AdminSalaController.class,
        AdminSessaoController.class,
        FilmeController.class,
        SessaoConsultaController.class
})
@Import({
        com.portfolio.cinebooking.configuracao.SegurancaConfig.class,
        ApiExceptionHandler.class,
        ApiSecurityExceptionHandler.class
})
class EndpointsRestantesErrorContractTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = JsonMapper.builder()
            .findAndAddModules()
            .build();

    @MockitoBean
    private FilmeServico filmeServico;

    @MockitoBean
    private AdminSalaServico adminSalaServico;

    @MockitoBean
    private AdminSessaoServico adminSessaoServico;

    @MockitoBean
    private SessaoConsultaServico sessaoConsultaServico;

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
    void deveRetornarCatalogoPublicoDeFilmesComSucesso() throws Exception {
        UUID filmeId = UUID.randomUUID();

        when(filmeServico.listarParaExibicao()).thenReturn(List.of(
                new FilmeResponseDTO(
                        filmeId,
                        "Filme em Cartaz",
                        "Sinopse",
                        125,
                        "16",
                        LocalDateTime.of(2026, 4, 26, 9, 0))));

        mockMvc.perform(get("/api/filmes"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(filmeId.toString()))
                .andExpect(jsonPath("$[0].titulo").value("Filme em Cartaz"))
                .andExpect(jsonPath("$[0].duracaoMinutos").value(125));
    }

    @Test
    void deveRetornarErroPadronizadoQuandoAdminNaoEstiverAutenticado() throws Exception {
        FilmeRequestDTO request = new FilmeRequestDTO();
        request.setTitulo("Filme Teste");
        request.setDuracaoMinutos(120);

        mockMvc.perform(post("/api/admin/filmes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.erro").value("Unauthorized"))
                .andExpect(jsonPath("$.mensagem").value("Autenticação necessária para acessar este recurso"))
                .andExpect(jsonPath("$.caminho").value("/api/admin/filmes"))
                .andExpect(jsonPath("$.errosDeCampo").isArray());

        verify(filmeServico, never()).criar(any());
    }

    @Test
    void deveRetornarErroPadronizadoQuandoClienteAcessarEndpointAdmin() throws Exception {
        FilmeRequestDTO request = new FilmeRequestDTO();
        request.setTitulo("Filme Teste");
        request.setDuracaoMinutos(120);

        mockMvc.perform(post("/api/admin/filmes")
                        .with(SecurityMockMvcRequestPostProcessors.user(novoUsuario(Perfil.CLIENTE)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.erro").value("Forbidden"))
                .andExpect(jsonPath("$.mensagem").value("Acesso negado para este recurso"))
                .andExpect(jsonPath("$.caminho").value("/api/admin/filmes"))
                .andExpect(jsonPath("$.errosDeCampo").isArray());

        verify(filmeServico, never()).criar(any());
    }

    @Test
    void deveRetornarErroPadronizadoQuandoPayloadDeFilmeForInvalido() throws Exception {
        FilmeRequestDTO request = new FilmeRequestDTO();
        request.setTitulo(" ");
        request.setDuracaoMinutos(0);

        mockMvc.perform(post("/api/admin/filmes")
                        .with(SecurityMockMvcRequestPostProcessors.user(novoUsuario(Perfil.ADMIN)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.erro").value("Bad Request"))
                .andExpect(jsonPath("$.mensagem").value("Dados da requisição são inválidos"))
                .andExpect(jsonPath("$.caminho").value("/api/admin/filmes"))
                .andExpect(jsonPath("$.errosDeCampo").isArray())
                .andExpect(jsonPath("$.errosDeCampo[?(@.campo == 'titulo')]").isNotEmpty())
                .andExpect(jsonPath("$.errosDeCampo[?(@.campo == 'duracaoMinutos')]").isNotEmpty());
    }

    @Test
    void deveRetornarErroPadronizadoQuandoSalaNaoForEncontradaNoAdmin() throws Exception {
        UUID salaId = UUID.randomUUID();

        when(adminSalaServico.buscar(salaId))
                .thenThrow(new ResponseStatusException(NOT_FOUND, "Sala não encontrada"));

        mockMvc.perform(get("/api/admin/salas/{id}", salaId)
                        .with(SecurityMockMvcRequestPostProcessors.user(novoUsuario(Perfil.ADMIN))))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.erro").value("Not Found"))
                .andExpect(jsonPath("$.mensagem").value("Sala não encontrada"))
                .andExpect(jsonPath("$.caminho").value("/api/admin/salas/" + salaId))
                .andExpect(jsonPath("$.errosDeCampo").isArray());
    }

    @Test
    void deveRetornarErroPadronizadoQuandoLayoutDaSalaForInvalido() throws Exception {
        UUID salaId = UUID.randomUUID();
        AssentosLayoutRequestDTO request = new AssentosLayoutRequestDTO();
        FilaAssentosDTO fila = new FilaAssentosDTO();
        fila.setLabel("");
        fila.setSeatNumbers(List.of(0));
        request.setRows(List.of(fila));

        mockMvc.perform(put("/api/admin/salas/{id}/assentos/layout", salaId)
                        .with(SecurityMockMvcRequestPostProcessors.user(novoUsuario(Perfil.ADMIN)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.erro").value("Bad Request"))
                .andExpect(jsonPath("$.mensagem").value("Dados da requisição são inválidos"))
                .andExpect(jsonPath("$.caminho").value("/api/admin/salas/" + salaId + "/assentos/layout"))
                .andExpect(jsonPath("$.errosDeCampo").isArray())
                .andExpect(jsonPath("$.errosDeCampo[?(@.campo == 'rows[0].label')]").isNotEmpty())
                .andExpect(jsonPath("$.errosDeCampo[?(@.campo == 'rows[0].seatNumbers[0]')]").isNotEmpty());

        verify(adminSalaServico, never()).definirLayoutAssentos(any(), any());
    }

    @Test
    void deveRetornarErroPadronizadoQuandoSessaoConflitarNoAdmin() throws Exception {
        SessaoRequestDTO request = new SessaoRequestDTO();
        request.setFilmeId(UUID.randomUUID());
        request.setSalaId(UUID.randomUUID());
        request.setInicio(LocalDateTime.of(2026, 4, 26, 18, 0));
        request.setFim(LocalDateTime.of(2026, 4, 26, 20, 0));

        when(adminSessaoServico.criar(any(SessaoRequestDTO.class)))
                .thenThrow(new ResponseStatusException(CONFLICT, "Já existe sessão nesta sala que se sobrepõe a este intervalo de horário"));

        mockMvc.perform(post("/api/admin/sessoes")
                        .with(SecurityMockMvcRequestPostProcessors.user(novoUsuario(Perfil.ADMIN)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.erro").value("Conflict"))
                .andExpect(jsonPath("$.mensagem").value("Já existe sessão nesta sala que se sobrepõe a este intervalo de horário"))
                .andExpect(jsonPath("$.caminho").value("/api/admin/sessoes"))
                .andExpect(jsonPath("$.errosDeCampo").isArray());
    }

    @Test
    void deveRetornarErroPadronizadoQuandoFilmeNaoForEncontradoNoCatalogoPublico() throws Exception {
        UUID filmeId = UUID.randomUUID();

        when(filmeServico.buscar(filmeId))
                .thenThrow(new ResponseStatusException(NOT_FOUND, "Filme não encontrado"));

        mockMvc.perform(get("/api/filmes/{id}", filmeId))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.erro").value("Not Found"))
                .andExpect(jsonPath("$.mensagem").value("Filme não encontrado"))
                .andExpect(jsonPath("$.caminho").value("/api/filmes/" + filmeId))
                .andExpect(jsonPath("$.errosDeCampo").isArray());
    }

    @Test
    void deveRetornarErroPadronizadoQuandoUuidDoFilmeForInvalido() throws Exception {
        mockMvc.perform(get("/api/filmes/nao-e-uuid"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.erro").value("Bad Request"))
                .andExpect(jsonPath("$.mensagem").value("Parâmetros da requisição são inválidos"))
                .andExpect(jsonPath("$.caminho").value("/api/filmes/nao-e-uuid"))
                .andExpect(jsonPath("$.errosDeCampo[0].campo").value("id"))
                .andExpect(jsonPath("$.errosDeCampo[0].mensagem").value("deve ser um UUID válido"));

        verify(filmeServico, never()).buscar(any());
    }

    @Test
    void deveRetornarErroPadronizadoQuandoUsuarioNaoEstiverAutenticadoNaConsultaDeSessao() throws Exception {
        UUID sessaoId = UUID.randomUUID();

        mockMvc.perform(get("/api/sessoes/{sessaoId}/assentos-disponiveis", sessaoId))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.erro").value("Unauthorized"))
                .andExpect(jsonPath("$.mensagem").value("Autenticação necessária para acessar este recurso"))
                .andExpect(jsonPath("$.caminho").value("/api/sessoes/" + sessaoId + "/assentos-disponiveis"))
                .andExpect(jsonPath("$.errosDeCampo").isArray());

        verify(sessaoConsultaServico, never()).listarAssentosDisponiveis(any());
    }

    @Test
    void deveRetornarErroPadronizadoQuandoSessaoNaoForEncontradaNaConsulta() throws Exception {
        UUID sessaoId = UUID.randomUUID();

        when(sessaoConsultaServico.listarAssentosDisponiveis(sessaoId))
                .thenThrow(new ResponseStatusException(NOT_FOUND, "Sessão não encontrada"));

        mockMvc.perform(get("/api/sessoes/{sessaoId}/assentos-disponiveis", sessaoId)
                        .with(SecurityMockMvcRequestPostProcessors.user(novoUsuario(Perfil.CLIENTE))))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.erro").value("Not Found"))
                .andExpect(jsonPath("$.mensagem").value("Sessão não encontrada"))
                .andExpect(jsonPath("$.caminho").value("/api/sessoes/" + sessaoId + "/assentos-disponiveis"))
                .andExpect(jsonPath("$.errosDeCampo").isArray());
    }

    @Test
    void deveRetornarAssentosDisponiveisQuandoConsultaForBemSucedida() throws Exception {
        UUID sessaoId = UUID.randomUUID();
        UUID showtimeSeatId = UUID.randomUUID();

        when(sessaoConsultaServico.listarAssentosDisponiveis(sessaoId))
                .thenReturn(List.of(new AssentoDisponivelResponseDTO(showtimeSeatId, "B", 7)));

        mockMvc.perform(get("/api/sessoes/{sessaoId}/assentos-disponiveis", sessaoId)
                        .with(SecurityMockMvcRequestPostProcessors.user(novoUsuario(Perfil.CLIENTE))))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].showtimeSeatId").value(showtimeSeatId.toString()))
                .andExpect(jsonPath("$[0].fila").value("B"))
                .andExpect(jsonPath("$[0].numero").value(7));
    }

    @Test
    void deveRetornarCorpoPadronizadoQuandoFilmeForCriadoComSucesso() throws Exception {
        UUID filmeId = UUID.randomUUID();
        FilmeRequestDTO request = new FilmeRequestDTO();
        request.setTitulo("Filme Teste");
        request.setDuracaoMinutos(110);
        request.setClassificacaoEtaria("14");

        when(filmeServico.criar(any(FilmeRequestDTO.class)))
                .thenReturn(new FilmeResponseDTO(
                        filmeId,
                        request.getTitulo(),
                        null,
                        request.getDuracaoMinutos(),
                        request.getClassificacaoEtaria(),
                        LocalDateTime.of(2026, 4, 26, 12, 0)));

        mockMvc.perform(post("/api/admin/filmes")
                        .with(SecurityMockMvcRequestPostProcessors.user(novoUsuario(Perfil.ADMIN)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(filmeId.toString()))
                .andExpect(jsonPath("$.titulo").value("Filme Teste"));
    }

    @Test
    void deveRetornarErroPadronizadoQuandoCorpoJsonForMalformado() throws Exception {
        mockMvc.perform(post("/api/admin/filmes")
                        .with(SecurityMockMvcRequestPostProcessors.user(novoUsuario(Perfil.ADMIN)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"titulo\":"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.erro").value("Bad Request"))
                .andExpect(jsonPath("$.mensagem").value("Corpo da requisição inválido ou malformado"))
                .andExpect(jsonPath("$.caminho").value("/api/admin/filmes"))
                .andExpect(jsonPath("$.errosDeCampo").isArray());

        verify(filmeServico, never()).criar(any());
    }

    private Usuario novoUsuario(Perfil perfil) {
        Usuario usuario = new Usuario();
        usuario.setId(UUID.randomUUID());
        usuario.setNome("Usuário Teste");
        usuario.setEmail("teste@example.com");
        usuario.setSenha("segredo");
        usuario.setPerfil(perfil);
        return usuario;
    }
}
