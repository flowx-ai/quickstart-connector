package ai.flowx.quickstart.connector.config;

import ai.flowx.quickstart.connector.dto.KafkaRequestMessageDTO;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.Map;

@Slf4j
@Configuration
@EnableKafka
public class KafkaConfiguration {

    private ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

    @Value(value = "${kafka.consumer.threads}")
    private Integer threadsNumber;

    @Value(value = "${kafka.auth-exception-retry-interval}")
    private Long authorizationExceptionRetryInterval;

    @PostConstruct
    private void configure() {
        initializeThreadPoolExecutor(executor, "kafka-connector-thread-group", "KafkaConnectorConsumerThread", threadsNumber);
    }

    private void initializeThreadPoolExecutor(ThreadPoolTaskExecutor threadPoolTaskExecutor, String threadGroupName, String threadNamePrefix, Integer threadsNumbers){
        threadPoolTaskExecutor.setThreadGroupName(threadGroupName);
        threadPoolTaskExecutor.setCorePoolSize(threadsNumbers);
        log.info("Configuring kafka consumers thread pool for group " + threadGroupName + " with " + threadPoolTaskExecutor.getCorePoolSize() + " threads.");

        threadPoolTaskExecutor.setThreadNamePrefix(threadNamePrefix);
        threadPoolTaskExecutor.setWaitForTasksToCompleteOnShutdown(true);
        threadPoolTaskExecutor.initialize();
    }

    @Bean
    public SeekToCurrentErrorHandler errorHandler() {
        SeekToCurrentErrorHandler handler = new SeekToCurrentErrorHandler();
        //handler.addNotRetryableExceptions(Exception.class); // TODO optional: add custom exception if needed
        return handler;
    }

    @Bean
    public ConsumerFactory<String, KafkaRequestMessageDTO> consumerFactoryKafkaMessage(KafkaProperties kafkaProperties) {
        Map<String, Object> props = kafkaProperties.buildConsumerProperties();
        props.put(JsonDeserializer.REMOVE_TYPE_INFO_HEADERS, true);
        props.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, KafkaRequestMessageDTO.class);

        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(),
                new ErrorHandlingDeserializer<>(new JsonDeserializer<>(KafkaRequestMessageDTO.class)));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, KafkaRequestMessageDTO> listenerContainerFactory(SeekToCurrentErrorHandler errorHandler, KafkaProperties kafkaProperties) {
        ConcurrentKafkaListenerContainerFactory<String, KafkaRequestMessageDTO> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactoryKafkaMessage(kafkaProperties));
        factory.setConcurrency(threadsNumber);
        factory.setErrorHandler(errorHandler);
        factory.getContainerProperties().setConsumerTaskExecutor(executor);
        factory.setContainerCustomizer(
                container -> container.getContainerProperties().setAuthorizationExceptionRetryInterval(
                        Duration.ofSeconds(authorizationExceptionRetryInterval)));
        return factory;
    }
}
