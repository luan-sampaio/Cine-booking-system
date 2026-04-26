-- Preço por ingresso definido na sessão (cálculo de reserva só no backend).
ALTER TABLE showtimes
    ADD COLUMN price_cents_per_seat INTEGER NOT NULL DEFAULT 2500;

ALTER TABLE showtimes
    ADD CONSTRAINT ck_showtimes_price_cents_positive CHECK (price_cents_per_seat > 0);

-- Total pago na reserva (N × preço da sessão no momento da conclusão).
ALTER TABLE reservations
    ADD COLUMN total_price_cents BIGINT NOT NULL DEFAULT 0;

ALTER TABLE reservations
    ADD CONSTRAINT ck_reservations_total_price_nonneg CHECK (total_price_cents >= 0);
