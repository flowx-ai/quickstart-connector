package ai.flowx.quickstart.connector.dto;

import lombok.Data;

@Data
public class StarwarsShipApiResponseDTO implements BaseApiResponseDTO{
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
}
