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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "reservations")
@Getter
@Setter
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "showtime_id", nullable = false)
    private Sessao sessao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private StatusReserva status = StatusReserva.PENDING;

    @OneToMany(mappedBy = "reserva")
    private List<ReservaAssento> assentos = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime criadoEm = LocalDateTime.now();
}
