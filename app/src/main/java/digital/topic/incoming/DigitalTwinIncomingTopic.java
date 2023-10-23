package digital.topic.incoming;

import it.wldt.adapter.digital.event.DigitalActionWldtEvent;
import digital.topic.MqttTopic;

public class DigitalTwinIncomingTopic extends MqttTopic {

    private final MqttSubscribeDigitalFunction subscribeDigitalFunction;

    public DigitalTwinIncomingTopic(String topic, MqttSubscribeDigitalFunction subscribeDigitalFunction) {
        super(topic);
        this.subscribeDigitalFunction = subscribeDigitalFunction;
    }

    public DigitalActionWldtEvent<?> applySubscribeFunction(String messagePayload) {
        return this.subscribeDigitalFunction.apply(messagePayload);
    }
}
