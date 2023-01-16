package my.edu.apu.rabbitmq.basic;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

public class Receiver2 {
    public static void main(String[] args) throws IOException, TimeoutException {
        String queueName = "firstQueue";
        ConnectionFactory cf = new ConnectionFactory();
        Connection con = cf.newConnection();
        Channel ch = con.createChannel();
        ch.queueDeclare(queueName, false, false, false, null);
        ch.basicConsume(
                queueName,
                (consumerTag, msg) -> {
                    String received = new String(msg.getBody(), StandardCharsets.UTF_8);
                    System.out.println(consumerTag + " > Message received: " + received);
                },
                (consumerTag) -> {
                    System.out.println(consumerTag + " > Message cancelled");
                }
        );
        System.out.println("Receiver listening on queue + " + queueName);
    }
}
