package ai.flowx.quickstart.connector.kafka;

import ai.flowx.quickstart.connector.dto.KafkaMessageDTO;
import ai.flowx.quickstart.connector.service.MessageHandlerService;
import io.opentracing.SpanContext;
import io.opentracing.Tracer;
import io.opentracing.contrib.kafka.TracingKafkaUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaMessageListener {
    private final Tracer tracer;

    private final MessageHandlerService messageHandlerService;

    @KafkaListener(topicPattern = "${kafka.topicNameIn}", containerFactory = "listenerContainerFactory")
    public void consume(ConsumerRecord<String, KafkaMessageDTO> record) {
        SpanContext spanContext = TracingKafkaUtils.extractSpanContext(record.headers(), tracer);
        log.info("Received new message from kafka {}.", record.value());
        KafkaMessageDTO dto = record.value();

        if (dto == null || StringUtils.isEmpty(dto.getProcessInstanceUuid())) {
            log.error("Message invalid: {}", dto);
            return;
        }

        messageHandlerService.process(dto, record.headers(), spanContext);
    }
}
