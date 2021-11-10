package ai.flowx.quickstart.connector.service;

import io.opentracing.Span;
import org.apache.kafka.common.header.Headers;

public interface MessageSenderService {

    void sendMessage(Headers headers, String key, Object payload, Span span);

}
