package ai.flowx.quickstart.connector.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class KafkaRequestMessageDTO {
    private String fromCurrency;
    private String toCurrency;
    private Double amount;
}
