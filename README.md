# DBExpress — Worker Banco Tranquilo (CP5)

Microsserviço Worker desenvolvido com **Spring Boot 4.1.1**, **Java 21** e **Spring for RabbitMQ (AMQP)** que simula a operadora financeira **Banco Tranquilo** para processamento assíncrono dos pagamentos solicitados pelo **Mercado Express**.

**IDE utilizada:** IntelliJ IDEA

---

## Links do Projeto

- **Link de Produção (Render):** [`LINK DO RENDER_DBEXPRESS`](https://banco-tranquilo-cp5.onrender.com/)
- **Repositório Principal (`mercadoExpress`):** [`LINK DO GITHUB_MERCADOEXPRESS`](https://github.com/vitalis-sa/mercado-express-cp5)
- **Vídeo de Demonstração (YouTube):** [`LINK DO VIDEO`](https://www.youtube.com/watch?v=9-1BpcvanuU)

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

## Configuração do Spring Starter

<img width="1199" height="567" alt="image" src="https://github.com/user-attachments/assets/ee0767e8-1dd8-4bc6-8ed0-b8b3e04159cd" />

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

## Parte 2 — Mensageria com RabbitMQ, Banco Tranquilo, E-mail e Dashboard

### Mapa de Exchanges, Routing Keys e Queues

| Fluxo | Exchange (`direct`, `durable`) | Routing Key | Queue (`durable`) |
|---|---|---|---|
| **1. Solicitação de Pagamento (API ➔ Worker)** | `pagamento-request-exchange` | `pagamento-request-rout-key` | `pagamento-request-queue` |
| **2. Resposta de Sucesso (Worker ➔ API)** | `pagamento-response-sucesso-exchange` | `pagamento-response-sucesso-rout-key` | `pagamento-response-sucesso-queue` |
| **3. Resposta de Erro (Worker ➔ API)** | `pagamento-response-erro-exchange` | `pagamento-response-erro-rout-key` | `pagamento-response-erro-queue` |
| **4. Envio de E-mail (Fila Direta)** | *(Default Exchange)* | `ms.email` | `ms.email` |

<img width="1098" height="441" alt="image" src="https://github.com/user-attachments/assets/8590d5ce-d7e3-4b45-8626-f9d0401a5790" />

---

### Exemplos de Requisições JSON para Testes no Postman

> Configurar **Authorization -> Basic Auth** com `admin@mercado.com` / `admin123`.

#### 1. Solicitar Compra / Pagamento (`POST http://localhost:8080/pagamentos`)
```json
{
  "numeroPedido": "171",
  "valor": 550,
  "produto": "camiseta",
  "emailCliente": "cliente@mercado.com"
}
```

#### 2. Enviar E-mail Direto (`POST http://localhost:8080/sending-email` ou via fila `ms.email` no RabbitMQ)
```json
{
  "ownerRef": "Tranquilo",
  "emailFrom": "seuemail@gmail.com",
  "emailTo": "destinatario@gmail.com",
  "subject": "Aproveita que está fácil",
  "text": "Olá, tudo bem? To enviando este e-mail somente para ficar tranquilo mesmo!!! Atenciosamente, Prof. Marcel"
}
```

---

### Carrinho de Compras e Meus Pedidos (`GET /carrinho` e `GET /compras`)

<img width="1919" height="412" alt="image" src="https://github.com/user-attachments/assets/94ff19b1-8f4c-44f5-a559-cd879d124e99" />

<img width="1919" height="326" alt="image" src="https://github.com/user-attachments/assets/3cfa7acf-8881-4748-ab2d-0f0f92e206d9" />

<img width="1915" height="460" alt="image" src="https://github.com/user-attachments/assets/b25da573-a1d0-4387-9bdb-6292d3ad4013" />

---

### E-mail de Confirmação Recebido e Persistência em `TB_EMAIL`

<img width="1132" height="259" alt="image" src="https://github.com/user-attachments/assets/cc67c289-4798-4dd5-b066-f0b39edd584f" />

Tabela de teste local
<img width="1643" height="356" alt="image" src="https://github.com/user-attachments/assets/7e804d33-16fb-4d13-8a74-bd2770823231" />

---

### Dashboard Gerencial (`GET /dashboard`)

Exibe o total de compras, compras aprovadas/recusadas/pendentes, receita total, lucro total, controle de e-mails enviados (`TB_EMAIL`) e gráficos interativos (Chart.js) de vendas por dia, produtos vendidos e estoque atual.

<img width="1906" height="920" alt="image" src="https://github.com/user-attachments/assets/f4de489f-5a53-4cc2-98fe-1262d4bd4167" />

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
