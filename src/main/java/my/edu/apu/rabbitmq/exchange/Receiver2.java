package my.edu.apu.rabbitmq.exchange;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

public class Receiver2 {
    public static void main(String[] args) throws IOException, TimeoutException {
        String exchangeName = "firstExchange";
        ConnectionFactory cf = new ConnectionFactory();
        Connection con = cf.newConnection();
        Channel ch = con.createChannel();
        ch.exchangeDeclare(exchangeName, BuiltinExchangeType.FANOUT);

        String queueName = ch.queueDeclare().getQueue();
        ch.queueBind(queueName, exchangeName, "");

        ch.basicConsume(
                queueName,
                true,
                (consumerTag, msg) -> {
                    String received = new String(msg.getBody(), StandardCharsets.UTF_8);
                    System.out.println(consumerTag + " > Message received: " + received);
                },
                (consumerTag) -> {
                    System.out.println(consumerTag + " > Message cancelled");
                }
        );
        System.out.println("Receiver is listening on exchange " + exchangeName + " on queue " + queueName);
    }
}
