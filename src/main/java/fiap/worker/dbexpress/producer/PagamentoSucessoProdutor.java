package fiap.worker.dbexpress.producer;

import fiap.worker.dbexpress.config.RabbitConfig;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Service;

@Service
public class PagamentoSucessoProdutor {

    private final AmqpTemplate amqpTemplate;

    public PagamentoSucessoProdutor(AmqpTemplate amqpTemplate) {
        this.amqpTemplate = amqpTemplate;
    }

    public void gerarResposta(String mensagem) {
        amqpTemplate.convertAndSend(
                RabbitConfig.SUCESSO_EXCHANGE,
                RabbitConfig.SUCESSO_ROUTING_KEY,
                mensagem);
    }
}
