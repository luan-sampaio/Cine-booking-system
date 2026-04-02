package com.portfolio.cinebooking.servico;

import com.portfolio.cinebooking.dto.SessaoHorarioRequestDTO;
import com.portfolio.cinebooking.dto.SessaoRequestDTO;
import com.portfolio.cinebooking.dto.SessaoResponseDTO;
import com.portfolio.cinebooking.modelo.Assento;
import com.portfolio.cinebooking.modelo.AssentoSessao;
import com.portfolio.cinebooking.modelo.Filme;
import com.portfolio.cinebooking.modelo.Sala;
import com.portfolio.cinebooking.modelo.Sessao;
import com.portfolio.cinebooking.modelo.StatusAssentoSessao;
import com.portfolio.cinebooking.repositorio.AssentoRepository;
import com.portfolio.cinebooking.repositorio.AssentoSessaoRepository;
import com.portfolio.cinebooking.repositorio.FilmeRepository;
import com.portfolio.cinebooking.repositorio.SalaRepository;
import com.portfolio.cinebooking.repositorio.SessaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminSessaoServico {

    private final SessaoRepository sessaoRepository;
    private final FilmeRepository filmeRepository;
    private final SalaRepository salaRepository;
    private final AssentoRepository assentoRepository;
    private final AssentoSessaoRepository assentoSessaoRepository;

    @Transactional(readOnly = true)
    public List<SessaoResponseDTO> listar() {
        return sessaoRepository.findAllComFilmeESalaOrderByInicioAsc().stream()
                .map(this::paraDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public SessaoResponseDTO buscar(UUID id) {
        return sessaoRepository.findByIdComDetalhes(id)
                .map(this::paraDTO)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sessão não encontrada"));
    }

    @Transactional
    public SessaoResponseDTO criar(SessaoRequestDTO dto) {
        validarIntervalo(dto.getInicio(), dto.getFim());
        Filme filme = filmeRepository.findById(dto.getFilmeId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Filme não encontrado"));
        Sala sala = salaRepository.findById(dto.getSalaId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sala não encontrada"));

        garantirSemSobreposicao(dto.getSalaId(), dto.getInicio(), dto.getFim(), null);

        List<Assento> assentosDaSala = assentoRepository.findBySala_IdOrderByRowLabelAscSeatNumberAsc(sala.getId());
        if (assentosDaSala.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A sala não possui assentos configurados. Use PUT /api/admin/salas/{id}/assentos/layout antes de criar a sessão.");
        }

        Sessao sessao = new Sessao();
        sessao.setFilme(filme);
        sessao.setSala(sala);
        sessao.setInicio(dto.getInicio());
        sessao.setFim(dto.getFim());
        sessao = sessaoRepository.save(sessao);

        gerarAssentosSessaoPreemptivamente(sessao, assentosDaSala);

        return paraDTO(sessao);
    }

    @Transactional
    public SessaoResponseDTO atualizar(UUID id, SessaoHorarioRequestDTO dto) {
        validarIntervalo(dto.getInicio(), dto.getFim());

        Sessao sessao = sessaoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sessão não encontrada"));

        
        garantirSemSobreposicao(sessao.getSala().getId(), dto.getInicio(), dto.getFim(), id);

        sessao.setInicio(dto.getInicio());
        sessao.setFim(dto.getFim());
        sessaoRepository.save(sessao);

        return sessaoRepository.findByIdComDetalhes(id)
                .map(this::paraDTO)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sessão não encontrada"));
    }

    @Transactional
    public void remover(UUID id) {
        if (!sessaoRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Sessão não encontrada");
        }
        sessaoRepository.deleteById(id);
    }

    private void validarIntervalo(java.time.LocalDateTime inicio, java.time.LocalDateTime fim) {
        if (!fim.isAfter(inicio)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O horário de fim deve ser posterior ao de início");
        }
    }

    private void garantirSemSobreposicao(UUID salaId, java.time.LocalDateTime inicio, java.time.LocalDateTime fim, UUID excluirSessaoId) {
        if (sessaoRepository.countSobreposicaoNaSala(salaId, excluirSessaoId, inicio, fim) > 0) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Já existe sessão nesta sala que se sobrepõe a este intervalo de horário");
        }
    }

    private void gerarAssentosSessaoPreemptivamente(Sessao sessao, List<Assento> assentosDaSala) {
        List<AssentoSessao> linhas = new ArrayList<>(assentosDaSala.size());
        for (Assento assento : assentosDaSala) {
            AssentoSessao linha = new AssentoSessao();
            linha.setSessao(sessao);
            linha.setAssento(assento);
            linha.setStatus(StatusAssentoSessao.AVAILABLE);
            linhas.add(linha);
        }
        assentoSessaoRepository.saveAll(linhas);
    }

    private SessaoResponseDTO paraDTO(Sessao s) {
        long total = assentoSessaoRepository.countBySessao_Id(s.getId());
        return new SessaoResponseDTO(
                s.getId(),
                s.getFilme().getId(),
                s.getFilme().getTitulo(),
                s.getSala().getId(),
                s.getSala().getNome(),
                s.getInicio(),
                s.getFim(),
                s.getCriadoEm(),
                total);
    }
}
