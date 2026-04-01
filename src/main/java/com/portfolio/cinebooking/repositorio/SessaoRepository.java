package com.portfolio.cinebooking.repositorio;

import com.portfolio.cinebooking.modelo.Sessao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SessaoRepository extends JpaRepository<Sessao, UUID> {

    @Query("""
            select count(s) from Sessao s
            where s.sala.id = :salaId
            and (:excluirId is null or s.id <> :excluirId)
            and s.inicio < :fim and s.fim > :inicio
            """)
    long countSobreposicaoNaSala(
            @Param("salaId") UUID salaId,
            @Param("excluirId") UUID excluirId,
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim);

    @Query("select distinct s from Sessao s join fetch s.filme join fetch s.sala order by s.inicio asc")
    List<Sessao> findAllComFilmeESalaOrderByInicioAsc();

    @Query("select s from Sessao s join fetch s.filme join fetch s.sala where s.id = :id")
    Optional<Sessao> findByIdComDetalhes(@Param("id") UUID id);
}
