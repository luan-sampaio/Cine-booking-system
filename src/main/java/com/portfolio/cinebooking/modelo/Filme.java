package com.portfolio.cinebooking.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "movies")
@Getter
@Setter
public class Filme {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "title", nullable = false, length = 255)
    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String synopsis;

    @Column(name = "duration_minutes", nullable = false)
    private Integer duracaoMinutos;

    @Column(name = "age_rating", length = 20)
    private String classificacaoEtaria;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime criadoEm = LocalDateTime.now();
}
