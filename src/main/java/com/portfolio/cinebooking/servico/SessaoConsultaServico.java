package com.portfolio.cinebooking.servico;

import com.portfolio.cinebooking.dto.AssentoDisponivelResponseDTO;
import com.portfolio.cinebooking.modelo.AssentoSessao;
import com.portfolio.cinebooking.modelo.StatusAssentoSessao;
import com.portfolio.cinebooking.repositorio.AssentoSessaoRepository;
import com.portfolio.cinebooking.repositorio.SessaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SessaoConsultaServico {

    private final SessaoRepository sessaoRepository;
    private final AssentoSessaoRepository assentoSessaoRepository;

    @Transactional(readOnly = true)
    public List<AssentoDisponivelResponseDTO> listarAssentosDisponiveis(UUID sessaoId) {
        if (!sessaoRepository.existsById(sessaoId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Sessão não encontrada");
        }
        return assentoSessaoRepository
                .findBySessaoIdAndStatusFetchAssento(sessaoId, StatusAssentoSessao.AVAILABLE)
                .stream()
                .map(this::paraDTO)
                .toList();
    }

    private AssentoDisponivelResponseDTO paraDTO(AssentoSessao linha) {
        return new AssentoDisponivelResponseDTO(
                linha.getId(),
                linha.getAssento().getRowLabel(),
                linha.getAssento().getSeatNumber());
    }
}
