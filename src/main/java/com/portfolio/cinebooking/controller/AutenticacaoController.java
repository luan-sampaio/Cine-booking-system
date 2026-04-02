package com.portfolio.cinebooking.controller;

import com.portfolio.cinebooking.dto.LoginRequestDTO;
import com.portfolio.cinebooking.dto.LoginResponseDTO;
import com.portfolio.cinebooking.dto.RegistroRequestDTO;
import com.portfolio.cinebooking.modelo.Perfil;
import com.portfolio.cinebooking.modelo.Usuario;
import com.portfolio.cinebooking.repositorio.UsuarioRepository;
import com.portfolio.cinebooking.seguranca.TokenServico;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticação", description = "Cadastro e login (JWT)")
public class AutenticacaoController {

    private final AuthenticationManager authManager;
    private final UsuarioRepository usuarioRepository;
    private final TokenServico tokenServico;
    private final PasswordEncoder passwordEncoder;

    @Operation(summary = "Login", description = "Autentica por e-mail e senha; retorna JWT.")
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid LoginRequestDTO dados) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(dados.getEmail(), dados.getSenha());
        var auth = authManager.authenticate(usernamePassword);

        var token = tokenServico.gerarToken((Usuario) auth.getPrincipal());

        return ResponseEntity.ok(new LoginResponseDTO(token));
    }

    @Operation(
            summary = "Signup (cadastro)",
            description = "Cria um novo usuário. Se o perfil não for especificado, o padrão é `CLIENTE`. Retorna **201** com JWT. E-mail duplicado: **409**."
    )
    @PostMapping("/signup")
    public ResponseEntity<LoginResponseDTO> signup(@RequestBody @Valid RegistroRequestDTO dados) {
        if (usuarioRepository.findByEmail(dados.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        var senhaEncriptada = passwordEncoder.encode(dados.getSenha());

        var novoUsuario = new Usuario();
        novoUsuario.setNome(dados.getNome());
        novoUsuario.setEmail(dados.getEmail());
        novoUsuario.setSenha(senhaEncriptada);
        novoUsuario.setPerfil(dados.getPerfil() != null ? dados.getPerfil() : Perfil.CLIENTE);

        var salvo = usuarioRepository.save(novoUsuario);
        var token = tokenServico.gerarToken(salvo);

        return ResponseEntity.status(HttpStatus.CREATED).body(new LoginResponseDTO(token));
    }
}
