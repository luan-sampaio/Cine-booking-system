CREATE TABLE movies (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title VARCHAR(255) NOT NULL,
    synopsis TEXT,
    duration_minutes INTEGER NOT NULL,
    age_rating VARCHAR(20),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT ck_movies_duration_positive CHECK (duration_minutes > 0)
);

CREATE TABLE rooms (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL,
    CONSTRAINT uq_rooms_name UNIQUE (name)
);

CREATE TABLE seats (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    room_id UUID NOT NULL REFERENCES rooms (id) ON DELETE CASCADE,
    row_label VARCHAR(10) NOT NULL,
    seat_number INTEGER NOT NULL,
    CONSTRAINT ck_seats_seat_number_positive CHECK (seat_number > 0),
    CONSTRAINT uq_seats_room_row_seat UNIQUE (room_id, row_label, seat_number)
);

CREATE INDEX idx_seats_room_id ON seats (room_id);
