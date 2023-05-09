package ai.flowx.quickstart.connector.kafka;

import ai.flowx.quickstart.connector.BaseIT;
import ai.flowx.quickstart.connector.dto.KafkaRequestMessageDTO;
import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;

import java.io.IOException;
import java.time.Duration;
import java.util.Collections;

public class RespondOnKafkaMessageIT extends BaseIT { // 👈 The class name should end it IT to be picked up by the maven runner

    private static final int KAFKA_CONSUMER_WAIT_TIME_SEC = 10;


    @Value("${kafka.topic.in}")
    private String topicIn;

    @Value("${kafka.topic.out}")
    private String topicOut;



    @Autowired
    private KafkaTemplate<String, KafkaRequestMessageDTO> kafkaTemplate;


    @BeforeEach
    public void cleanupKafkaBefore() {
        try {
            kafkaApi.deleteRecordsForTopic(topicIn);
            kafkaApi.deleteRecordsForTopic(topicOut);
        } catch (Exception e) {
            System.out.println("Could not delete records from Kafka topics " + e);
        }
    }

    @AfterEach
    public void cleanupKafka() {
        kafkaTemplate.destroy();
        kafkaConsumer.unsubscribe();
        kafkaConsumer.close(Duration.ZERO);
    }

    // 👇 An example for an integration test
    @Test
    public void givenSomething_somethingShouldHappen() throws IOException {

        kafkaConsumer = kafkaApi.createConsumer(Collections.singleton(topicOut)); // 👈 make sure to instantiat the kafka consumer
        Assert.assertTrue(true);
    }
}
