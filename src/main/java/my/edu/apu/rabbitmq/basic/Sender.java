package my.edu.apu.rabbitmq.basic;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Sender {
    public static void main(String[] args) throws IOException, TimeoutException {
        String queueName = "firstQueue";
        String msg = "Hello World";
        ConnectionFactory cf = new ConnectionFactory();
        try (Connection con = cf.newConnection()) {
            Channel ch = con.createChannel();
            ch.queueDeclare(queueName, false, false, false, null);
            ch.basicPublish("", queueName, null, msg.getBytes());
            System.out.println("Sender has published message.");
        }
    }
}
