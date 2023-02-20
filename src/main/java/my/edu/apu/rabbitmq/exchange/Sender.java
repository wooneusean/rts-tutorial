package my.edu.apu.rabbitmq.exchange;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

public class Sender {
    public static void main(String[] args) throws IOException, TimeoutException {
        String exchangeName = "firstExchange";
        ConnectionFactory cf = new ConnectionFactory();
        try (Connection con = cf.newConnection()) {
            Channel ch = con.createChannel();
            ch.exchangeDeclare(exchangeName, BuiltinExchangeType.FANOUT);

            String msg;
            Scanner scn = new Scanner(System.in);

            while (true) {
                System.out.println("Enter a message:");
                msg = scn.nextLine();
                if (msg.isBlank()) {
                    break;
                }

                ch.basicPublish(exchangeName, "", false, null, msg.getBytes());
            }
        }

    }
}
