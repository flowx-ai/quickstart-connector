package ai.flowx.quickstart.connector.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.GenericContainer;

import java.time.Duration;
import java.time.Instant;

public class MailhogApi {
    private final GenericContainer mailhogContainer;
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;
    private final Integer httpPort;
    private final Integer smtpPort;

    private final String mailHogUrl;
    private final String emailsUrlV2;
    private final String emailsUrlV1;

    public MailhogApi(GenericContainer mailhogContainer,
                      ObjectMapper objectMapper,
                      RestTemplate restTemplate,
                      Integer httpPort,
                      Integer smtpPort) {
        this.mailhogContainer = mailhogContainer;
        this.objectMapper = objectMapper;
        this.restTemplate = restTemplate;
        this.httpPort = httpPort;
        this.smtpPort = smtpPort;

        this.mailHogUrl = "http://" + mailhogContainer.getHost() + ":" + mailhogContainer.getMappedPort(httpPort);
        this.emailsUrlV2 = mailHogUrl + "/api/v2/messages";
        this.emailsUrlV1 = mailHogUrl + "/api/v1/messages";
    }

    public JsonNode getEmails() throws JsonProcessingException {
        ResponseEntity<String> response = restTemplate.getForEntity(emailsUrlV2, String.class);

        return objectMapper.readTree(response.getBody());
    }

    public JsonNode getAndRemoveEmails() throws JsonProcessingException {
        JsonNode emails = getEmails();
        removeAllEmails();

        return emails;
    }

    public JsonNode waitForMessagesAndRemove(Duration timeout) throws InterruptedException {
        Instant start = Instant.now();
        while (Duration.between(start, Instant.now()).compareTo(timeout) < 0) {
            JsonNode emails = null;
            try {
                emails = getEmails();
                if (Integer.parseInt(emails.get("count").asText()) > 0) {
                    removeAllEmails();
                    return emails;
                }
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }

            Thread.sleep(500);
        }

        return null;
    }

    public void removeAllEmails() {
        this.restTemplate.delete(emailsUrlV1);
    }
}
