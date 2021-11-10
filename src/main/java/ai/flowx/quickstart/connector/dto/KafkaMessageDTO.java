package ai.flowx.quickstart.connector.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class KafkaMessageDTO {
    private String processInstanceUuid;
    private String message;
}
