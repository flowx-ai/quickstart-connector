package ai.flowx.quickstart.connector.service.impl;

import ai.flowx.quickstart.connector.service.MessageSenderService;
import io.opentracing.Span;
import io.opentracing.SpanContext;
import io.opentracing.Tracer;
import io.opentracing.contrib.kafka.TracingKafkaUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Headers;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import static ai.flowx.quickstart.connector.kafka.KafkaUtils.headersToList;

@Slf4j
@Component
public class KafkaSenderServiceImpl implements MessageSenderService {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String responseTopic;

    private final Tracer tracer;

    public KafkaSenderServiceImpl(
            final KafkaTemplate<String, Object> kafkaTemplate,
            final @Value("${kafka.topicNameOut}") String responseTopic, final Tracer tracer) {
        this.kafkaTemplate = kafkaTemplate;
        this.responseTopic = responseTopic;
        this.tracer = tracer;
    }

    @Override
    public void sendMessage(Headers headers, String key, Object payload, Span span) {
        ProducerRecord<String, Object> producerRecord = new ProducerRecord<>(responseTopic, key, payload);
        populateHeaders(producerRecord, headers, span.context());

        kafkaTemplate.send(producerRecord);

        log.info("Sent message {} with headers {}", producerRecord.value(), headersToList(producerRecord.headers()));
    }

    private void populateHeaders(ProducerRecord<String, Object> producerRecord,
                                 Headers headers, SpanContext spanContext) {
        if (headers != null) {
            headers.forEach(header -> producerRecord.headers().add(header));
        }
        TracingKafkaUtils.inject(spanContext, producerRecord.headers(), tracer);
    }
}
