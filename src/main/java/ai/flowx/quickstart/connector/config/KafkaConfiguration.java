package ai.flowx.quickstart.connector.config;

import ai.flowx.quickstart.connector.dto.KafkaMessageDTO;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.SeekToCurrentErrorHandler;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.time.Duration;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConfiguration {

    @Value(value = "${kafka.authorizationExceptionRetryInterval}")
    private Long authorizationExceptionRetryInterval;

    @Bean
    public SeekToCurrentErrorHandler errorHandler() {
        SeekToCurrentErrorHandler handler = new SeekToCurrentErrorHandler();
        handler.addNotRetryableExceptions(Exception.class); // TODO add custom exception
        return handler;
    }

    @Bean
    public ConsumerFactory<String, KafkaMessageDTO> consumerKafkaMessage(KafkaProperties kafkaProperties) {
        Map<String, Object> props = kafkaProperties.buildConsumerProperties();
        props.put(JsonDeserializer.REMOVE_TYPE_INFO_HEADERS, true);
        props.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, KafkaMessageDTO.class);
        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), new JsonDeserializer<>(KafkaMessageDTO.class, false));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, KafkaMessageDTO> listenerContainerFactory(
            ConsumerFactory<String, KafkaMessageDTO> consumerFinishedProcess,
            @Value("${kafka.consumerThreads:1}") int consumerThreads,
            SeekToCurrentErrorHandler errorHandler) {
        ConcurrentKafkaListenerContainerFactory<String, KafkaMessageDTO> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFinishedProcess);
        factory.setConcurrency(consumerThreads);
        factory.setErrorHandler(errorHandler);
        factory.setContainerCustomizer(
                container -> container.getContainerProperties().setAuthorizationExceptionRetryInterval(
                        Duration.ofSeconds(authorizationExceptionRetryInterval)));
        return factory;
    }
}
