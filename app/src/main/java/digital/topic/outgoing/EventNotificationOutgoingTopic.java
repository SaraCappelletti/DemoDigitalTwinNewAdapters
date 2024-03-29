package digital.topic.outgoing;

import digital.topic.MqttQosLevel;
import it.wldt.core.state.DigitalTwinStateEventNotification;

import java.util.function.Function;

public class EventNotificationOutgoingTopic<T> extends DigitalTwinOutgoingTopic<DigitalTwinStateEventNotification<T>>{
    public EventNotificationOutgoingTopic(String topic, MqttQosLevel qosLevel, Function<T, String> notificationBodyToString) {
        super(topic, qosLevel, eventNotification -> notificationBodyToString.apply(eventNotification.getBody()));
    }
}
