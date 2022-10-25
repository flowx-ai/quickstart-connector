package ai.flowx.quickstart.connector.dto;

import lombok.Data;

@Data
public class ExchangeApiResponseDTO implements BaseApiResponseDTO {
    private String result;
    private String base_code;
    private String target_code;
    private double conversion_rate;
    private double conversion_result;
}
