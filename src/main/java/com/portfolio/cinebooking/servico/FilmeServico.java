package com.portfolio.cinebooking.servico;

import com.portfolio.cinebooking.dto.FilmeRequestDTO;
import com.portfolio.cinebooking.dto.FilmeResponseDTO;
import com.portfolio.cinebooking.modelo.Filme;
import com.portfolio.cinebooking.repositorio.FilmeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FilmeServico {

    private final FilmeRepository filmeRepository;

    @Transactional(readOnly = true)
    public List<FilmeResponseDTO> listarParaExibicao() {
        return filmeRepository.findAllByOrderByTituloAsc().stream()
                .map(this::paraDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public FilmeResponseDTO buscar(UUID id) {
        return filmeRepository.findById(id)
                .map(this::paraDTO)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Filme não encontrado"));
    }

    @Transactional
    public FilmeResponseDTO criar(FilmeRequestDTO dto) {
        Filme filme = new Filme();
        aplicar(filme, dto);
        return paraDTO(filmeRepository.save(filme));
    }

    @Transactional
    public FilmeResponseDTO atualizar(UUID id, FilmeRequestDTO dto) {
        Filme filme = filmeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Filme não encontrado"));
        aplicar(filme, dto);
        return paraDTO(filmeRepository.save(filme));
    }

    @Transactional
    public void remover(UUID id) {
        if (!filmeRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Filme não encontrado");
        }
        filmeRepository.deleteById(id);
    }

    private void aplicar(Filme filme, FilmeRequestDTO dto) {
        filme.setTitulo(dto.getTitulo().trim());
        filme.setSynopsis(dto.getSynopsis() != null && !dto.getSynopsis().isBlank() ? dto.getSynopsis().trim() : null);
        filme.setDuracaoMinutos(dto.getDuracaoMinutos());
        filme.setClassificacaoEtaria(
                dto.getClassificacaoEtaria() != null && !dto.getClassificacaoEtaria().isBlank()
                        ? dto.getClassificacaoEtaria().trim()
                        : null);
    }

    private FilmeResponseDTO paraDTO(Filme f) {
        return new FilmeResponseDTO(
                f.getId(),
                f.getTitulo(),
                f.getSynopsis(),
                f.getDuracaoMinutos(),
                f.getClassificacaoEtaria(),
                f.getCriadoEm());
    }
}
