package com.portfolio.cinebooking.modelo;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "reservation_seats")
@Getter
@Setter
public class ReservaAssento {

    @EmbeddedId
    private ReservaAssentoId id = new ReservaAssentoId();

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @MapsId("reservaId")
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reserva reserva;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @MapsId("assentoSessaoId")
    @JoinColumn(name = "showtime_seat_id", nullable = false)
    private AssentoSessao assentoSessao;
}
