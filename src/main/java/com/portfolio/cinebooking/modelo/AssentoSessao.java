package com.portfolio.cinebooking.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(
        name = "showtime_seats",
        uniqueConstraints = @UniqueConstraint(columnNames = {"showtime_id", "seat_id"})
)
@Getter
@Setter
public class AssentoSessao {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "showtime_id", nullable = false)
    private Sessao sessao;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id", nullable = false)
    private Assento assento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusAssentoSessao status = StatusAssentoSessao.AVAILABLE;

    @Version
    @Column(nullable = false)
    private Long version = 0L;
}
