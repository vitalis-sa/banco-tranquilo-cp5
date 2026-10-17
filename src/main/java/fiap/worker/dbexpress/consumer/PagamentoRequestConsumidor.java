package fiap.worker.dbexpress.consumer;

import fiap.worker.dbexpress.config.RabbitConfig;
import fiap.worker.dbexpress.producer.PagamentoErroProdutor;
import fiap.worker.dbexpress.producer.PagamentoSucessoProdutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import java.util.concurrent.ThreadLocalRandom;

@Component
public class PagamentoRequestConsumidor {

    private static final Logger log = LoggerFactory.getLogger(PagamentoRequestConsumidor.class);
    private static final int CHANCE_APROVACAO_PERCENTUAL = 80;

    private final PagamentoErroProdutor erroProdutor;
    private final PagamentoSucessoProdutor sucessoProdutor;
    private final JsonMapper jsonMapper;

    public PagamentoRequestConsumidor(PagamentoErroProdutor erroProdutor,
                                      PagamentoSucessoProdutor sucessoProdutor,
                                      JsonMapper jsonMapper) {
        this.erroProdutor = erroProdutor;
        this.sucessoProdutor = sucessoProdutor;
        this.jsonMapper = jsonMapper;
    }

    @RabbitListener(queues = {RabbitConfig.REQUEST_QUEUE})
    public void receberMensagem(@Payload String payload) {
        JsonNode json = jsonMapper.readTree(payload);
        String numeroPedido = json.path("numeroPedido").asText("desconhecido");

        if (ThreadLocalRandom.current().nextInt(100) < CHANCE_APROVACAO_PERCENTUAL) {
            log.info("Pedido {} aprovado pelo Banco Tranquilo.", numeroPedido);
            sucessoProdutor.gerarResposta(payload);
        } else {
            log.warn("Pedido {} recusado pelo Banco Tranquilo.", numeroPedido);
            erroProdutor.gerarResposta(payload);
        }
    }
}
