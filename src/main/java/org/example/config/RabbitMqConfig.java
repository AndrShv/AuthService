package org.example.config;


import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Configuration
public class RabbitMqConfig  {

    @Value("${queue.name}")
    private String queueName;

    @Value("${spring.rabbitmq.username}")
    private String username;

    @Value("${spring.rabbitmq.password}")
    private String password;

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory factory = new CachingConnectionFactory("localhost");
        factory.setUsername(username);
        factory.setPassword(password);
        return factory;
    }

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public AmqpTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }

    // --- Exchanges ---
    @Bean
    public TopicExchange userExchange() {
        return new TopicExchange("user.exchange");
    }

    @Bean
    public TopicExchange videoExchange() {
        return new TopicExchange("video.exchange");
    }
    @Bean
    public TopicExchange userProfileExchange() {
        return new TopicExchange("user.profile.exchange");
    }

    // --- Queues ---
    @Bean
    public Queue userQueue() {
        return new Queue("user.registered.queue", true);
    }

    @Bean
    public Queue videoQueue() {
        return new Queue("video.create.queue", true);
    }

    @Bean
    public Queue userProfileQueue() {
        return new Queue("user.profile.queue", true);
    }

    // --- Bindings ---
    @Bean
    public Binding userBinding(Queue userQueue, TopicExchange userExchange) {
        return BindingBuilder.bind(userQueue).to(userExchange).with("user.registered");
    }

    @Bean
    public Binding videoBinding(Queue videoQueue, TopicExchange videoExchange) {
        return BindingBuilder.bind(videoQueue).to(videoExchange).with("video.create");
    }
    @Bean
    public Binding userProfileBinding(Queue userProfileQueue, TopicExchange userProfileExchange) {
        return BindingBuilder.bind(userProfileQueue).to(userProfileExchange).with("user.profile.create");
    }
}
