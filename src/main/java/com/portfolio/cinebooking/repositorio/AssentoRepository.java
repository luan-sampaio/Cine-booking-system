package com.portfolio.cinebooking.repositorio;

import com.portfolio.cinebooking.modelo.Assento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AssentoRepository extends JpaRepository<Assento, UUID> {

    long countBySala_Id(UUID salaId);

    List<Assento> findBySala_IdOrderByRowLabelAscSeatNumberAsc(UUID salaId);
}
