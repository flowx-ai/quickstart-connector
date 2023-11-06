package ai.flowx.commons.kafka;

import lombok.experimental.UtilityClass;

@UtilityClass
public class KafkaProcessHeaders {
    public static final String HEADER_PROCESS_INSTANCE_ID = "processInstanceId";
    public static final String HEADER_PROCESS_INSTANCE_UUID = "processInstanceUuid";
    public static final String HEADER_PROCESS_DEFINITION_UUID = "processDefinitionUuid";

    public static final String HEADER_PROCESS_START_DATE = "processStartDate";
    public static final String HEADER_DESTINATION_ID = "destinationId";
    public static final String HEADER_CALLBACKS_FOR_ACTION = "callbacksForAction";
}