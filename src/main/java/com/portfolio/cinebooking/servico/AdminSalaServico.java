package com.portfolio.cinebooking.servico;

import com.portfolio.cinebooking.dto.AssentoItemResponseDTO;
import com.portfolio.cinebooking.dto.AssentosLayoutRequestDTO;
import com.portfolio.cinebooking.dto.FilaAssentosDTO;
import com.portfolio.cinebooking.dto.SalaDetalheResponseDTO;
import com.portfolio.cinebooking.dto.SalaRequestDTO;
import com.portfolio.cinebooking.dto.SalaResumoResponseDTO;
import com.portfolio.cinebooking.modelo.Assento;
import com.portfolio.cinebooking.modelo.Sala;
import com.portfolio.cinebooking.repositorio.AssentoRepository;
import com.portfolio.cinebooking.repositorio.SalaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminSalaServico {

    private final SalaRepository salaRepository;
    private final AssentoRepository assentoRepository;

    @Transactional
    public SalaResumoResponseDTO criar(SalaRequestDTO dto) {
        String nome = dto.getNome().trim();
        if (nome.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nome da sala inválido");
        }
        if (salaRepository.existsByNomeIgnoreCase(nome)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe sala com este nome");
        }
        Sala sala = new Sala();
        sala.setNome(nome);
        sala = salaRepository.save(sala);
        return new SalaResumoResponseDTO(sala.getId(), sala.getNome(), 0L);
    }

    @Transactional(readOnly = true)
    public List<SalaResumoResponseDTO> listar() {
        return salaRepository.findAllByOrderByNomeAsc().stream()
                .map(s -> new SalaResumoResponseDTO(
                        s.getId(),
                        s.getNome(),
                        assentoRepository.countBySala_Id(s.getId())))
                .toList();
    }

    @Transactional(readOnly = true)
    public SalaDetalheResponseDTO buscar(UUID id) {
        Sala sala = salaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sala não encontrada"));
        List<AssentoItemResponseDTO> itens = assentoRepository.findBySala_IdOrderByRowLabelAscSeatNumberAsc(id).stream()
                .map(a -> new AssentoItemResponseDTO(a.getId(), a.getRowLabel(), a.getSeatNumber()))
                .toList();
        return new SalaDetalheResponseDTO(sala.getId(), sala.getNome(), itens);
    }

    @Transactional
    public SalaDetalheResponseDTO definirLayoutAssentos(UUID salaId, AssentosLayoutRequestDTO dto) {
        Sala sala = salaRepository.findById(salaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sala não encontrada"));

        validarSemDuplicatasNoPayload(dto);

        sala.getAssentos().clear();
        for (FilaAssentosDTO fila : dto.getRows()) {
            String label = fila.getLabel().trim();
            if (label.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Rótulo de fila inválido");
            }
            for (Integer num : fila.getSeatNumbers()) {
                Assento assento = new Assento();
                assento.setSala(sala);
                assento.setRowLabel(label);
                assento.setSeatNumber(num);
                sala.getAssentos().add(assento);
            }
        }
        salaRepository.save(sala);
        return buscar(salaId);
    }

    private static void validarSemDuplicatasNoPayload(AssentosLayoutRequestDTO dto) {
        Set<String> pares = new HashSet<>();
        for (FilaAssentosDTO fila : dto.getRows()) {
            String label = fila.getLabel().trim();
            for (Integer num : fila.getSeatNumbers()) {
                String chave = label + "\0" + num;
                if (!pares.add(chave)) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Assento duplicado no layout: fila " + label + ", número " + num);
                }
            }
        }
    }
}
