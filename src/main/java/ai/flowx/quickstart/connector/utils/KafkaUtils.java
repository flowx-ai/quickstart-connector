package ai.flowx.quickstart.connector.utils;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.apache.kafka.common.header.Headers;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;


@Slf4j
@UtilityClass
public class KafkaUtils {

    // TODO make sure methods return only Optional or a default value instead of null or empty string in case of errors

    public static Long extractHeaderLong(Headers headers, String headerName) {
        return Optional.ofNullable(headers.lastHeader(headerName))
                .map(value -> {
                    try {
                        return Long.parseLong(Arrays.toString(value.value()));
                    } catch (Exception e) {
                        byte[] header = value.value();
                        return Long.parseLong(new String(header));
                    }
                })
                .orElse(null);

    }

    public static String extractHeaderString(Headers headers, String headerName) {
        return Optional.ofNullable(headers.lastHeader(headerName))
                .map(value -> new String(value.value()))
                .orElse("");
    }

    public static UUID extractHeaderUuid(Headers headers, String headerName) {
        String uuid = KafkaUtils.extractHeaderString(headers, headerName);
        if (StringUtils.isEmpty(uuid)) {
            return null;
        }
        try {
            return UUID.fromString(uuid);
        } catch (IllegalArgumentException e) {
            log.error("Error while converting uuid header: {}", uuid);
            return null;
        }

    }

    public static List<String> headersToList(Headers headers) {
        return Arrays.stream(headers.toArray())
                .map(header -> "\"" + header.key() + "\": \"" + (header.value() == null ? ""
                        : new String(header.value())) + "\"")
                .collect(Collectors.toList());
    }

}