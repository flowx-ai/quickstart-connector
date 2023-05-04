package ai.flowx.quickstart.connector.service.impl;

import ai.flowx.commons.kafka.KafkaUtils;
import ai.flowx.commons.trace.aop.Trace;
import ai.flowx.quickstart.connector.dto.KafkaRequestMessageDTO;
import ai.flowx.quickstart.connector.dto.KafkaResponseMessageDTO;
import ai.flowx.quickstart.connector.dto.StarwarsShipApiResponseDTO;
import ai.flowx.quickstart.connector.exception.StarwarsStarshipException;
import ai.flowx.quickstart.connector.service.ApiService;
import ai.flowx.quickstart.connector.service.MessageHandlerService;
import ai.flowx.quickstart.connector.service.MessageSenderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.header.Headers;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Trace
@Slf4j
@Service
@RequiredArgsConstructor
public class MessageHandlerServiceImpl implements MessageHandlerService {

    private final MessageSenderService messageSenderService;
    private final ApiService apiService;

    @Value("${starwars-api.scheme}")
    private String scheme;
    @Value("${starwars-api.host}")
    private String host;
    @Value("${starwars-api.path}")
    private String path;
    @Override
    public void process(KafkaRequestMessageDTO kafkaMessage, Headers headers)  {
        String processInstanceUuid = KafkaUtils.extractHeaderString(headers, "processInstanceUuid");

        KafkaResponseMessageDTO.KafkaResponseMessageDTOBuilder responseMessageDTOBuilder = KafkaResponseMessageDTO.builder();

        try{
            StarwarsShipApiResponseDTO responseBody = (StarwarsShipApiResponseDTO) apiService.blockingCall(StarwarsShipApiResponseDTO.class, scheme, host, path, kafkaMessage.getId());

            responseMessageDTOBuilder.model(responseBody.getModel())
                    .crew(responseBody.getCrew())
                    .name(responseBody.getName())
                    .cargo_capacity(responseBody.getCargo_capacity())
                    .cost_in_credits(responseBody.getCost_in_credits())
                    .hyperdrive_rating(responseBody.getHyperdrive_rating())
                    .manufacturer(responseBody.getManufacturer())
                    .passengers(responseBody.getPassengers())
                    .starship_class(responseBody.getStarship_class());

        }
        catch (StarwarsStarshipException exc){
            responseMessageDTOBuilder.errorMessage(exc.getMessage());
            messageSenderService.sendMessage(headers, processInstanceUuid, responseMessageDTOBuilder.build());
        }


        messageSenderService.sendMessage(headers, processInstanceUuid, responseMessageDTOBuilder.build());

    }
}
