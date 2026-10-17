package fiap.worker.dbexpress.producer;

import fiap.worker.dbexpress.config.RabbitConfig;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Service;

@Service
public class PagamentoErroProdutor {

    private final AmqpTemplate amqpTemplate;

    public PagamentoErroProdutor(AmqpTemplate amqpTemplate) {
        this.amqpTemplate = amqpTemplate;
    }

    public void gerarResposta(String mensagem) {
        amqpTemplate.convertAndSend(
                RabbitConfig.ERRO_EXCHANGE,
                RabbitConfig.ERRO_ROUTING_KEY,
                mensagem);
    }
}
