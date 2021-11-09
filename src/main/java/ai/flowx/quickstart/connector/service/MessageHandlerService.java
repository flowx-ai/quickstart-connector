package ai.flowx.quickstart.connector.service;

import ai.flowx.quickstart.connector.dto.KafkaMessageDTO;
import io.opentracing.SpanContext;
import org.apache.kafka.common.header.Headers;

public interface MessageHandlerService {
    void process(KafkaMessageDTO kafkaMessage, Headers headers, SpanContext spanContext);
}
