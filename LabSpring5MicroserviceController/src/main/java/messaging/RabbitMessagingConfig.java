package messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMessagingConfig {
    @Value("${queue.name.with-owners}")
    public static String outcomeToOwnersQueueName;

    @Value("${queue.name.with-cats}")
    public static String outcomeToCatsQueueName;

    @Value("${queue.name.income}")
    public static String incomeQueueName;

    @Value("${spring.rabbitmq.template.exchange}")
    private String exchangeName;

    @Value("${spring.rabbitmq.host}")
    private String connectFactoryName;

    @Value("${spring.rabbitmq.template.routing-key.owners}")
    public static String outcomeToOwnersRoutingName;

    @Value("${spring.rabbitmq.template.routing-key.cats}")
    public static String outcomeToCatsRoutingName;

    @Value("${spring.rabbitmq.template.routing-key.from-owners}")
    public static String incomeFromOwnersRoutingName;

    @Value("${spring.rabbitmq.template.routing-key.from-cats}")
    public static String incomeFromCatsRoutingName;

    @Bean
    public Queue outcomeToOwnersQueue() {
        return new Queue(outcomeToOwnersQueueName, true);
    }

    @Bean
    public Queue outcomeToCatsQueue() {
        return new Queue(outcomeToCatsQueueName, true);
    }

    @Bean
    public Queue incomeQueue() {
        return new Queue(incomeQueueName, true);
    }

    @Bean
    DirectExchange exchange(){
        return new DirectExchange(exchangeName);
    }

    @Bean
    Binding bindingOutcomeToOwnerService(Queue outcomeToOwnersQueue, DirectExchange exchange){
        return BindingBuilder.bind(outcomeToOwnersQueue).to(exchange).with(outcomeToOwnersRoutingName);
    }

    @Bean
    Binding bindingOutcomeToCatService(Queue outcomeToCatsQueue, DirectExchange exchange){
        return BindingBuilder.bind(outcomeToCatsQueue).to(exchange).with(outcomeToCatsRoutingName);
    }

    @Bean
    public CachingConnectionFactory connectionFactory() {
        return new CachingConnectionFactory(connectFactoryName);
    }

    @Bean
    public MessageConverter jsonMessageConverter(ObjectMapper objectMapper) {
        objectMapper.registerModule(new JavaTimeModule());
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    @Bean
    RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter(new ObjectMapper()));
        return rabbitTemplate;
    }

    @Value("${queue.name.with-owners}")
    private void setOutcomeToOwnersQueueNameStatic(String name){
        RabbitMessagingConfig.outcomeToOwnersQueueName = name;
    }

    @Value("${queue.name.with-cats}")
    private void setOutcomeToCatsQueueNameStatic(String name){
        RabbitMessagingConfig.outcomeToCatsQueueName = name;
    }

    @Value("${queue.name.income}")
    private void setIncomeQueueNameStatic(String name){
        RabbitMessagingConfig.incomeQueueName = name;
    }

    @Value("${spring.rabbitmq.template.routing-key.owners}")
    private void setOutcomeToOwnersRoutingNameStatic(String name){
        RabbitMessagingConfig.outcomeToOwnersRoutingName = name;
    }

    @Value("${spring.rabbitmq.template.routing-key.cats}")
    private void setOutcomeToCatsRoutingNameStatic(String name){
        RabbitMessagingConfig.outcomeToCatsRoutingName = name;
    }

    @Value("${spring.rabbitmq.template.routing-key.from-cats}")
    private void setIncomeFromCatsRoutingNameStatic(String name){
        RabbitMessagingConfig.incomeFromCatsRoutingName = name;
    }

    @Value("${spring.rabbitmq.template.routing-key.from-owners}")
    private void setIncomeFromOwnersRoutingNameStatic(String name){
        RabbitMessagingConfig.incomeFromOwnersRoutingName = name;
    }
}
