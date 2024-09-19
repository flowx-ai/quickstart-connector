package ai.flowx.quickstart.connector.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.converter.RecordMessageConverter;
import org.springframework.kafka.support.converter.StringJsonMessageConverter;

import java.time.Duration;

@Slf4j
@Configuration
@EnableKafka
public class KafkaConfiguration {

    @Value(value = "${kafka.consumer.threads}")
    private Integer threadsNumber;

    @Value(value = "${kafka.auth-exception-retry-interval}")
    private Long authorizationExceptionRetryInterval;

    @Bean
    public DefaultErrorHandler errorHandler() {
        return new DefaultErrorHandler();
    }

    @Bean
    public StringJsonMessageConverter messageConverter(ObjectMapper objectMapper) {
        return new StringJsonMessageConverter(objectMapper);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> listenerContainerFactory(ConsumerFactory consumerFactory, RecordMessageConverter messageConverter, DefaultErrorHandler errorHandler) {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.setRecordMessageConverter(messageConverter);
        factory.setConcurrency(threadsNumber);
        factory.setCommonErrorHandler(errorHandler);
        factory.setContainerCustomizer(
                container -> container.getContainerProperties().setAuthExceptionRetryInterval(
                        Duration.ofSeconds(authorizationExceptionRetryInterval)));
        return factory;
    }
}
