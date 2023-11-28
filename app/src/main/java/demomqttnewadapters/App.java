package demomqttnewadapters;

import digital.MqttDigitalAdapter;
import digital.MqttDigitalAdapterConfiguration;
import digital.exception.MqttDigitalAdapterConfigurationException;
import it.wldt.core.engine.WldtEngine;
import it.wldt.exception.EventBusException;
import it.wldt.exception.ModelException;
import it.wldt.exception.WldtConfigurationException;
import it.wldt.exception.WldtRuntimeException;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import physical.MqttPhysicalAdapter;
import physical.MqttPhysicalAdapterConfiguration;
import physical.exception.MqttPhysicalAdapterConfigurationException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class App {
    public static void main(String[] args) throws WldtConfigurationException, ModelException, WldtRuntimeException, EventBusException, MqttPhysicalAdapterConfigurationException, MqttException, MqttDigitalAdapterConfigurationException, IOException {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "error");

        WldtEngine dtEngine = new WldtEngine(new DefaultShadowingFunction(), "mqtt-device-digital-twin");
        dtEngine.addPhysicalAdapter(buildMqttPhysicalAdapter("/app/app/config/config_pa.yml"));
        dtEngine.addDigitalAdapter(buildMqttDigitalAdapter("/app/app/config/config_da.yml"));

        /*App app = new App();
        String fileName = "resources/config_pa.yml";

        System.out.println("getResourceAsStream : " + fileName);
        InputStream is = app.getFileFromResourceAsStream(fileName);
        printInputStream(is);*/


        dtEngine.startLifeCycle();
    }

    private InputStream getFileFromResourceAsStream(String fileName) {

        // The class loader that loaded the class
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(fileName);

        // the stream holding the file content
        if (inputStream == null) {
            throw new IllegalArgumentException("file not found! " + fileName);
        } else {
            return inputStream;
        }

    }

    private static void printInputStream(InputStream is) {

        try (InputStreamReader streamReader =
                     new InputStreamReader(is, StandardCharsets.UTF_8);
             BufferedReader reader = new BufferedReader(streamReader)) {

            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }



    private static MqttPhysicalAdapter buildMqttPhysicalAdapter(String filepath) throws MqttPhysicalAdapterConfigurationException, MqttException, IOException {
        MqttPhysicalAdapterConfiguration configuration = MqttPhysicalAdapterConfiguration
                .builder(filepath)//"host.docker.internal", 1883)
                .readFromConfig()
                //.addPhysicalAssetPropertyAndTopic("intensity", 0, "sensor/intensity", Integer::parseInt)
                //.addPhysicalAssetActionAndTopic("switch-off", "sensor.actuation", "text/plain", "sensor/actions/switch", actionBody -> "switch" + actionBody)
                .build();
        return new MqttPhysicalAdapter("mqtt-device-pa", configuration);
    }

    private static MqttDigitalAdapter buildMqttDigitalAdapter(String filepath) throws MqttException, MqttDigitalAdapterConfigurationException, IOException {
        //NB: In MqttDigitalAdapter topics for Property and Event are OutgoingTopics,
        // so each function takes as input something that has the same type as the body of the Event or Property
        MqttDigitalAdapterConfiguration configuration = MqttDigitalAdapterConfiguration
                .builder(filepath)//"host.docker.internal", 1883)
                .readFromConfig()
                //.addActionTopic("switch_off", "app/actions/switch-off", msg -> "OFF")
                //.addActionTopic("switch_on", "app/actions/switch-on", msg -> "ON")
                .build();
        return new MqttDigitalAdapter("test-mqtt-da", configuration);
    }
}
