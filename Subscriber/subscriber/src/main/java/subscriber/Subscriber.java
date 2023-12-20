package subscriber;

import com.hivemq.client.mqtt.MqttGlobalPublishFilter;
import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt5.Mqtt5BlockingClient;
import com.hivemq.client.mqtt.mqtt5.Mqtt5BlockingClient.Mqtt5Publishes;
import com.hivemq.client.mqtt.mqtt5.Mqtt5Client;
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5Publish;
import com.hivemq.client.mqtt.mqtt5.message.subscribe.Mqtt5Subscribe;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
class Subscriber
{

    public static void main(String[] args) throws InterruptedException {

final String host = "30e8e768fd954309afa9e81a432a8802.s2.eu.hivemq.cloud"; // use your host-name, it should look like '<alphanumeric>.s2.eu.hivemq.cloud'
final String username = "nikola1"; // your credentials
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
        .send();





if(client.getState().isConnected())
{
	  client.toBlocking().subscribeWith().topicFilter("home/temperature1").qos(MqttQos.EXACTLY_ONCE).send();

	  try (Mqtt5Publishes publishes =  client.toBlocking().publishes(MqttGlobalPublishFilter.ALL)) {
		  
		   while(true) {
		  Mqtt5Publish publishMessage1 = publishes.receive();
		  
		    String topic = publishMessage1.getTopic().toString();
		    String payload = publishMessage1.getPayloadAsBytes().toString();
           	    
		    if(topic.equals("home/temperature1")) {
		    	int temperatureValue = extractTemperatureValue(payload);
	            System.out.print("+".repeat(temperatureValue));
	            System.out.println(" " + payload);
	            if(temperatureValue>38) 
	            	System.out.println("Mnogu visoka temperatura");
	            else System.out.println();
		    }
	  }
		}
	  
}
else
{
	  System.out.print("MQTT service connect failed");
}

}
    private static int extractTemperatureValue(String payload) {
        return Integer.parseInt(payload.replaceAll("[^0-9]", ""));
    }


    }
