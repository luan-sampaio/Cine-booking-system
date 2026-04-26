package com.portfolio.cinebooking.servico;

import com.portfolio.cinebooking.dto.ReservaRequestDTO;
import com.portfolio.cinebooking.dto.ReservaResponseDTO;
import com.portfolio.cinebooking.modelo.Assento;
import com.portfolio.cinebooking.modelo.AssentoSessao;
import com.portfolio.cinebooking.modelo.Perfil;
import com.portfolio.cinebooking.modelo.Reserva;
import com.portfolio.cinebooking.modelo.Sessao;
import com.portfolio.cinebooking.modelo.StatusAssentoSessao;
import com.portfolio.cinebooking.modelo.Usuario;
import com.portfolio.cinebooking.repositorio.AssentoSessaoRepository;
import com.portfolio.cinebooking.repositorio.ReservaAssentoRepository;
import com.portfolio.cinebooking.repositorio.ReservaRepository;
import com.portfolio.cinebooking.repositorio.SessaoRepository;
import com.portfolio.cinebooking.repositorio.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReservaServicoTest {

    @Mock
    private ReservaRepository reservaRepository;

    @Mock
    private ReservaAssentoRepository reservaAssentoRepository;

    @Mock
    private SessaoRepository sessaoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private AssentoSessaoRepository assentoSessaoRepository;

    @InjectMocks
    private ReservaServico reservaServico;

    @Test
    void deveReservarAssentosDisponiveis() {
        UUID sessaoId = UUID.randomUUID();
        UUID usuarioId = UUID.randomUUID();
        UUID assentoSessaoId = UUID.randomUUID();

        Sessao sessao = new Sessao();
        sessao.setId(sessaoId);

        Usuario usuario = new Usuario();
        usuario.setId(usuarioId);
        usuario.setPerfil(Perfil.CLIENTE);
        usuario.setEmail("cliente@example.com");
        usuario.setSenha("segredo");

        Assento assento = new Assento();
        assento.setId(UUID.randomUUID());

        AssentoSessao assentoSessao = new AssentoSessao();
        assentoSessao.setId(assentoSessaoId);
        assentoSessao.setSessao(sessao);
        assentoSessao.setAssento(assento);
        assentoSessao.setStatus(StatusAssentoSessao.AVAILABLE);

        ReservaRequestDTO dto = new ReservaRequestDTO();
        dto.setSessaoId(sessaoId);
        dto.setAssentoSessaoIds(List.of(assentoSessaoId));

        when(sessaoRepository.findById(sessaoId)).thenReturn(Optional.of(sessao));
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(assentoSessaoRepository.findBySessaoIdAndIdIn(sessaoId, java.util.Set.of(assentoSessaoId)))
                .thenReturn(List.of(assentoSessao));
        when(reservaRepository.save(any(Reserva.class))).thenAnswer(invocation -> {
            Reserva reserva = invocation.getArgument(0);
            reserva.setId(UUID.randomUUID());
            return reserva;
        });

        ReservaResponseDTO response = reservaServico.reservar(usuarioId, dto);

        assertThat(response.sessaoId()).isEqualTo(sessaoId);
        assertThat(response.usuarioId()).isEqualTo(usuarioId);
        assertThat(response.assentoSessaoIds()).containsExactly(assentoSessaoId);
        assertThat(assentoSessao.getStatus()).isEqualTo(StatusAssentoSessao.BOOKED);

        ArgumentCaptor<List<AssentoSessao>> captor = ArgumentCaptor.forClass(List.class);
        verify(assentoSessaoRepository).saveAllAndFlush(captor.capture());
        assertThat(captor.getValue()).containsExactly(assentoSessao);
        verify(reservaAssentoRepository).saveAll(any());
    }

    @Test
    void deveRetornarConflitoQuandoAssentoNaoEstiverDisponivel() {
        UUID sessaoId = UUID.randomUUID();
        UUID usuarioId = UUID.randomUUID();
        UUID assentoSessaoId = UUID.randomUUID();

        Sessao sessao = new Sessao();
        sessao.setId(sessaoId);

        Usuario usuario = new Usuario();
        usuario.setId(usuarioId);

        AssentoSessao assentoSessao = new AssentoSessao();
        assentoSessao.setId(assentoSessaoId);
        assentoSessao.setSessao(sessao);
        assentoSessao.setStatus(StatusAssentoSessao.BOOKED);

        ReservaRequestDTO dto = new ReservaRequestDTO();
        dto.setSessaoId(sessaoId);
        dto.setAssentoSessaoIds(List.of(assentoSessaoId));

        when(sessaoRepository.findById(sessaoId)).thenReturn(Optional.of(sessao));
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(assentoSessaoRepository.findBySessaoIdAndIdIn(sessaoId, java.util.Set.of(assentoSessaoId)))
                .thenReturn(List.of(assentoSessao));

        assertThatThrownBy(() -> reservaServico.reservar(usuarioId, dto))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> {
                    ResponseStatusException exception = (ResponseStatusException) ex;
                    assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
                });
    }

    @Test
    void deveTraduzirFalhaDeLockOtimistaEmConflito() {
        UUID sessaoId = UUID.randomUUID();
        UUID usuarioId = UUID.randomUUID();
        UUID assentoSessaoId = UUID.randomUUID();

        Sessao sessao = new Sessao();
        sessao.setId(sessaoId);

        Usuario usuario = new Usuario();
        usuario.setId(usuarioId);

        AssentoSessao assentoSessao = new AssentoSessao();
        assentoSessao.setId(assentoSessaoId);
        assentoSessao.setSessao(sessao);
        assentoSessao.setStatus(StatusAssentoSessao.AVAILABLE);

        ReservaRequestDTO dto = new ReservaRequestDTO();
        dto.setSessaoId(sessaoId);
        dto.setAssentoSessaoIds(List.of(assentoSessaoId));

        when(sessaoRepository.findById(sessaoId)).thenReturn(Optional.of(sessao));
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(assentoSessaoRepository.findBySessaoIdAndIdIn(sessaoId, java.util.Set.of(assentoSessaoId)))
                .thenReturn(List.of(assentoSessao));
        when(assentoSessaoRepository.saveAllAndFlush(any()))
                .thenThrow(new ObjectOptimisticLockingFailureException(AssentoSessao.class, assentoSessaoId));

        assertThatThrownBy(() -> reservaServico.reservar(usuarioId, dto))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> {
                    ResponseStatusException exception = (ResponseStatusException) ex;
                    assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
                });
    }

    @Test
    void deveRetornarNotFoundQuandoSessaoNaoExistir() {
        UUID usuarioId = UUID.randomUUID();
        UUID sessaoId = UUID.randomUUID();

        ReservaRequestDTO dto = new ReservaRequestDTO();
        dto.setSessaoId(sessaoId);
        dto.setAssentoSessaoIds(List.of(UUID.randomUUID()));

        when(sessaoRepository.findById(sessaoId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reservaServico.reservar(usuarioId, dto))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> {
                    ResponseStatusException exception = (ResponseStatusException) ex;
                    assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
                    assertThat(exception.getReason()).isEqualTo("Sessão não encontrada");
                });

        verify(usuarioRepository, never()).findById(any());
    }

    @Test
    void deveRetornarNotFoundQuandoUsuarioNaoExistir() {
        UUID usuarioId = UUID.randomUUID();
        UUID sessaoId = UUID.randomUUID();

        Sessao sessao = new Sessao();
        sessao.setId(sessaoId);

        ReservaRequestDTO dto = new ReservaRequestDTO();
        dto.setSessaoId(sessaoId);
        dto.setAssentoSessaoIds(List.of(UUID.randomUUID()));

        when(sessaoRepository.findById(sessaoId)).thenReturn(Optional.of(sessao));
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reservaServico.reservar(usuarioId, dto))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> {
                    ResponseStatusException exception = (ResponseStatusException) ex;
                    assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
                    assertThat(exception.getReason()).isEqualTo("Usuário não encontrado");
                });

        verify(assentoSessaoRepository, never()).findBySessaoIdAndIdIn(any(), any());
    }

    @Test
    void deveRetornarBadRequestQuandoListaDeAssentosEstiverVazia() {
        UUID usuarioId = UUID.randomUUID();
        UUID sessaoId = UUID.randomUUID();

        Sessao sessao = new Sessao();
        sessao.setId(sessaoId);

        Usuario usuario = new Usuario();
        usuario.setId(usuarioId);

        ReservaRequestDTO dto = new ReservaRequestDTO();
        dto.setSessaoId(sessaoId);
        dto.setAssentoSessaoIds(List.of());

        when(sessaoRepository.findById(sessaoId)).thenReturn(Optional.of(sessao));
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));

        assertThatThrownBy(() -> reservaServico.reservar(usuarioId, dto))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> {
                    ResponseStatusException exception = (ResponseStatusException) ex;
                    assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
                    assertThat(exception.getReason()).isEqualTo("Informe ao menos um assento para reserva");
                });

        verify(assentoSessaoRepository, never()).findBySessaoIdAndIdIn(any(), any());
    }

    @Test
    void deveRetornarBadRequestQuandoListaDeAssentosTiverIdsDuplicados() {
        UUID usuarioId = UUID.randomUUID();
        UUID sessaoId = UUID.randomUUID();
        UUID assentoSessaoId = UUID.randomUUID();

        Sessao sessao = new Sessao();
        sessao.setId(sessaoId);

        Usuario usuario = new Usuario();
        usuario.setId(usuarioId);

        ReservaRequestDTO dto = new ReservaRequestDTO();
        dto.setSessaoId(sessaoId);
        dto.setAssentoSessaoIds(List.of(assentoSessaoId, assentoSessaoId));

        when(sessaoRepository.findById(sessaoId)).thenReturn(Optional.of(sessao));
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));

        assertThatThrownBy(() -> reservaServico.reservar(usuarioId, dto))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> {
                    ResponseStatusException exception = (ResponseStatusException) ex;
                    assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
                    assertThat(exception.getReason()).isEqualTo("A lista de assentos contém IDs duplicados");
                });

        verify(assentoSessaoRepository, never()).findBySessaoIdAndIdIn(any(), any());
    }

    @Test
    void deveRetornarBadRequestQuandoAssentosNaoExistiremOuNaoPertenceremASessao() {
        UUID usuarioId = UUID.randomUUID();
        UUID sessaoId = UUID.randomUUID();
        UUID assentoSessaoId = UUID.randomUUID();

        Sessao sessao = new Sessao();
        sessao.setId(sessaoId);

        Usuario usuario = new Usuario();
        usuario.setId(usuarioId);

        ReservaRequestDTO dto = new ReservaRequestDTO();
        dto.setSessaoId(sessaoId);
        dto.setAssentoSessaoIds(List.of(assentoSessaoId));

        when(sessaoRepository.findById(sessaoId)).thenReturn(Optional.of(sessao));
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(assentoSessaoRepository.findBySessaoIdAndIdIn(sessaoId, java.util.Set.of(assentoSessaoId)))
                .thenReturn(List.of());

        assertThatThrownBy(() -> reservaServico.reservar(usuarioId, dto))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> {
                    ResponseStatusException exception = (ResponseStatusException) ex;
                    assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
                    assertThat(exception.getReason()).isEqualTo("Um ou mais assentos informados não existem ou não pertencem à sessão");
                });

        verify(assentoSessaoRepository, never()).saveAllAndFlush(any());
    }
}
