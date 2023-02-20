package my.edu.apu.lab8;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.util.concurrent.TimeoutException;

public class WingFlaps {
    public static void main(String[] args) throws IOException, TimeoutException {
        String exchangeName = "lab8";
        ConnectionFactory cf = new ConnectionFactory();
        Connection con = cf.newConnection();
        Channel ch = con.createChannel();
        ch.exchangeDeclare(exchangeName, BuiltinExchangeType.DIRECT);

        String queueName = ch.queueDeclare().getQueue();
        ch.queueBind(queueName, exchangeName, "flaps");

        ch.basicConsume(
                queueName,
                true,
                (consumerTag, msg) -> {
                    try (ByteArrayInputStream bis = new ByteArrayInputStream(msg.getBody())) {
                        ObjectInput in = new ObjectInputStream(bis);
                        WingFlapPackage wfp = (WingFlapPackage) in.readObject();
                        System.out.println(
                                consumerTag +
                                " > Message received: direction = " +
                                wfp.direction +
                                " alt = " +
                                wfp.altitude);

                        switch (wfp.direction) {
                            case -1 -> wfp.altitude -= 500;
                            case 1 -> wfp.altitude += 500;
                            default -> {
                            }
                        }

                        ch.basicPublish(exchangeName, "alt", false, null, String.valueOf(wfp.altitude).getBytes());
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                },
                (consumerTag) -> {
                    System.out.println(consumerTag + " > Message cancelled");
                }
        );
        System.out.println("Receiver is listening on exchange " + exchangeName + " on queue " + queueName);
    }
}
