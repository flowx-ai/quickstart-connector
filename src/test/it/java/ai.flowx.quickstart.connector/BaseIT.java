package ai.flowx.quickstart.connector;

import ai.flowx.quickstart.connector.config.TestConfig;
import ai.flowx.quickstart.connector.utils.JsonResourceReader;
import ai.flowx.quickstart.connector.utils.KafkaApi;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

@Testcontainers
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ConnectorApp.class})
@ActiveProfiles({"it"})
@ContextConfiguration(classes = TestConfig.class)
@DirtiesContext
@Import({BaseIT.KafkaTestContainersConfiguration.class})
public abstract class BaseIT {

    public static AdminClient adminClient;

    public static KafkaConsumer<String, String> kafkaConsumer;

    @Autowired
    public ObjectMapper objectMapper;

    public JsonResourceReader jsonResourceReader;

    public KafkaApi kafkaApi;

    @Container
    public static KafkaContainer kafkaContainer = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:latest"))
            .waitingFor(Wait.forLogMessage(".*started.*\\n", 1))
            .withReuse(false);


    @DynamicPropertySource
    static void setDynamicProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafkaContainer::getBootstrapServers);
    }

    @PostConstruct
    public void init() {
        jsonResourceReader = new JsonResourceReader(objectMapper);
        kafkaApi = new KafkaApi(kafkaContainer, adminClient);
    }

    @BeforeAll
    public static void setUp() {
        kafkaContainer.start();

        // Get the container's IP address and port
        String bootstrapServers = kafkaContainer.getBootstrapServers();

        // Create a KafkaAdminClient using the bootstrap servers
        Properties props = new Properties();
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        adminClient = AdminClient.create(props);

        // setup topics

        // ⁉️ maybe do this in a better way ...
        String[] topics = { "ai.flowx.starwars-ships.in", "ai.flowx.starwars-ships.out"};

        var newTopics = Arrays.stream(topics)
                .map(topic -> new NewTopic(topic, 1, (short) 1))
                .collect(Collectors.toList());

        adminClient.createTopics(newTopics);
    }

    @AfterAll
    public static void tearDown() {
        adminClient.close(Duration.ZERO);
        kafkaContainer.stop();
    }

    @TestConfiguration
    static class KafkaTestContainersConfiguration {
        /**
         * Not a big fan of keeping commented code on git, but might be useful if the BaseIT is reused in a project with no
         * kafkaListenerContainerFactory bean defined.
         */
//        @Bean
//        ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
//            ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
//            factory.setConsumerFactory(consumerFactory());
//            return factory;
//        }

        /**
         * The reason for having this bean is because the default consumerFactory has caching and apparently
         * SpringBootTest does not reset that cache even if class is annotated with @DirtiesContext.
         * This lead to consumers being created using a wrong broker address of a destroyed container.
         * <p>
         * This consumerFactory is used by KafkaConsumerConfig.java in the kafkaListenerContainerFactory method.
         */
        @Bean
        public ConsumerFactory<String, String> consumerFactory() {
            return new DefaultKafkaConsumerFactory<>(consumerConfigs());
        }

        @Bean
        public Map<String, Object> consumerConfigs() {
            Map<String, Object> props = new HashMap<>();
            props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaContainer.getBootstrapServers());
            props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
            props.put(ConsumerConfig.GROUP_ID_CONFIG, "flowx-test");
            props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
            props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
            return props;
        }

        @Bean
        public ProducerFactory<String, Object> producerFactory(KafkaProperties kafkaProperties) {
            DefaultKafkaProducerFactory<String, Object> producerFactory =
                    new DefaultKafkaProducerFactory<>(kafkaProperties.buildProducerProperties());
            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            mapper.registerModule(new JavaTimeModule());

            org.springframework.kafka.support.serializer.JsonSerializer<Object> jsonSerializer = new org.springframework.kafka.support.serializer.JsonSerializer<>(mapper);
            producerFactory.setValueSerializer(jsonSerializer);
            return producerFactory;
        }

        @Bean
        public KafkaTemplate<String, ?> kafkaTemplate(ProducerFactory<String, Object> producerFactory) {
            return new KafkaTemplate<>(producerFactory);
        }
    }
}