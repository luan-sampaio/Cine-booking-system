package com.portfolio.cinebooking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.portfolio.cinebooking.configuracao.ApiExceptionHandler;
import com.portfolio.cinebooking.dto.LoginRequestDTO;
import com.portfolio.cinebooking.dto.RegistroRequestDTO;
import com.portfolio.cinebooking.modelo.Perfil;
import com.portfolio.cinebooking.modelo.Usuario;
import com.portfolio.cinebooking.repositorio.UsuarioRepository;
import com.portfolio.cinebooking.seguranca.ApiSecurityExceptionHandler;
import com.portfolio.cinebooking.seguranca.FiltroSeguranca;
import com.portfolio.cinebooking.seguranca.TokenServico;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AutenticacaoController.class)
@Import({
        com.portfolio.cinebooking.configuracao.SegurancaConfig.class,
        ApiExceptionHandler.class,
        ApiSecurityExceptionHandler.class
})
class AutenticacaoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @MockitoBean
    private UsuarioRepository usuarioRepository;

    @MockitoBean
    private TokenServico tokenServico;

    @MockitoBean
    private PasswordEncoder passwordEncoder;

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
    void deveRetornarErroPadronizadoQuandoLoginFalhar() throws Exception {
        LoginRequestDTO request = new LoginRequestDTO();
        request.setEmail("cliente@example.com");
        request.setSenha("invalida");

        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.erro").value("Unauthorized"))
                .andExpect(jsonPath("$.mensagem").value("Credenciais inválidas"))
                .andExpect(jsonPath("$.caminho").value("/api/auth/login"))
                .andExpect(jsonPath("$.errosDeCampo").isArray());
    }

    @Test
    void deveRetornarErroPadronizadoQuandoEmailJaEstiverCadastrado() throws Exception {
        RegistroRequestDTO request = new RegistroRequestDTO();
        request.setNome("Cliente");
        request.setEmail("cliente@example.com");
        request.setSenha("segredo");
        request.setPerfil(Perfil.CLIENTE);

        Usuario usuarioExistente = new Usuario();
        usuarioExistente.setId(UUID.randomUUID());
        usuarioExistente.setEmail(request.getEmail());

        when(usuarioRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(usuarioExistente));

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.erro").value("Conflict"))
                .andExpect(jsonPath("$.mensagem").value("Já existe usuário com este e-mail"))
                .andExpect(jsonPath("$.caminho").value("/api/auth/signup"))
                .andExpect(jsonPath("$.errosDeCampo").isArray());

        verify(passwordEncoder, never()).encode(any());
    }
}
