package my.edu.apu.flightcontrol;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.math.BigInteger;
import java.util.concurrent.TimeoutException;

public class FlightControl {

    public static void main(String[] args) throws IOException, TimeoutException {
        String exchangeName = "flight_control";
        ConnectionFactory cf = new ConnectionFactory();
        Connection con = cf.newConnection();
        Channel ch = con.createChannel();
        ch.exchangeDeclare(exchangeName, BuiltinExchangeType.DIRECT);

        String queueName = ch.queueDeclare().getQueue();
        ch.queueBind(queueName, exchangeName, "control");

        ch.basicConsume(
                queueName,
                true,
                (consumerTag, msg) -> {
                    BigInteger received = new BigInteger(msg.getBody());
                    System.out.println(consumerTag + " > Message received: " + received);
                },
                (consumerTag) -> {
                    System.out.println(consumerTag + " > Message cancelled");
                }
        );
        System.out.println("Receiver is listening on exchange " + exchangeName + " on queue " + queueName);
    }
}
