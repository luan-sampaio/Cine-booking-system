package com.portfolio.cinebooking.repositorio;

import com.portfolio.cinebooking.modelo.Sala;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SalaRepository extends JpaRepository<Sala, UUID> {

    boolean existsByNomeIgnoreCase(String nome);

    List<Sala> findAllByOrderByNomeAsc();
}
