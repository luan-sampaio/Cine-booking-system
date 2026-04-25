package com.portfolio.cinebooking.servico;

import com.portfolio.cinebooking.dto.ReservaRequestDTO;
import com.portfolio.cinebooking.dto.ReservaResponseDTO;
import com.portfolio.cinebooking.modelo.AssentoSessao;
import com.portfolio.cinebooking.modelo.Reserva;
import com.portfolio.cinebooking.modelo.ReservaAssento;
import com.portfolio.cinebooking.modelo.ReservaAssentoId;
import com.portfolio.cinebooking.modelo.Sessao;
import com.portfolio.cinebooking.modelo.StatusAssentoSessao;
import com.portfolio.cinebooking.modelo.Usuario;
import com.portfolio.cinebooking.repositorio.AssentoSessaoRepository;
import com.portfolio.cinebooking.repositorio.ReservaAssentoRepository;
import com.portfolio.cinebooking.repositorio.ReservaRepository;
import com.portfolio.cinebooking.repositorio.SessaoRepository;
import com.portfolio.cinebooking.repositorio.UsuarioRepository;
import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReservaServico {

    private final ReservaRepository reservaRepository;
    private final ReservaAssentoRepository reservaAssentoRepository;
    private final SessaoRepository sessaoRepository;
    private final UsuarioRepository usuarioRepository;
    private final AssentoSessaoRepository assentoSessaoRepository;

    @Transactional
    public ReservaResponseDTO reservar(UUID usuarioId, ReservaRequestDTO dto) {
        try {
            return reservarInternamente(usuarioId, dto);
        } catch (ObjectOptimisticLockingFailureException | OptimisticLockException ex) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Um ou mais assentos acabaram de ser reservados por outro usuário. Atualize a disponibilidade e tente novamente.",
                    ex);
        }
    }

    private ReservaResponseDTO reservarInternamente(UUID usuarioId, ReservaRequestDTO dto) {
        Sessao sessao = sessaoRepository.findById(dto.getSessaoId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sessão não encontrada"));

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));

        Set<UUID> assentoSessaoIds = normalizarAssentoSessaoIds(dto);
        List<AssentoSessao> assentosSessao = assentoSessaoRepository.findBySessaoIdAndIdIn(sessao.getId(), assentoSessaoIds);

        if (assentosSessao.size() != assentoSessaoIds.size()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Um ou mais assentos informados não existem ou não pertencem à sessão");
        }

        List<AssentoSessao> indisponiveis = assentosSessao.stream()
                .filter(assentoSessao -> assentoSessao.getStatus() != StatusAssentoSessao.AVAILABLE)
                .toList();

        if (!indisponiveis.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Um ou mais assentos não estão mais disponíveis para reserva");
        }

        for (AssentoSessao assentoSessao : assentosSessao) {
            assentoSessao.setStatus(StatusAssentoSessao.BOOKED);
        }
        assentoSessaoRepository.saveAllAndFlush(assentosSessao);

        Reserva reserva = new Reserva();
        reserva.setUsuario(usuario);
        reserva.setSessao(sessao);
        Reserva reservaSalva = reservaRepository.save(reserva);

        List<ReservaAssento> itensReserva = assentosSessao.stream()
                .map(assentoSessao -> criarItemReserva(reservaSalva, assentoSessao))
                .toList();
        reservaAssentoRepository.saveAll(itensReserva);
        reservaSalva.setAssentos(itensReserva);

        return paraDTO(reservaSalva, assentoSessaoIds.stream().toList());
    }

    private Set<UUID> normalizarAssentoSessaoIds(ReservaRequestDTO dto) {
        Set<UUID> ids = new LinkedHashSet<>(dto.getAssentoSessaoIds());
        if (ids.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Informe ao menos um assento para reserva");
        }
        if (ids.size() != dto.getAssentoSessaoIds().size()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A lista de assentos contém IDs duplicados");
        }
        return ids;
    }

    private ReservaAssento criarItemReserva(Reserva reserva, AssentoSessao assentoSessao) {
        ReservaAssento item = new ReservaAssento();
        item.setReserva(reserva);
        item.setAssentoSessao(assentoSessao);
        item.setId(new ReservaAssentoId(reserva.getId(), assentoSessao.getId()));
        return item;
    }

    private ReservaResponseDTO paraDTO(Reserva reserva, List<UUID> assentoSessaoIds) {
        return new ReservaResponseDTO(
                reserva.getId(),
                reserva.getUsuario().getId(),
                reserva.getSessao().getId(),
                reserva.getStatus(),
                reserva.getCriadoEm(),
                assentoSessaoIds);
    }
}
