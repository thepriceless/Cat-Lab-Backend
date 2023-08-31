package messaging;

import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

@Component
@ComponentScan(basePackages = {"messaging"})
public class RabbitMessagingClient {
    private final RabbitTemplate template;

    private final DirectExchange exchange;

    @Autowired
    public RabbitMessagingClient(RabbitTemplate template, DirectExchange exchange) {
        this.template = template;
        this.exchange = exchange;
    }

    public <T> void sendWithoutResponse(String routingName, T messageBody, String headerParam) {
        template.convertAndSend(exchange.getName(), routingName, messageBody, m -> {
            m.getMessageProperties().getHeaders().put("keyHeader", headerParam);
            return m;
        });
    }

    public <T, G> G sendAndReceive(String routingName, T messageBody, Class<G> responseClass, String headerParam) {
        return (G) template.convertSendAndReceive(exchange.getName(), routingName, messageBody, m -> {
            m.getMessageProperties().getHeaders().put("keyHeader", headerParam);
            return m;
        });
    }
}
