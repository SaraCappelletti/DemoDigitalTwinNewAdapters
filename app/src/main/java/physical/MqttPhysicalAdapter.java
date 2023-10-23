package physical;

import physical.topic.incoming.DigitalTwinIncomingTopic;
import physical.topic.outgoing.DigitalTwinOutgoingTopic;
import it.wldt.adapter.physical.ConfigurablePhysicalAdapter;
import it.wldt.adapter.physical.event.PhysicalAssetActionWldtEvent;
import it.wldt.adapter.physical.event.PhysicalAssetEventWldtEvent;
import it.wldt.adapter.physical.event.PhysicalAssetPropertyWldtEvent;
import it.wldt.core.event.WldtEvent;
import it.wldt.exception.EventBusException;
import it.wldt.exception.PhysicalAdapterException;
import org.eclipse.paho.client.mqttv3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class MqttPhysicalAdapter extends ConfigurablePhysicalAdapter<MqttPhysicalAdapterConfiguration> {

    private static final Logger logger = LoggerFactory.getLogger(MqttPhysicalAdapter.class);

    private final IMqttClient mqttClient;
    public MqttPhysicalAdapter(String id, MqttPhysicalAdapterConfiguration configuration) throws MqttException {
        super(id, configuration);
        this.mqttClient = new MqttClient(getConfiguration().getBrokerConnectionString(),
                getConfiguration().getClientId(),
                getConfiguration().getPersistence());
    }

    @Override
    public void onIncomingPhysicalAction(PhysicalAssetActionWldtEvent<?> physicalActionEvent) {
        logger.info("MQTT Physical Adapter received action event: {}", physicalActionEvent);
        getConfiguration()
                .getOutgoingTopicByActionKey(physicalActionEvent.getActionKey())
                .ifPresent(t -> publishOnTopic(t, t.applyPublishFunction(physicalActionEvent)));
    }

    @Override
    public void onAdapterStart() {
        try {
            connectToMqttBroker();
            getConfiguration().getIncomingTopics().forEach(this::subscribeClientToDigitalTwinIncomingTopic);
            logger.info("MQTT Physical Adapter - MQTT client subscribed to incoming topics");
            notifyPhysicalAdapterBound(getConfiguration().getPhysicalAssetDescription());
        } catch (PhysicalAdapterException | EventBusException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAdapterStop() {
        try {
            mqttClient.disconnect();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private void publishOnTopic(DigitalTwinOutgoingTopic topic, String payload){
        try {
            MqttMessage msg = new MqttMessage(payload.getBytes());
            msg.setQos(topic.getQos());
            msg.setRetained(true);
            mqttClient.publish(topic.getTopic(), msg);
            logger.info("Physical Adapter - MQTT client published message: {} on topic: {}", payload, topic.getTopic());
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private void subscribeClientToDigitalTwinIncomingTopic(DigitalTwinIncomingTopic topic) {
        try {
            mqttClient.subscribe(topic.getTopic(), topic.getQos(), (t, msg) ->{
                List<? extends WldtEvent<?>> wldtEvents = topic.applySubscribeFunction(new String(msg.getPayload()));
                wldtEvents.forEach(e -> {
                    try {
                        if(e instanceof PhysicalAssetEventWldtEvent){
                            publishPhysicalAssetEventWldtEvent((PhysicalAssetEventWldtEvent<?>) e);
                        }else if(e instanceof PhysicalAssetPropertyWldtEvent){
                            publishPhysicalAssetPropertyWldtEvent((PhysicalAssetPropertyWldtEvent<?>) e);
                        }
                    } catch (EventBusException ex) {
                        ex.printStackTrace();
                    }
                });
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private void connectToMqttBroker(){
        try {
            mqttClient.connect(getConfiguration().getConnectOptions());
            logger.info("MQTT Physical Adapter - MQTT client connected to broker - clientId: {}", getConfiguration().getClientId());
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
