-- Sessão de exibição: filme em uma sala com horário (término pode ser calculado na aplicação a partir da duração do filme).
CREATE TABLE showtimes (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    movie_id UUID NOT NULL REFERENCES movies (id),
    room_id UUID NOT NULL REFERENCES rooms (id),
    starts_at TIMESTAMP NOT NULL,
    ends_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT ck_showtimes_ends_after_start CHECK (ends_at > starts_at)
);

CREATE INDEX idx_showtimes_room_starts ON showtimes (room_id, starts_at);
CREATE INDEX idx_showtimes_movie_id ON showtimes (movie_id);
CREATE INDEX idx_showtimes_starts_at ON showtimes (starts_at);

-- Estado de cada assento na sessão (uma linha por par sessão + assento após popularização na aplicação).
CREATE TABLE showtime_seats (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    showtime_id UUID NOT NULL REFERENCES showtimes (id) ON DELETE CASCADE,
    seat_id UUID NOT NULL REFERENCES seats (id) ON DELETE CASCADE,
    status VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE',
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT uq_showtime_seats_showtime_seat UNIQUE (showtime_id, seat_id),
    CONSTRAINT ck_showtime_seats_status CHECK (
        status IN ('AVAILABLE', 'HELD', 'BOOKED')
    )
);

CREATE INDEX idx_showtime_seats_showtime_id ON showtime_seats (showtime_id);
CREATE INDEX idx_showtime_seats_showtime_status ON showtime_seats (showtime_id, status);
