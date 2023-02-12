package my.edu.apu.lab6;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Worker {
    public static void main(String[] args) {
        ScheduledExecutorService bakery = Executors.newScheduledThreadPool(1);
        bakery.scheduleAtFixedRate(new WorkerLogic(), 0, 1, TimeUnit.SECONDS);
    }
}

class WorkerLogic implements Runnable {
    String myExchange = "bakerFanoutExchange";
    String myExchange2 = "workerCustomerExchange";
    ConnectionFactory cf = new ConnectionFactory();

    @Override
    public void run() {
        for (int i = 0; i < 4; i++) {
            retrieveMsg();
            try {
                Thread.sleep(100);
            } catch (Exception e) {
            }
        }
    }

    public void retrieveMsg() {
        try {
            Connection con = cf.newConnection(); //2
            Channel ch = con.createChannel(); //3
            //ch.queueDeclare(qName, false, false, false, null); //4
            ch.exchangeDeclare(myExchange, "direct");
            // 5. get queue name
            String qName = ch.queueDeclare().getQueue();
            //6. bind queue, exchange, any key
            ch.queueBind(qName, myExchange, ""); //queue, the exchange, the key
            try {
                // 7. consume or retrieve the message
                ch.basicConsume(qName, true, (x, msg) -> {
                    String message = new String(msg.getBody(), "UTF-8");
                    System.out.println("WORKER: received key from baker: " + message);
                    sendMsg(message);
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

    public void sendMsg(String msg) {
        try (Connection con = cf.newConnection()) {
            Channel chan = con.createChannel();
            chan.exchangeDeclare(myExchange2, "topic"); //name, type
            chan.basicPublish(myExchange2, msg, false, null, msg.getBytes());
            System.out.println("WORKER: items sent to customers");
        } catch (IOException | TimeoutException ex) {
            Logger.getLogger(BakerLogic.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}