-- Reserva de um usuário para uma sessão (showtime).
CREATE TABLE reservations (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES usuarios (id),
    showtime_id UUID NOT NULL REFERENCES showtimes (id),
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT ck_reservations_status CHECK (
        status IN ('PENDING', 'CONFIRMED', 'CANCELLED', 'EXPIRED')
    )
);

CREATE INDEX idx_reservations_user_id ON reservations (user_id);
CREATE INDEX idx_reservations_showtime_id ON reservations (showtime_id);
CREATE INDEX idx_reservations_status ON reservations (status);

-- Itens da reserva: cada linha amarra a reserva a um assento da sessão (showtime_seats).
-- Uma reserva pode referenciar dezenas de showtime_seats; o par (reservation_id, showtime_seat_id) é único.
CREATE TABLE reservation_seats (
    reservation_id UUID NOT NULL REFERENCES reservations (id) ON DELETE CASCADE,
    showtime_seat_id UUID NOT NULL REFERENCES showtime_seats (id) ON DELETE CASCADE,
    PRIMARY KEY (reservation_id, showtime_seat_id)
);

CREATE INDEX idx_reservation_seats_showtime_seat_id ON reservation_seats (showtime_seat_id);
