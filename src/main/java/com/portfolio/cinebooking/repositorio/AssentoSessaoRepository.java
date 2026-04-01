package com.portfolio.cinebooking.repositorio;

import com.portfolio.cinebooking.modelo.AssentoSessao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AssentoSessaoRepository extends JpaRepository<AssentoSessao, UUID> {

    long countBySessao_Id(UUID sessaoId);
}
