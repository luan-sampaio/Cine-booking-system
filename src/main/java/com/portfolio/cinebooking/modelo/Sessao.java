package com.portfolio.cinebooking.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "showtimes")
@Getter
@Setter
public class Sessao {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", nullable = false)
    private Filme filme;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Sala sala;

    @Column(name = "starts_at", nullable = false)
    private LocalDateTime inicio;

    @Column(name = "ends_at", nullable = false)
    private LocalDateTime fim;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime criadoEm = LocalDateTime.now();
}
