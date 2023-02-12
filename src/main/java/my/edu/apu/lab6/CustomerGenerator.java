package my.edu.apu.lab6;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CustomerGenerator {
    public static void main(String[] args) {
        while (true) {
            new Thread(new Customer()).start();
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
            }
        }
    }
}

class Customer implements Runnable {
    Random rand = new Random();
    String myExchange = "workerCustomerExchange";
    ConnectionFactory cf = new ConnectionFactory();

    @Override
    public void run() {
        String bindingKey = genKey();
        receiveMsg(bindingKey);
    }

    public void receiveMsg(String key) {
        try {
            Connection con = cf.newConnection(); //2
            Channel ch = con.createChannel(); //3
            ch.exchangeDeclare(myExchange, "topic");
            String qName = ch.queueDeclare().getQueue();
            ch.queueBind(qName, myExchange, key); //queue, the exchange, the key
            try {
                // 7. consume or retrieve the message
                ch.basicConsume(qName, true, (x, msg) -> {
                    String message = new String(msg.getBody(), "UTF-8");
                    System.out.println("CUSTOMER: received the following item: " + message);
                }, x -> {
                }); //queue, 2 lambda functions
            } catch (IOException ex) {
                Logger.getLogger(WorkerLogic.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (IOException ex) {
            Logger.getLogger(WorkerLogic.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TimeoutException ex) {
            Logger.getLogger(WorkerLogic.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String genKey() {
        String item;
        int no = rand.nextInt(4);
        switch (no) {
            case 0:
                item = "bun";
                break;
            case 1:
                item = "*";
                break;
            case 2:
                item = "bun.bun";
                break;
            default:
                item = "bun.cake";
                break;
        }
        return item;
    }
}