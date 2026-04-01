package com.portfolio.cinebooking.repositorio;

import com.portfolio.cinebooking.modelo.Filme;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface FilmeRepository extends JpaRepository<Filme, UUID> {

    List<Filme> findAllByOrderByTituloAsc();
}
