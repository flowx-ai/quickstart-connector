package ai.flowx.quickstart.connector.config;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class JaegerConstants {
    public static final String JAEGER_SPAN_PROCESS_MESSAGE = "process message";

    public static final String JAEGER_TAG_PROCESS_INSTANCE_UUID = "process instance uuid";

    public static final String JAEGER_LOG_PROCESS_MESSAGE = "process message log message";
}
