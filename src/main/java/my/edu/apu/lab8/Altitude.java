package my.edu.apu.lab8;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Altitude {
    public static int value = 10000;

    public static void main(String[] args) throws IOException, TimeoutException {
        new Thread(new AltitudeSender()).start();

        String exchangeName = "lab8";
        ConnectionFactory cf = new ConnectionFactory();
        Connection con = cf.newConnection();
        Channel ch = con.createChannel();
        ch.exchangeDeclare(exchangeName, BuiltinExchangeType.DIRECT);

        String queueName = ch.queueDeclare().getQueue();
        ch.queueBind(queueName, exchangeName, "alt");

        ch.basicConsume(queueName, true, (consumerTag, msg) -> {
            int newAltitude = Integer.parseInt(new String(msg.getBody(), StandardCharsets.UTF_8));
            System.out.println(consumerTag + " > Message received: " + newAltitude);
            Altitude.value = newAltitude;
        }, (consumerTag) -> {
            System.out.println(consumerTag + " > Message cancelled");
        });
        System.out.println("Receiver is listening on exchange " + exchangeName + " on queue " + queueName);
    }
}

class AltitudeSender implements Runnable {

    @Override
    public void run() {
        String exchangeName = "lab8";
        ConnectionFactory cf = new ConnectionFactory();
        try (Connection con = cf.newConnection()) {
            Channel ch = con.createChannel();
            ch.exchangeDeclare(exchangeName, BuiltinExchangeType.DIRECT);
            while (true) {
                Altitude.value += (Math.random() * 1000) - 500;

                ch.basicPublish(exchangeName, "fc", false, null, String.valueOf(Altitude.value).getBytes());

                TimeUnit.MILLISECONDS.sleep(500);
            }
        } catch (IOException | TimeoutException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}