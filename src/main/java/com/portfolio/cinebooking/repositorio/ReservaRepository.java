package com.portfolio.cinebooking.repositorio;

import com.portfolio.cinebooking.modelo.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ReservaRepository extends JpaRepository<Reserva, UUID> {
}
