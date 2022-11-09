package ai.flowx.quickstart.connector.service.impl;

import ai.flowx.commons.trace.aop.Trace;
import ai.flowx.commons.trace.service.KafkaUtils;
import ai.flowx.quickstart.connector.dto.KafkaRequestMessageDTO;
import ai.flowx.quickstart.connector.dto.KafkaResponseMessageDTO;
import ai.flowx.quickstart.connector.service.MessageHandlerService;
import ai.flowx.quickstart.connector.service.MessageSenderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.header.Headers;
import org.springframework.stereotype.Service;

@Trace
@Slf4j
@Service
@RequiredArgsConstructor
public class MessageHandlerServiceImpl implements MessageHandlerService {

    private final MessageSenderService messageSenderService;

    @Override
    public void process(KafkaRequestMessageDTO kafkaMessage, Headers headers) {
        String processInstanceUuid = KafkaUtils.extractHeaderString(headers, "processInstanceUuid");

        // TODO 7. Implement message processing logic
        KafkaResponseMessageDTO responseMessageDTO = KafkaResponseMessageDTO.builder()
                .response("Message processed successfully")
                .build();

        // TODO 8. Make sure to send the process instance uuid as a key for the Kafka message
        messageSenderService.sendMessage(headers, processInstanceUuid, responseMessageDTO);
    }
}
