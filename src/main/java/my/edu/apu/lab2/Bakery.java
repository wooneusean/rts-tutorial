package my.edu.apu.lab2;

import java.util.concurrent.atomic.AtomicInteger;

public class Bakery {
    static AtomicInteger coolingRack = new AtomicInteger(0);
    static AtomicInteger shelf = new AtomicInteger(0);

    public static void main(String[] args) throws InterruptedException {
        new Thread(new Worker()).start();
        new Thread(new Baker()).start();
        while (true) {
            Thread.sleep(1000);
            if (shelf.get() > 0) {
                shelf.addAndGet(-2);
                System.out.println("Customer > Taking bread");
                System.out.println(coolingRack + "/" + shelf);
            }
        }
    }
}

class Worker implements Runnable {
    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(1000);
                if (Bakery.shelf.get() + 4 <= 10 && Bakery.coolingRack.get() >= 4) {
                    System.out.println("Worker > Putting 4 buns");
                    Bakery.shelf.addAndGet(4);
                    Bakery.coolingRack.addAndGet(-4);
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

class Baker implements Runnable {
    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(750);
                if (Bakery.coolingRack.get() + 12 <= 18) {
                    System.out.println("Baker > Baking 12 buns and putting into cooling rack");
                    Bakery.coolingRack.addAndGet(12);
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}