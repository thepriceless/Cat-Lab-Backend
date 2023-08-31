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
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {"messaging"})
public class RabbitMessagingConfig {
    @Value("${queue.name.with-owners}")
    public static String incomeFromUserServiceQueueName;

    @Value("${queue.name.back-to-users}")
    public static String outcomeToUserServiceQueueName;

    @Value("${queue.name.with-cats}")
    public static String outcomeToCatServiceQueueName;

    @Value("${spring.rabbitmq.template.exchange}")
    private String exchangeName;

    @Value("${spring.rabbitmq.template.routing-key.owners}")
    public static String incomeFromUserServiceRoutingName;

    @Value("${spring.rabbitmq.template.routing-key.users}")
    public static String outcomeToUserServiceRoutingName;

    @Value("${spring.rabbitmq.template.routing-key.cats}")
    public static String outcomeToCatServiceRoutingName;

    @Value("${spring.rabbitmq.host}")
    private String connectFactoryName;

    @Bean
    public Queue incomeFromUserServiceQueue() {
        return new Queue(incomeFromUserServiceQueueName, true);
    }

    @Bean
    public Queue outcomeToUserServiceQueue() {
        return new Queue(outcomeToUserServiceQueueName, true);
    }

    @Bean
    public Queue outcomeToCatServiceQueue() {
        return new Queue(outcomeToCatServiceQueueName, true);
    }

    @Bean
    DirectExchange exchange(){
        return new DirectExchange(exchangeName);
    }

    @Bean
    Binding bindingOutcomeToUserService(Queue outcomeToUserServiceQueue, DirectExchange exchange){
        return BindingBuilder.bind(outcomeToUserServiceQueue).to(exchange).with(outcomeToUserServiceRoutingName);
    }

    @Bean
    Binding bindingOutcomeToCatService(Queue outcomeToCatServiceQueue, DirectExchange exchange){
        return BindingBuilder.bind(outcomeToCatServiceQueue).to(exchange).with(outcomeToCatServiceRoutingName);
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
    private void setIncomeFromUserServiceQueueNameStatic(String name){
        RabbitMessagingConfig.incomeFromUserServiceQueueName = name;
    }

    @Value("${queue.name.back-to-users}")
    private void setOutcomeToUserServiceQueueNameStatic(String name){
        RabbitMessagingConfig.outcomeToUserServiceQueueName = name;
    }

    @Value("${queue.name.with-cats}")
    private void setOutcomeToCatServiceQueueNameStatic(String name){
        RabbitMessagingConfig.outcomeToCatServiceQueueName = name;
    }

    @Value("${spring.rabbitmq.template.routing-key.owners}")
    private void setIncomeFromUserServiceRoutingNameStatic(String name){
        RabbitMessagingConfig.incomeFromUserServiceRoutingName = name;
    }

    @Value("${spring.rabbitmq.template.routing-key.users}")
    private void setOutcomeToUserServiceRoutingNameStatic(String name){
        RabbitMessagingConfig.outcomeToUserServiceRoutingName = name;
    }

    @Value("${spring.rabbitmq.template.routing-key.cats}")
    private void setOutcomeToCatServiceRoutingNameStatic(String name){
        RabbitMessagingConfig.outcomeToCatServiceRoutingName = name;
    }
}
