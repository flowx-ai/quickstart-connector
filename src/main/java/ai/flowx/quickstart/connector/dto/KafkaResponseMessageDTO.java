package ai.flowx.quickstart.connector.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
public class KafkaResponseMessageDTO { // TODO 6. outgoing DTO format
    private String name;
    private String model;
    private String starship_class;
    private String manufacturer;
    private String cost_in_credits;
    private String length;
    private String crew;
    private String passengers;
    private String hyperdrive_rating;
    private String cargo_capacity;

    private String errorMessage;
}
