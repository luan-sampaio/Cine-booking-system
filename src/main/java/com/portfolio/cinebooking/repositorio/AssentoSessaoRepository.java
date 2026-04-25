package com.portfolio.cinebooking.repositorio;

import com.portfolio.cinebooking.modelo.AssentoSessao;
import com.portfolio.cinebooking.modelo.StatusAssentoSessao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface AssentoSessaoRepository extends JpaRepository<AssentoSessao, UUID> {

    long countBySessao_Id(UUID sessaoId);

    @Query("""
            select a from AssentoSessao a join fetch a.assento
            where a.sessao.id = :sessaoId and a.status = :status
            order by a.assento.rowLabel asc, a.assento.seatNumber asc
            """)
    List<AssentoSessao> findBySessaoIdAndStatusFetchAssento(
            @Param("sessaoId") UUID sessaoId,
            @Param("status") StatusAssentoSessao status);

    @Query("""
            select a from AssentoSessao a
            where a.sessao.id = :sessaoId and a.id in :ids
            """)
    List<AssentoSessao> findBySessaoIdAndIdIn(
            @Param("sessaoId") UUID sessaoId,
            @Param("ids") Set<UUID> ids);
}
