SeekerNaut API - Interface para Ollama

Este projeto é uma API backend construída com Spring Boot para fornecer uma interface para interagir com modelos de linguagem hospedados no Ollama.
Tecnologias Utilizadas

    Java: Linguagem de programação principal.
    Spring Boot (3.4.5): Framework Java para desenvolvimento rápido de aplicações.
    Spring Web: Para construção de APIs RESTful.
    Spring WebFlux: Para construção de APIs reativas e não bloqueantes (para interações com o Ollama).
    Spring Data JPA: Para interação com o banco de dados relacional PostgreSQL.
    Spring Data MongoDB: Para interação com o banco de dados NoSQL MongoDB (para logs).
    Spring Security: Para implementação de segurança e autenticação.
    Spring Boot Starter OAuth2 Client: Para suporte a autenticação via OAuth 2.0 (opcional).
    Spring Session Core: Para gerenciamento de sessões de usuários (opcional).
    PostgreSQL: Banco de dados relacional para armazenar dados estruturados (usuários, conversas, mensagens).
    MongoDB: Banco de dados NoSQL para armazenar logs da aplicação.
    Lombok: Biblioteca para reduzir o boilerplate de código Java.
    jjwt (JSON Web Token): Para geração e validação de tokens de autenticação.
    Jakarta Persistence API: Para mapeamento objeto-relacional (ORM).

Funcionalidades Essenciais da API

    /api/chat (POST): Endpoint para enviar mensagens do usuário para o Ollama e receber a resposta do modelo. Mantém o histórico da conversa para respostas contextuais.
    /api/conversations (POST): Endpoint para iniciar uma nova conversa, retornando um ID único para rastreamento.
    /api/conversations/{conversationId}/messages (GET): Endpoint para obter o histórico completo de mensagens de uma conversa específica.
    Logging: Registro detalhado de eventos da aplicação e interações com o Ollama no MongoDB.

Funcionalidades de Extensão (Potenciais)

    /api/models (GET): Listagem dos modelos disponíveis no Ollama e seleção de modelos por conversa.
    Autenticação OAuth 2.0: Implementação de autenticação de usuários via provedores externos (Google, GitHub, etc.).
    /api/users/me/conversations (GET): Listagem das conversas salvas de um usuário autenticado.
    Filas de Mensagens (Kafka/RabbitMQ): Desacoplamento do processamento de logs ou comunicação com o Ollama para escalabilidade.
    /actuator: Exposição de métricas da aplicação para monitoramento.
    Cache de Respostas do Ollama: Otimização para prompts repetidos (com cautela devido ao contexto).
    WebSockets: Comunicação em tempo real para a interação de chat.

Configuração da Aplicação

As configurações da aplicação podem ser encontradas nos arquivos application.yml e application-<profile>.yml (para diferentes ambientes).
application.yml (Configurações Padrão)

Contém as configurações padrão para a porta do servidor, nome da aplicação, conexão com o banco de dados PostgreSQL, conexão com o MongoDB para logs, segredo e tempo de expiração do JWT, e configurações de logging.
application-hml.yml (Profile de Homologação/Desenvolvimento)

Sobrescreve as configurações padrão para o ambiente de homologação/desenvolvimento, como URLs de banco de dados e níveis de log mais detalhados.
application-prd.yml (Profile de Produção)

Sobrescreve as configurações padrão para o ambiente de produção, como URLs de banco de dados, níveis de log mais restritos e estratégias de DDL do JPA.

Para ativar um profile específico, utilize a propriedade spring.profiles.active como variável de ambiente ou argumento de linha de comando ao executar a aplicação (ex: -Dspring.profiles.active=prd).
Banco de Dados

O esquema do banco de dados relacional PostgreSQL é definido para armazenar informações de usuários (opcional), conversas e mensagens. Os scripts SQL para criação das tabelas (users, conversations, messages) podem ser encontrados [aqui - adicione o caminho para o arquivo SQL quando criá-lo].
Segurança

A API utiliza JSON Web Tokens (JWT) para autenticação. Um token é gerado após a autenticação bem-sucedida do usuário e deve ser incluído nos headers de autorização (Authorization: Bearer <token>) para acessar endpoints protegidos.

A chave secreta para assinar e verificar os tokens JWT é configurada na propriedade seekernaut.app.jwtSecret. Mantenha esta chave em segurança.
Tratamento de Exceções

A aplicação possui um handler global de exceções (CommonsExceptionHandler) para fornecer respostas de erro consistentes e informativas para diferentes tipos de exceções, incluindo erros de validação, erros de banco de dados e exceções de negócio. As mensagens de erro podem ser internacionalizadas utilizando o MessageSource.
Como Executar a Aplicação

    Pré-requisitos:
        Java Development Kit (JDK) 17 ou superior instalado.
        Maven instalado.
        Uma instância do PostgreSQL em execução (configurada no application.yml).
        Uma instância do MongoDB em execução (configurada no application.yml).
        Ollama instalado e em execução (para testar a interação com os modelos).

    Clonar o Repositório:
    Bash

git clone <URL_DO_SEU_REPOSITORIO>
cd seekernaut-api

Construir a Aplicação:
Bash

mvn clean install

Executar a Aplicação:
Bash

mvn spring-boot:run

Você pode especificar um profile ao executar:
Bash

mvn spring-boot:run -Dspring-boot.run.profiles=hml

ou
Bash

    mvn spring-boot:run -Dspring-boot.run.profiles=prd

A API estará disponível na porta configurada (server.port no application.yml, padrão é 9001).
