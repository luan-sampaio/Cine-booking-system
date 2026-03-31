package com.portfolio.cinebooking.configuracao;

import com.portfolio.cinebooking.modelo.Perfil;
import com.portfolio.cinebooking.modelo.Usuario;
import com.portfolio.cinebooking.repositorio.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class SeedAdminConfig {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner seedAdminUsuario(
            @Value("${seed.admin.enabled:true}") boolean enabled,
            @Value("${seed.admin.nome:Admin}") String nome,
            @Value("${seed.admin.email:admin@cinebooking.com}") String email,
            @Value("${seed.admin.senha:Admin@123}") String senha
    ) {
        return args -> {
            if (!enabled) {
                return;
            }

            if (usuarioRepository.findByEmail(email).isPresent()) {
                log.info("Seed admin ignorado: usuario {} ja existe.", email);
                return;
            }

            Usuario admin = new Usuario();
            admin.setNome(nome);
            admin.setEmail(email);
            admin.setSenha(passwordEncoder.encode(senha));
            admin.setPerfil(Perfil.ADMIN);

            usuarioRepository.save(admin);
            log.info("Usuario admin criado com sucesso: {}", email);
        };
    }
}
