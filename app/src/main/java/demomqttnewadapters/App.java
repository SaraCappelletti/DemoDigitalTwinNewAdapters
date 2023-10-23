package demomqttnewadapters;

import digital.MqttDigitalAdapter;
import digital.MqttDigitalAdapterConfiguration;
import digital.exception.MqttDigitalAdapterConfigurationException;
import it.wldt.core.engine.WldtEngine;
import it.wldt.exception.EventBusException;
import it.wldt.exception.ModelException;
import it.wldt.exception.WldtConfigurationException;
import it.wldt.exception.WldtRuntimeException;
import org.eclipse.paho.client.mqttv3.MqttException;
import physical.MqttPhysicalAdapter;
import physical.MqttPhysicalAdapterConfiguration;
import physical.exception.MqttPhysicalAdapterConfigurationException;

import java.io.IOException;

public class App {
    public static void main(String[] args) throws WldtConfigurationException, ModelException, WldtRuntimeException, EventBusException, MqttPhysicalAdapterConfigurationException, MqttException, MqttDigitalAdapterConfigurationException, IOException {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "error");

        WldtEngine dtEngine = new WldtEngine(new DefaultShadowingFunction(), "mqtt-device-digital-twin");
        dtEngine.addPhysicalAdapter(buildMqttPhysicalAdapter());
        dtEngine.addDigitalAdapter(buildMqttDigitalAdapter());

        dtEngine.startLifeCycle();
    }



    private static MqttPhysicalAdapter buildMqttPhysicalAdapter() throws MqttPhysicalAdapterConfigurationException, MqttException, IOException {
        String filepath = "src/main/resources/config_pa.yml";
        MqttPhysicalAdapterConfiguration configuration = MqttPhysicalAdapterConfiguration
                .builder("localhost", 1883, "mqtt.physical.adapter")
                .addPhysicalAssetPropertyAndTopic("intensity", 0, "sensor/intensity", Integer::parseInt)
                .build();
        return new MqttPhysicalAdapter("mqtt-device-pa", configuration);
    }

    private static MqttDigitalAdapter buildMqttDigitalAdapter() throws MqttException, MqttDigitalAdapterConfigurationException, IOException {
        //NB: In MqttDigitalAdapter topics for Property and Event are OutgoingTopics,
        // so each function takes as input something that has the same type as the body of the Event or Property
        String filepath = "src/main/resources/config_da.yml";
        MqttDigitalAdapterConfiguration configuration = MqttDigitalAdapterConfiguration
                .builder("localhost", 1883, "mqtt.digital.adapter")
                .addActionTopic("switch_off", "app/actions/switch-off", msg -> "OFF")
                .build();
        return new MqttDigitalAdapter("test-mqtt-da", configuration);
    }
}
