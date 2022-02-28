import java.util.concurrent.atomic.AtomicInteger;

public class AtomicIntegerDemon {

    public static void main(String[] args){
        int count = 10;
        ShutdownLatch latch = new ShutdownLatch(count);
        AtomicInteger sum = new AtomicInteger(0);
        for(int i=0; i<10; i++){
            new Counter(latch, sum).start();
        }

        System.out.println(sum.get());
    }

}

class Counter extends Thread {

    private final ShutdownLatch latch;

    private final AtomicInteger atomicInteger;

    public Counter(ShutdownLatch latch, AtomicInteger atomicInteger){
        this.latch = latch;
        this.atomicInteger = atomicInteger;
    }

    @Override
    public void run(){
        for(int i=0; i<100; i++){
            atomicInteger.incrementAndGet();
        }
        try {
            latch.shutdown();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

class ShutdownLatch {

    private int count;

    public ShutdownLatch(int count){
        this.count = count;
    }

    public synchronized void shutdown() throws InterruptedException {
        this.count--;
        if(this.count == 0) this.notifyAll();
        while (this.count != 0) this.wait();
    }

}