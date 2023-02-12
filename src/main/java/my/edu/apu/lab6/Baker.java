package my.edu.apu.lab6;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Baker {
    public static void main(String[] args) {
        ScheduledExecutorService bakery = Executors.newScheduledThreadPool(1);
        bakery.scheduleAtFixedRate(new BakerLogic(), 0, 5, TimeUnit.SECONDS);
    }
}

class BakerLogic implements Runnable {
    Random rand = new Random();
    String bakerFanOutExchange = "bakerFanOutExchange";
    ConnectionFactory cf = new ConnectionFactory();

    @Override
    public void run() {
        for (int i = 0; i < 12; i++) {
            String itemKey = genItem();
            sendMessage(itemKey);
            System.out.println("BAKER: the following item has been sent: " + itemKey);
            try {
                Thread.sleep(100);
            } catch (Exception ignored) {
            }
        }
    }

    public String genItem() {
        int no = rand.nextInt(4);
        return switch (no) {
            case 0 -> "bun";
            case 1 -> "cake";
            case 2 -> "bun.bun";
            default -> "bun.cake";
        };
    }

    public void sendMessage(String msg) {
        try (Connection con = cf.newConnection()) {
            Channel ch = con.createChannel();
            ch.exchangeDeclare(bakerFanOutExchange, BuiltinExchangeType.DIRECT); //name, type
            ch.basicPublish(bakerFanOutExchange, "", false, null, msg.getBytes());
        } catch (IOException | TimeoutException ex) {
            Logger.getLogger(BakerLogic.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}