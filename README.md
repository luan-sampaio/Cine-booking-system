# 🎬 Movie Reservation API

![Java](https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=java&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.5-brightgreen?style=for-the-badge&logo=spring&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue?style=for-the-badge&logo=postgresql&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-20.10-blue?style=for-the-badge&logo=docker&logoColor=white)

Uma API REST robusta para gerenciamento de cinemas, construída com as melhores práticas de desenvolvimento back-end usando Spring Boot. O sistema oferece uma base sólida para controle de filmes, salas, sessões e assentos, implementando regras de negócio críticas, como a prevenção de sessões sobrepostas na mesma sala. A segurança é garantida por um sistema de autenticação JWT com controle de acesso baseado em perfis (RBAC), assegurando que apenas usuários autorizados possam realizar operações sensíveis.

## ✨ Funcionalidades

### ⚙️ Lógica e Regras de Negócio
*   **Gestão de Entidades:** CRUD completo para Filmes, Salas e Sessões gerenciado por administradores.
*   **Controle de Sessões (Showtimes):** Sistema inteligente que impede a criação de sessões sobrepostas na mesma sala, validando horários de início e fim com base na duração do filme.
*   **Geração Automática de Assentos:** Ao criar uma nova sessão, todos os assentos correspondentes ao layout da sala são gerados e marcados como `DISPONÍVEL`.
*   **Consulta de Disponibilidade:** Endpoints para que os clientes possam visualizar em tempo real quais assentos estão livres para uma determinada sessão.

### 🔐 Segurança e Autenticação
*   **Autenticação via JWT:** Geração de token para usuários autenticados, desacoplando o estado da sessão do servidor.
*   **Controle de Acesso (RBAC):** Dois níveis de permissão:
    *   `ADMIN`: Acesso total para gerenciar filmes, salas, sessões e usuários.
    *   `CLIENTE`: Acesso para consultar filmes, sessões e assentos disponíveis.
*   **Endpoints Protegidos:** Rotas críticas são protegidas com base no perfil do usuário, garantindo que apenas pessoal autorizado possa realizar operações sensíveis.

---

## 📂 Estrutura do Projeto

```text
cine-booking-system/
├── src/
│   ├── main/
│   │   ├── java/com/portfolio/cinebooking/
│   │   │   ├── CineBookingApplication.java # Ponto de entrada da aplicação
│   │   │   ├── controller/                 # Endpoints da API (REST Controllers)
│   │   │   ├── dto/                        # Data Transfer Objects
│   │   │   ├── modelo/                     # Entidades JPA (Modelos de dados)
│   │   │   ├── repositorio/                # Interfaces de acesso ao banco (JPA Repositories)
│   │   │   ├── seguranca/                  # Configuração de segurança e JWT
│   │   │   └── servico/                    # Lógica de negócio
│   │   └── resources/
│   │       ├── application.yml             # Configurações do Spring
│   │       └── db/migration/               # Scripts de migração (Flyway)
│   └── test/
├── .gitignore
├── docker-compose.yml                  # Orquestração do container PostgreSQL
├── mvnw                                # Maven Wrapper (Linux/Mac)
├── mvnw.cmd                            # Maven Wrapper (Windows)
└── pom.xml                             # Dependências e build do projeto
```

---

## 🛠️ Tecnologias Utilizadas
*   **Linguagem:** Java 17
*   **Framework:** Spring Boot 3.2.5
*   **Banco de Dados:** PostgreSQL 15
*   **Containerização:** Docker Compose
*   **Persistência de Dados:** Spring Data JPA
*   **Migrações de Schema:** Flyway
*   **Segurança:** Spring Security com autenticação JWT 
*   **Documentação:** Springdoc OpenAPI 
*   **Build e Dependências:** Maven
*   **Utilitários:** Lombok

---

## 💻 Pré-requisitos

Antes de começar, você vai precisar ter instalado em sua máquina:
*   [Java (JDK) 17](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
*   [Docker](https://www.docker.com/get-started)
*   [Git](https://git-scm.com) 

## 🚀 Como executar o projeto

1.  **Clone o repositório:**
    ```bash
    git clone https://github.com/luan-sampaio/movie-reservation-api.git
    cd movie-reservation-api
    ```

2.  **Inicie o banco de dados com Docker:**
    ```bash
    docker-compose up -d
    ```
    Este comando irá iniciar um container PostgreSQL com as configurações definidas no `docker-compose.yml`.

3.  **Execute a aplicação Spring Boot:**
    ```bash
    ./mvnw spring-boot:run
    ```
    A API estará disponível em `http://localhost:8080`.

4.  **Acesse a documentação da API:**
    Para explorar e testar os endpoints, acesse o Swagger UI no seu navegador:
    [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

## 👨‍💻 Autor

<table>
  <tr>
    <td align="center">
      <a href="https://github.com/luan-sampaio">
        <img src="https://avatars.githubusercontent.com/luan-sampaio" width="100px;" alt="Foto de Luan Sampaio no GitHub"/>
        <br>
        <sub>
          <b>Luan Sampaio</b>
        </sub>
      </a>
    </td>
  </tr>
</table>
