package ai.flowx.quickstart.connector.service.impl;

import ai.flowx.quickstart.connector.dto.KafkaRequestMessageDTO;
import ai.flowx.quickstart.connector.dto.KafkaResponseMessageDTO;
import ai.flowx.quickstart.connector.exception.ConnectorException;
import ai.flowx.quickstart.connector.service.MessageHandlerService;
import ai.flowx.quickstart.connector.service.MessageSenderService;
import ai.flowx.quickstart.connector.utils.KafkaUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.header.Headers;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageHandlerServiceImpl implements MessageHandlerService {

    private final MessageSenderService messageSenderService;

    @Override
    public void process(KafkaRequestMessageDTO kafkaMessage, Headers headers) {
        String processInstanceUuid = KafkaUtils.extractHeaderString(headers, "processInstanceUuid");

        KafkaResponseMessageDTO.KafkaResponseMessageDTOBuilder responseMessageDTOBuilder = KafkaResponseMessageDTO.builder();

        // Add you custom business logic here
        try {
            String name = "John Doe";
            responseMessageDTOBuilder.name(name);

            // If there is an error, throw a ConnectorException
            // throw new ConnectorException("Error message");
        } catch (ConnectorException exc) {
            responseMessageDTOBuilder.errorMessage(exc.getMessage());
            messageSenderService.sendMessage(headers, processInstanceUuid, responseMessageDTOBuilder.build());
        }

        // Send processed message
        messageSenderService.sendMessage(headers, processInstanceUuid, responseMessageDTOBuilder.build());
    }
}
