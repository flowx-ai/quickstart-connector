package ai.flowx.quickstart.connector.service.impl;

import ai.flowx.quickstart.connector.config.JaegerConstants;
import ai.flowx.quickstart.connector.dto.KafkaMessageDTO;
import ai.flowx.quickstart.connector.service.MessageHandlerService;
import ai.flowx.quickstart.connector.service.MessageSenderService;
import io.opentracing.Span;
import io.opentracing.SpanContext;
import io.opentracing.Tracer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.header.Headers;
import org.springframework.stereotype.Service;

import java.util.Map;

import static java.util.Map.entry;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageHandlerServiceImpl implements MessageHandlerService {

    private final Tracer tracer;

    private final MessageSenderService messageSenderService;

    @Override
    public void process(KafkaMessageDTO kafkaMessage, Headers headers, SpanContext spanContext) {
        Span span = tracer.buildSpan(JaegerConstants.JAEGER_SPAN_PROCESS_MESSAGE).asChildOf(spanContext).start();
        span.setTag(JaegerConstants.JAEGER_TAG_PROCESS_INSTANCE_UUID, kafkaMessage.getProcessInstanceUuid());

        span.log(Map.ofEntries(entry(JaegerConstants.JAEGER_LOG_PROCESS_MESSAGE, kafkaMessage.toString())));

        span.finish();

        // TODO : Implement message processing logic
        String result = "Message processed successfully";

        // TODO : Make sure to send the process instance uuid as a key for the Kafka message
        messageSenderService.sendMessage(headers, result, kafkaMessage.getProcessInstanceUuid(), span);
    }
}
