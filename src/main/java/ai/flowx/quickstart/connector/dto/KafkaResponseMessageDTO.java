package ai.flowx.quickstart.connector.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
public class KafkaResponseMessageDTO implements BaseApiResponseDTO { // TODO 6. outgoing DTO format
    private String name;
    private String errorMessage;
}
