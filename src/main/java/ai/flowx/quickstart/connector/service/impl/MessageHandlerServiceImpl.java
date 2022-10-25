package ai.flowx.quickstart.connector.service.impl;

import ai.flowx.commons.kafka.KafkaUtils;
import ai.flowx.commons.trace.aop.Trace;
import ai.flowx.quickstart.connector.dto.ExchangeApiResponseDTO;
import ai.flowx.quickstart.connector.dto.KafkaRequestMessageDTO;
import ai.flowx.quickstart.connector.dto.KafkaResponseMessageDTO;
import ai.flowx.quickstart.connector.exception.ExchangeException;
import ai.flowx.quickstart.connector.service.ApiService;
import ai.flowx.quickstart.connector.service.MessageHandlerService;
import ai.flowx.quickstart.connector.service.MessageSenderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.header.Headers;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientException;

@Trace
@Slf4j
@Service
@RequiredArgsConstructor
public class MessageHandlerServiceImpl implements MessageHandlerService {

    private final MessageSenderService messageSenderService;
    private final ApiService apiService;

    @Value("${exchangerate-api.scheme}")
    private String scheme;

    @Value("${exchangerate-api.host}")
    private String host;

    @Value("${exchangerate-api.path}")
    private String path;

    @Value("${exchangerate-api.key}")
    private String apiKey;

    @Override
    public void process(KafkaRequestMessageDTO kafkaMessage, Headers headers) {
        String processInstanceUuid = KafkaUtils.extractHeaderString(headers, "processInstanceUuid");

        KafkaResponseMessageDTO.KafkaResponseMessageDTOBuilder responseMessageDTOBuilder = KafkaResponseMessageDTO.builder();

        try {
            ExchangeApiResponseDTO responseBody = (ExchangeApiResponseDTO) apiService.blockingCall(ExchangeApiResponseDTO.class, scheme, host, path, apiKey, kafkaMessage.getFromCurrency(), kafkaMessage.getToCurrency(), kafkaMessage.getAmount().toString());

            responseMessageDTOBuilder.toCurrency(kafkaMessage.getToCurrency())
                    .fromCurrency(kafkaMessage.getFromCurrency())
                    .initialAmount(kafkaMessage.getAmount())
                    .exchangedAmount(responseBody.getConversion_result());

        } catch (WebClientException exc) {
            responseMessageDTOBuilder.errorMessage(exc.getMessage());
            messageSenderService.sendMessage(headers, processInstanceUuid, responseMessageDTOBuilder.build());
            throw new ExchangeException(exc.getMessage());
        }

        messageSenderService.sendMessage(headers, processInstanceUuid, responseMessageDTOBuilder.build());
    }
}
