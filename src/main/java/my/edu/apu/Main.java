package my.edu.apu;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Main {
    public static void main(String[] args) {
        ExecutorService factory = Executors.newCachedThreadPool();

        while (true) {
            Can can = new Can();
            try {
                Future<Can> filledCan = factory.submit(new FillerLogic(can));
                Future<Can> sealedCan = factory.submit(new SealerLogic(filledCan.get()));
                Future<Can> labelledCan = factory.submit(new LabelerLogic(sealedCan.get()));
                System.out.println("Can #" + labelledCan.get().canId + " has been filled, sealed and labelled.");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}

class FillerLogic implements Callable<Can> {
    private final Can can;

    public FillerLogic(Can can) {
        this.can = can;
    }

    @Override
    public Can call() throws Exception {
        System.out.println("Filling Can #" + can.canId);
        Thread.sleep(500);
        this.can.setFilled(true);
        return this.can;
    }
}

class SealerLogic implements Callable<Can> {
    private final Can can;

    public SealerLogic(Can can) {
        this.can = can;
    }

    @Override
    public Can call() throws Exception {
        System.out.println("Sealing Can #" + can.canId);
        Thread.sleep(500);
        this.can.setSealed(true);
        return this.can;
    }
}

class LabelerLogic implements Callable<Can> {

    private final Can can;

    public LabelerLogic(Can can) {
        this.can = can;
    }

    @Override
    public Can call() throws Exception {
        System.out.println("Labelling Can #" + can.canId);
        Thread.sleep(500);
        this.can.setLabeled(true);
        return this.can;
    }
}

class Can {
    static int totalCans = 0;
    boolean filled = false;
    boolean sealed = false;
    boolean labeled = false;

    int canId;

    public Can() {
        Can.totalCans++;
        canId = Can.totalCans;
    }

    public boolean isFilled() {
        return filled;
    }

    public void setFilled(boolean filled) {
        this.filled = filled;
    }

    public boolean isSealed() {
        return sealed;
    }

    public void setSealed(boolean sealed) {
        this.sealed = sealed;
    }

    public boolean isLabeled() {
        return labeled;
    }

    public void setLabeled(boolean labeled) {
        this.labeled = labeled;
    }
}