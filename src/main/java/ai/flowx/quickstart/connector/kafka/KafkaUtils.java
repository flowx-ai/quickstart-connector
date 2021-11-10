package ai.flowx.quickstart.connector.kafka;

import lombok.experimental.UtilityClass;
import org.apache.kafka.common.header.Headers;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class KafkaUtils {

    public static List<String> headersToList(Headers headers) {
        return Arrays.stream(headers.toArray())
                .map(header -> "\"" + header.key() + "\": \"" + (header.value() == null ? ""
                        : new String(header.value())) + "\"")
                .collect(Collectors.toList());
    }

}
