package ai.flowx.quickstart.connector.utils;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.RecordsToDelete;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.TopicPartitionInfo;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.testcontainers.containers.KafkaContainer;

import java.util.*;
import java.util.concurrent.ExecutionException;

public class KafkaApi {
    private final KafkaContainer kafkaContainer;
    private final AdminClient adminClient;

    public KafkaApi(KafkaContainer kafkaContainer, AdminClient adminClient) {
        this.kafkaContainer = kafkaContainer;
        this.adminClient = adminClient;
    }

    public KafkaConsumer<String, String> createConsumer(Set<String> topics) {
        return createConsumer(topics, "test-group", "true");
    }

    public KafkaConsumer<String, String> createConsumer(Set<String> topics, String group, String autocommit) {
        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps(kafkaContainer.getBootstrapServers(), group, autocommit);
        consumerProps.put("key.deserializer", StringDeserializer.class);
        consumerProps.put("value.deserializer", StringDeserializer.class);

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(consumerProps);
        consumer.subscribe(topics);

        return consumer;
    }

    public void deleteRecordsForTopic(String topicName) {
        Map<TopicPartition, RecordsToDelete> recordsToDelete = new HashMap<>();
        List<TopicPartitionInfo> partitions = null;
        try {
            partitions = adminClient.describeTopics(Collections.singleton(topicName))
                    .values().get(topicName).get().partitions();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }

        partitions.forEach(partitionInfo -> {
            TopicPartition topicPartition = new TopicPartition(topicName, partitionInfo.partition());
            recordsToDelete.put(topicPartition, RecordsToDelete.beforeOffset(-1L));
        });
        adminClient.deleteRecords(recordsToDelete);
    }
}
