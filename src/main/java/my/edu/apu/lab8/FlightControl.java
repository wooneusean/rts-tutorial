package my.edu.apu.lab8;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

public class FlightControl {
    public static void main(String[] args) throws IOException, TimeoutException {
        String exchangeName = "lab8";
        ConnectionFactory cf = new ConnectionFactory();
        Connection con = cf.newConnection();
        Channel ch = con.createChannel();
        ch.exchangeDeclare(exchangeName, BuiltinExchangeType.DIRECT);

        String queueName = ch.queueDeclare().getQueue();
        ch.queueBind(queueName, exchangeName, "fc");

        ch.basicConsume(queueName, true, (consumerTag, msg) -> {
            int altitude = Integer.parseInt(new String(msg.getBody(), StandardCharsets.UTF_8));
            System.out.println(consumerTag + " > Message received: " + altitude);
            int direction = 0;
            if (altitude < 10000) {
                // too low
                direction = 1;
            } else if (altitude > 10000) {
                // too high
                direction = -1;
            }
            WingFlapPackage wfp = new WingFlapPackage(direction, altitude);
            try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
                 ObjectOutputStream out = new ObjectOutputStream(bos)) {
                out.writeObject(wfp);
                out.flush();
                ch.basicPublish(exchangeName, "flaps", false, null, bos.toByteArray());
            }
        }, (consumerTag) -> {
            System.out.println(consumerTag + " > Message cancelled");
        });
        System.out.println("Receiver is listening on exchange " + exchangeName + " on queue " + queueName);
    }
}
