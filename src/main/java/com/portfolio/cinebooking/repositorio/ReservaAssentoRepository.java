package com.portfolio.cinebooking.repositorio;

import com.portfolio.cinebooking.modelo.ReservaAssento;
import com.portfolio.cinebooking.modelo.ReservaAssentoId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ReservaAssentoRepository extends JpaRepository<ReservaAssento, ReservaAssentoId> {

    List<ReservaAssento> findByReserva_Id(UUID reservaId);
}
