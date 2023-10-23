package demomqttnewadapters;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class MqttMessageSender {
    public static void main(String[] args) {
        String broker = "tcp://localhost:1883"; // Indirizzo del tuo broker MQTT
        String clientId = "MqttMessageSender";
        MemoryPersistence persistence = new MemoryPersistence();

        try {
            MqttClient client = new MqttClient(broker, clientId, persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);

            System.out.println("Connettendosi al broker: " + broker);
            client.connect(connOpts);
            System.out.println("Connesso");

            //mosquitto_pub -t "device/switch" -m "{\"value\": \"ON\"}" -h localhost -p 1883

            String topic = "device/switch"; // Il topic a cui invierai il messaggio
            String content = "{\"value\": \"ON\"}"; // Contenuto del messaggio JSON
            int qos = 0; // Quality of Service (0 - At most once)
            MqttMessage message = new MqttMessage(content.getBytes());
            message.setQos(qos);

            System.out.println("Invio messaggio: " + message);
            client.publish(topic, message);
            System.out.println("Messaggio inviato");

            client.disconnect();
            System.out.println("Disconnesso");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}