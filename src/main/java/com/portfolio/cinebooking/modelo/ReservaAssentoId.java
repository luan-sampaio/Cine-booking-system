package com.portfolio.cinebooking.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ReservaAssentoId implements Serializable {

    @Column(name = "reservation_id", nullable = false)
    private UUID reservaId;

    @Column(name = "showtime_seat_id", nullable = false)
    private UUID assentoSessaoId;
}
