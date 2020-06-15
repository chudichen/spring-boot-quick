package com.quick.mq.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * @author Michael Chu
 * @since 2020-06-15 22:16
 */
@Configuration
public class RabbitMqConfig {

    @Value("${rabbitmq.host}")
    private String host;

    @Value("${rabbitmq.port}")
    private int port;

    @Value("${rabbitmq.username}")
    private String username;

    @Value("${rabbitmq.password}")
    private String password;

    @Value("${rabbitmq.publisher-confirms}")
    private boolean publisherConfirm;

    @Value("${rabbitmq.virtual-host}")
    private String virtualHost;

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost(host);
        connectionFactory.setHost(host);
        connectionFactory.setPort(port);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        connectionFactory.setVirtualHost(virtualHost);
        // 必须要设置
        connectionFactory.setPublisherConfirms(publisherConfirm);
        return connectionFactory;
    }


    /**
     * 自己控制失效时间的queue
     */
    public static final String DELAY_QUEUE_MSG = "delay_queue";

    /**
     * DLX
     */
    public static final String DELAY_EXCHANGE_NAME = "delay_exchange";

    public static final String PROCESS_QUEUE = "process_queue";

    public static final String PROCESS_EXCHANGE_NAME = "process_exchange";

    public static final String ROUTING_KEY = "delay";

    public static final String DLX_EXCHANGE = "x-dead-letter-exchange";

    public static final String DLX_ROUTING_KEY = "x-dead-letter-routing-key";

    public static final String DLX_MESSAGE_TTL = "x-message-ttl";

    public static final String DELAY_QUEUE_1S = "delay_queue_1s";

    public static final String DELAY_EXCHANGE_1S = "delay_exchange_1s";

    public static final String DELAY_QUEUE_5S = "delay_queue_5s";

    public static final String DELAY_EXCHANGE_5S = "delay_exchange_5s";

    public static final String DELAY_QUEUE_10S = "delay_queue_10s";

    public static final String DELAY_EXCHANGE_10S = "delay_exchange_10s";


    @Bean
    public DirectExchange delayExchange() {
        return new DirectExchange(DELAY_EXCHANGE_NAME);
    }

    @Bean
    public DirectExchange processExchange() {
        return new DirectExchange(PROCESS_EXCHANGE_NAME);
    }

    @Bean
    public Queue delayQueueForMsg() {
        return QueueBuilder.durable(DELAY_QUEUE_MSG)
                .withArgument(DLX_EXCHANGE, PROCESS_EXCHANGE_NAME)
                .withArgument(DLX_ROUTING_KEY, ROUTING_KEY)
                .build();
    }

    @Bean
    public Queue processQueue() {
        return QueueBuilder.durable(PROCESS_QUEUE)
                .build();
    }

    /**
     * 将延迟队列与exchange绑定，即到达指定时间之后需要转交给queue消费
     *
     * @return 绑定
     */
    @Bean
    public Binding delayBinding() {
        return BindingBuilder.bind(delayQueueForMsg())
                .to(delayExchange())
                .with(ROUTING_KEY);
    }

    @Bean
    public Binding queueBinding() {
        return BindingBuilder.bind(processQueue())
                .to(processExchange())
                .with(ROUTING_KEY);
    }

    /**
     * 延迟定时队列
     */
    @Bean
    public DirectExchange delayExchange1s() {
        return new DirectExchange(DELAY_EXCHANGE_1S);
    }

    @Bean
    public Queue delayQueue1s() {
        return QueueBuilder.durable(DELAY_QUEUE_1S)
                .withArgument(DLX_EXCHANGE, PROCESS_EXCHANGE_NAME)
                .withArgument(DLX_ROUTING_KEY, ROUTING_KEY)
                .withArgument(DLX_MESSAGE_TTL, 1000)
                .build();
    }

    @Bean
    public Binding delayQueue1sBind() {
        return BindingBuilder.bind(delayQueue1s())
                .to(delayExchange1s())
                .with(ROUTING_KEY);
    }



    @Bean
    public DirectExchange delayExchange5s() {
        return new DirectExchange(DELAY_EXCHANGE_5S);
    }

    @Bean
    public Queue delayQueue5s() {
        return QueueBuilder.durable(DELAY_QUEUE_5S)
                .withArgument(DLX_EXCHANGE, PROCESS_EXCHANGE_NAME)
                .withArgument(DLX_ROUTING_KEY, ROUTING_KEY)
                .withArgument(DLX_MESSAGE_TTL, 5000)
                .build();
    }

    @Bean
    public Binding delayQueue5sBind() {
        return BindingBuilder.bind(delayQueue5s())
                .to(delayExchange5s())
                .with(ROUTING_KEY);
    }

    @Bean
    public DirectExchange delayExchange10s() {
        return new DirectExchange(DELAY_EXCHANGE_10S);
    }

    @Bean
    public Queue delayQueue10s() {
        return QueueBuilder.durable(DELAY_QUEUE_10S)
                .withArgument(DLX_EXCHANGE, PROCESS_EXCHANGE_NAME)
                .withArgument(DLX_ROUTING_KEY, ROUTING_KEY)
                .withArgument(DLX_MESSAGE_TTL, 10000)
                .build();
    }

    @Bean
    public Binding delayQueue10sBind() {
        return BindingBuilder.bind(delayQueue10s())
                .to(delayExchange10s())
                .with(ROUTING_KEY);
    }

    /**
     * 必须是prototype类型
     */
    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public RabbitTemplate rabbitTemplate() {
        RabbitTemplate template = new RabbitTemplate(connectionFactory());
        return template;
    }

}
