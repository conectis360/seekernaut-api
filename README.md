# SeekerNaut API 🦜 - Interface para Ollama

Uma API backend robusta e escalável construída com Spring Boot, projetada para servir como uma interface inteligente e gerenciável para interagir com modelos de linguagem hospedados localmente via Ollama.

---

## ✨ Funcionalidades

### Essenciais
* **Conversação Contextual:** Envie prompts para modelos Ollama e receba respostas, mantendo o histórico da conversa para interações mais ricas (`POST /api/chat`).
* **Gerenciamento de Conversas:** Inicie novas conversas e obtenha um ID único para rastreamento (`POST /api/conversations`).
* **Histórico de Mensagens:** Recupere o histórico completo de mensagens de uma conversa específica (`GET /api/conversations/{conversationId}/messages`).
* **Logging Detalhado:** Registre eventos importantes da aplicação e interações com o Ollama no MongoDB para auditoria e depuração.

### Extensões Potenciais (Roadmap)
* **Seleção de Modelos:** Liste modelos disponíveis no Ollama e permita a seleção por conversa (`GET /api/models`).
* **Autenticação Segura:** Implemente OAuth 2.0 para login com provedores externos (Google, GitHub, etc.).
* **Conversas por Usuário:** Permita que usuários autenticados visualizem suas próprias conversas salvas (`GET /api/users/me/conversations`).
* **Escalabilidade com Filas:** Utilize Kafka ou RabbitMQ para processamento assíncrono de logs ou comunicação com Ollama.
* **Monitoramento:** Exponha métricas da aplicação via Spring Boot Actuator (`/actuator`).
* **Otimização de Cache:** Implemente cache para respostas do Ollama (considerando o impacto no contexto).
* **Comunicação Real-time:** Utilize WebSockets para uma experiência de chat mais fluida.

---

## 🛠️ Tecnologias Utilizadas

* **Linguagem:** Java 17+
* **Framework Principal:** Spring Boot 3.x.x *(Verifique sua versão exata, o original diz 3.4.5, que parece futura. Use a versão real)*
* **Web:** Spring Web (RESTful), Spring WebFlux (Reativo para Ollama)
* **Persistência:**
    * Spring Data JPA & Jakarta Persistence API (ORM)
    * PostgreSQL (Banco de Dados Relacional: Usuários, Conversas, Mensagens)
    * Spring Data MongoDB (Banco de Dados NoSQL: Logs)
* **Segurança:** Spring Security, JJWT (JSON Web Token), Spring Boot Starter OAuth2 Client (Opcional)
* **Gerenciamento de Sessão:** Spring Session Core (Opcional)
* **Utilitários:** Lombok
* **Build:** Maven

---

## 🚀 Como Começar

Siga estes passos para configurar e executar a aplicação localmente.

### Pré-requisitos

* ✅ JDK 17 ou superior instalado e configurado.
* ✅ Maven instalado e configurado.
* ✅ Instância do PostgreSQL em execução e acessível.
* ✅ Instância do MongoDB em execução e acessível.
* ✅ Ollama instalado e em execução (com pelo menos um modelo baixado, ex: `ollama pull llama3`).

### Instalação e Execução

1.  **Clone o Repositório:**
    ```bash
    git clone <URL_DO_SEU_REPOSITORIO>
    cd seekernaut-api
    ```
    *(Substitua `<URL_DO_SEU_REPOSITORIO>` pela URL real do seu projeto no GitHub)*

2.  **Configure as Variáveis de Ambiente/Aplicação:**
    * Edite os arquivos `application.yml` (padrão), `application-hml.yml` (desenvolvimento) e `application-prd.yml` (produção) conforme necessário.
    * **Certifique-se de configurar corretamente:**
        * Conexões com PostgreSQL (`spring.datasource.*`)
        * Conexões com MongoDB (`spring.data.mongodb.*`)
        * Segredo JWT (`seekernaut.app.jwtSecret`) - **MUITO IMPORTANTE MANTER SEGURO!**
        * URL base do Ollama (se não for o padrão `http://localhost:11434`)

3.  **Compile o Projeto:**
    ```bash
    mvn clean install
    ```

4.  **Execute a Aplicação:**
    * **Usando o perfil padrão (`application.yml`):**
        ```bash
        mvn spring-boot:run
        ```
    * **Especificando um perfil (ex: homologação):**
        ```bash
        mvn spring-boot:run -Dspring-boot.run.profiles=hml
        ```
    * **Especificando o perfil de produção:**
        ```bash
        mvn spring-boot:run -Dspring-boot.run.profiles=prd
        ```

    A API estará disponível na porta configurada (padrão: `9001`). Verifique a propriedade `server.port` no seu `application.yml`.

