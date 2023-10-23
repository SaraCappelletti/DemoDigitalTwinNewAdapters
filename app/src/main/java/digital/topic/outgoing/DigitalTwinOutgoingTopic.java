package digital.topic.outgoing;

import digital.topic.MqttQosLevel;
import digital.topic.MqttTopic;
import it.wldt.core.state.DigitalTwinStateEventNotification;
import it.wldt.core.state.DigitalTwinStateProperty;

public class DigitalTwinOutgoingTopic<T> extends MqttTopic {
    private final MqttPublishDigitalFunction<T> publishDigitalFunction;

    public DigitalTwinOutgoingTopic(String topic, MqttQosLevel qosLevel, MqttPublishDigitalFunction<T> publishDigitalFunction) {
        super(topic, qosLevel);
        this.publishDigitalFunction = publishDigitalFunction;
    }

    public String applyPublishFunction(DigitalTwinStateProperty<?> digitalTwinStateComponent){
        return publishDigitalFunction.apply((T) digitalTwinStateComponent);
    }

    public String applyPublishFunction(DigitalTwinStateEventNotification<?> digitalTwinStateComponent){
        return publishDigitalFunction.apply((T) digitalTwinStateComponent);
    }
}
