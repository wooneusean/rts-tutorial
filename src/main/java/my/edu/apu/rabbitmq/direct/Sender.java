package my.edu.apu.rabbitmq.direct;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

public class Sender {
    public static void main(String[] args) throws IOException, TimeoutException {
        String exchangeName = "directExchange";
        ConnectionFactory cf = new ConnectionFactory();
        try (Connection con = cf.newConnection()) {
            Channel ch = con.createChannel();
            ch.exchangeDeclare(exchangeName, BuiltinExchangeType.DIRECT);
            String msg;
            String key;
            Scanner scn = new Scanner(System.in);
            while (true) {
                System.out.println("Enter a key:");
                key = scn.nextLine();
                System.out.println("Enter a message:");
                msg = scn.nextLine();
                if (msg.isBlank() || key.isBlank()) {
                    break;
                }

                ch.basicPublish(exchangeName, key, false, null, msg.getBytes());
            }
        }

    }
}