---

## ⚙️ Configuração

A aplicação utiliza arquivos `application.yml` para gerenciamento de configuração, suportando perfis do Spring Boot para diferentes ambientes (dev, hml, prd).

* **`application.yml`:** Contém configurações padrão e comuns a todos os ambientes.
* **`application-<profile>.yml`:** (ex: `application-hml.yml`, `application-prd.yml`) Sobrescreve ou adiciona configurações específicas para um ambiente.

**Ativação de Perfil:**
Use a propriedade `spring.profiles.active` ao iniciar a aplicação:
* Argumento JVM: `-Dspring.profiles.active=prd`
* Variável de Ambiente: `SPRING_PROFILES_ACTIVE=prd`

**Principais Configurações:**
* `server.port`: Porta em que a aplicação será executada.
* `spring.application.name`: Nome da aplicação.
* `spring.datasource.*`: Configurações de conexão com o PostgreSQL.
* `spring.data.mongodb.*`: Configurações de conexão com o MongoDB.
* `seekernaut.app.jwtSecret`: Chave secreta para assinar os tokens JWT. **Mantenha esta chave segura e considere usar variáveis de ambiente em produção.**
* `seekernaut.app.jwtExpirationMs`: Tempo de expiração para os tokens JWT.
* `logging.level.*`: Níveis de log para diferentes pacotes.

---

## 💾 Banco de Dados

* **PostgreSQL:** Utilizado para armazenar dados estruturados como usuários (se a autenticação for implementada), conversas e mensagens.
    * O esquema do banco de dados (DDL) pode ser encontrado em: `[adicione o caminho para o arquivo SQL aqui, ex: src/main/resources/db/migration/V1__init.sql]`
    * O Spring Data JPA pode gerenciar o schema automaticamente com base nas entidades (`spring.jpa.hibernate.ddl-auto`), mas para produção é recomendado usar ferramentas de migração como Flyway ou Liquibase.
* **MongoDB:** Utilizado para armazenar logs da aplicação de forma flexível e escalável.

---

## 🔐 Segurança

* **Autenticação via JWT:** A API protege endpoints usando JSON Web Tokens.
* **Fluxo:** Após a autenticação (se implementada) ou ao iniciar certas operações, um token JWT é gerado.
* **Uso:** Inclua o token JWT no cabeçalho `Authorization` de cada requisição para endpoints protegidos:
    ```
    Authorization: Bearer <seu_token_jwt>
    ```
* **Chave Secreta:** A segurança do JWT depende da confidencialidade da chave `seekernaut.app.jwtSecret` configurada. **Não a exponha publicamente!**

---

## 🔌 API Endpoints Principais

* `POST /api/chat`: Envia uma mensagem do usuário para o modelo Ollama no contexto de uma conversa existente.
* `POST /api/conversations`: Inicia uma nova conversa.
* `GET /api/conversations/{conversationId}/messages`: Retorna o histórico de mensagens de uma conversa.

*(Considere adicionar um link para uma documentação mais detalhada da API, como Swagger UI, se disponível)*

---

## 🧩 Tratamento de Exceções

A aplicação implementa um `CommonsExceptionHandler` global para capturar exceções e retornar respostas de erro padronizadas em formato JSON. Isso inclui:
* Erros de validação (`MethodArgumentNotValidException`)
* Erros de acesso a dados (`DataAccessException`)
* Exceções de negócio personalizadas
* Suporte à internacionalização (i18n) para mensagens de erro.

---

## 🤝 Contribuição

Contribuições são bem-vindas! Se você deseja contribuir, por favor:
1.  Faça um Fork do repositório.
2.  Crie uma Branch para sua feature (`git checkout -b feature/MinhaNovaFeature`).
3.  Faça o Commit de suas mudanças (`git commit -m 'Adiciona MinhaNovaFeature'`).
4.  Faça o Push para a Branch (`git push origin feature/MinhaNovaFeature`).
5.  Abra um Pull Request.

*(Opcional: Adicione mais detalhes sobre guias de estilo de código, processo de revisão, etc.)*

---

## 📄 Licença

Este projeto está licenciado sob a Licença [Nome da Sua Licença - Ex: MIT]. Veja o arquivo `LICENSE` para mais detalhes.

*(Certifique-se de adicionar um arquivo `LICENSE` ao seu repositório e substituir `[Nome da Sua Licença - Ex: MIT]` pelo nome correto)*

---