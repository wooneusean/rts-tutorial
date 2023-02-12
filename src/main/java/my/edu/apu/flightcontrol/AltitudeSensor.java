package my.edu.apu.flightcontrol;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class AltitudeSensor implements Runnable {

    public static void main(String[] args) {
        ScheduledExecutorService es = Executors.newScheduledThreadPool(1);
        es.scheduleAtFixedRate(new AltitudeSensor(), 0, 1, TimeUnit.SECONDS);
    }

    @Override
    public void run() {
        String exchangeName = "flight_control";
        ConnectionFactory cf = new ConnectionFactory();
        try (Connection con = cf.newConnection()) {
            Channel ch = con.createChannel();
            ch.exchangeDeclare(exchangeName, BuiltinExchangeType.DIRECT);

            Random r = new Random();
            BigInteger i = BigInteger.valueOf(r.nextInt(100000));
            System.out.println("[Altitude Sensor] Sending altitude of " + i + " feet");
            ch.basicPublish(exchangeName, "control", false, null, i.toByteArray());
        } catch (IOException | TimeoutException e) {
            throw new RuntimeException(e);
        }
    }
}
