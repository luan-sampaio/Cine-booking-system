-- Admin padrão (desenvolvimento: admin@cinebooking.com / Admin@123
-- Hash BCrypt (cost 10), compatível com BCryptPasswordEncoder do Spring Security.
INSERT INTO usuarios (id, nome, email, senha, perfil, criado_em)
VALUES (
    gen_random_uuid(),
    'Admin',
    'admin@cinebooking.com',
    '$2a$10$TuWabi6DfGaKrSQwJGca1OBaM7F5LXpi49OIs.EpImuJLOU64Aps2',
    'ADMIN',
    CURRENT_TIMESTAMP
)
ON CONFLICT (email) DO NOTHING;
