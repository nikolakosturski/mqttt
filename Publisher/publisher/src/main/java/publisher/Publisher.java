package publisher;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import com.hivemq.client.mqtt.mqtt5.Mqtt5Client;

public class Publisher {

    public static void main(String[] args) throws InterruptedException {
        final String host = "30e8e768fd954309afa9e81a432a8802.s2.eu.hivemq.cloud"; // use your host-name, it should look like '<alphanumeric>.s2.eu.hivemq.cloud'
        final String username = "nikola2"; // your credentials
        final String password = "Milo1234.";
   
        // 1. create the client
        final Mqtt5Client client = Mqtt5Client.builder() 
               // .identifier("sensor-" + getMacAddress()) // use a unique identifier
                .serverHost(host) 
                .automaticReconnectWithDefaultConfig() // the client automatically reconnects
                .serverPort(8883) // this is the port of your cluster, for mqtt it is the default port 8883
                .sslWithDefaultConfig() // establish a secured connection to HiveMQ Cloud using TLS
                .build();
                

        // 2. connect the client
        client.toBlocking().connectWith()
                .simpleAuth() // using authentication, which is required for a secure connection
                .username(username) // use the username and password you just created
                .password(password.getBytes(StandardCharsets.UTF_8))
                .applySimpleAuth()
                .willPublish() // the last message, before the client disconnects
                    .topic("home/temperature1")
                    .payload("sensor gone".getBytes())
                    .applyWillPublish()
                .send();

        int i=0;
        // 3. simulate periodic publishing of sensor data
        while (i<6) {
            client.toBlocking().publishWith()
                    .topic("home/temperature1")
                    .payload(getTemperature())
                    .send();

            TimeUnit.MILLISECONDS.sleep(500);
            
            i++;
        }
    }

    private static byte[] getTemperature() {
        // simulate a temperature sensor with values between 12°C and 50°C
        final int temperature = ThreadLocalRandom.current().nextInt(1, 13);
        return (temperature + "°C").getBytes(StandardCharsets.UTF_8);
    }
}
