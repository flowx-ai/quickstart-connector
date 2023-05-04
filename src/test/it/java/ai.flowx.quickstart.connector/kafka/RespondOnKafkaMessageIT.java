package ai.flowx.quickstart.connector.kafka;

import ai.flowx.quickstart.connector.BaseIT;
import ai.flowx.quickstart.connector.dto.KafkaRequestMessageDTO;
import ai.flowx.quickstart.connector.dto.KafkaResponseMessageDTO;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.utils.KafkaTestUtils;

import java.io.IOException;
import java.time.Duration;
import java.util.Collections;

import static org.junit.Assert.assertEquals;

public class RespondOnKafkaMessageIT extends BaseIT {

    private static final int KAFKA_CONSUMER_WAIT_TIME_SEC = 10;


    @Value("${kafka.in.connector}")
    private String topicIn;/// = "ai.flowx.starwars-ships.in";

    @Value("${kafka.out.connector}")
    private String topicOut;/// = "ai.flowx.starwars-ships.out";



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

    @Test
    public void givenProperId_ShouldReturnPayload() throws IOException {

        var validRequest = jsonResourceReader.readObjectFromMocks("kafka/getStarship.json", KafkaRequestMessageDTO.class);

        kafkaConsumer = kafkaApi.createConsumer(Collections.singleton(topicOut));

        ProducerRecord<String, KafkaRequestMessageDTO> producerRecord = new ProducerRecord<>(topicIn, validRequest);

        kafkaTemplate.send(producerRecord);

        ConsumerRecords<String, String> records = KafkaTestUtils.getRecords(kafkaConsumer, Duration.ofSeconds(KAFKA_CONSUMER_WAIT_TIME_SEC).toMillis());

        Assert.assertEquals(records.count(), 1);

        ConsumerRecord<String, String> received = records.iterator().next();
        KafkaResponseMessageDTO kafkaReponse = objectMapper.readValue(received.value(), KafkaResponseMessageDTO.class);

        assertEquals(kafkaReponse.getName(), "Death Star");
    }
}
