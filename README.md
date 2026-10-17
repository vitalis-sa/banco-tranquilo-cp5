# DBExpress — Worker Banco Tranquilo (CP5)

Microsserviço Worker desenvolvido com **Spring Boot 4.1.1**, **Java 21** e **Spring for RabbitMQ (AMQP)** que simula a operadora financeira **Banco Tranquilo** para processamento assíncrono dos pagamentos solicitados pelo **Mercado Express**.

**IDE utilizada:** IntelliJ IDEA

---

## Links do Projeto

- **Link de Produção (Render):** `COLOQUE_AQUI_O_LINK_DO_RENDER_DBEXPRESS`
- **Repositório Principal (`mercadoExpress`):** `COLOQUE_AQUI_O_LINK_DO_GITHUB_MERCADOEXPRESS`
- **Vídeo de Demonstração (YouTube):** `COLOQUE_AQUI_O_LINK_DO_VIDEO`

---

## Integrantes

| Nome | RM |
|---|---|
| Ana Flavia Camelo | 561489 |
| Gustavo Kenji Terada | 562745 |
| João Guilherme Carvalho Novaes | 566234 |
| Pedro Chasci Puga | 565154 |
| Lucas Figueiredo Vieira | 561342 |

---

## Tecnologias utilizadas

- Java 21
- Spring Boot 4.1.1
- Spring for RabbitMQ (AMQP)
- Spring Web MVC
- Jackson Databind
- Docker
- Maven

---

## Funcionamento do Worker (Banco Tranquilo)

1. O **DBExpress** escuta continuamente a fila `pagamento-request-queue` no **RabbitMQ**.
2. Ao receber o payload JSON de um pedido enviado pelo `mercadoExpress`:
   - Simula a análise bancária do pagamento (**80% de chance de aprovação** e **20% de chance de recusa**).
3. Publica o resultado de volta para o RabbitMQ:
   - **Se Aprovado:** envia para a exchange `pagamento-response-sucesso-exchange` (routing key `pagamento-response-sucesso-rout-key` ➔ fila `pagamento-response-sucesso-queue`).
   - **Se Recusado:** envia para a exchange `pagamento-response-erro-exchange` (routing key `pagamento-response-erro-rout-key` ➔ fila `pagamento-response-erro-queue`).

### Mapa de Filas e Exchanges

| Fluxo | Exchange (`direct`, `durable`) | Routing Key | Queue (`durable`) |
|---|---|---|---|
| **Entrada (Consumo)** | `pagamento-request-exchange` | `pagamento-request-rout-key` | `pagamento-request-queue` |
| **Saída — Sucesso** | `pagamento-response-sucesso-exchange` | `pagamento-response-sucesso-rout-key` | `pagamento-response-sucesso-queue` |
| **Saída — Erro** | `pagamento-response-erro-exchange` | `pagamento-response-erro-rout-key` | `pagamento-response-erro-queue` |

---

## Como executar localmente

1. Certifique-se de que o **RabbitMQ** esteja rodando (via `docker-compose.yml` do projeto `mercadoExpress` na porta `5672`).
2. Execute a classe `DbexpressApplication` no **IntelliJ IDEA** ou pelo terminal:
```bash
./mvnw spring-boot:run
```
3. O serviço iniciará na porta `9090` (`server.port: ${PORT:9090}`) e começará a consumir imediatamente as mensagens da fila `pagamento-request-queue`.

---

## Variáveis de Ambiente (Deploy no Render / CloudAMQP)

| Variável | Descrição | Valor Padrão (Local) |
|---|---|---|
| `PORT` | Porta HTTP do serviço | `9090` |
| `RABBIT_HOST` | Host do servidor RabbitMQ / CloudAMQP | `localhost` |
| `RABBIT_PORT` | Porta AMQP | `5672` |
| `RABBIT_USER` | Usuário do RabbitMQ | `guest` |
| `RABBIT_PASSWORD` | Senha do RabbitMQ | `guest` |
| `RABBIT_VHOST` | Virtual Host do RabbitMQ | `/` |

---

## Estrutura do Projeto (`dbexpress`)

```text
dbexpress/
├── src/main/java/fiap/worker/dbexpress/
│   ├── config/
│   │   └── RabbitConfig.java                 # Declaração das Exchanges, Queues e Bindings
│   ├── consumer/
│   │   └── PagamentoRequestConsumidor.java   # Consome pagamento-request-queue e decide aprovação/erro
│   ├── producer/
│   │   ├── PagamentoSucessoProdutor.java     # Publica na exchange de sucesso
│   │   └── PagamentoErroProdutor.java        # Publica na exchange de erro
│   └── DbexpressApplication.java
├── src/main/resources/
│   └── application.yaml
└── Dockerfile
```
