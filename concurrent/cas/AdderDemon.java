import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class AdderDemon {

    public static void main(String[] args) throws InterruptedException {

        Adder[][] adders = new Adder[][] {
                {new SimpleAdder(), new SyncAdder(), new AtomicAdder()},
                {new SimpleAdder(), new AtomicAdder(), new SyncAdder()},
                {new SyncAdder(), new SimpleAdder(), new AtomicAdder()},
                {new SyncAdder(), new AtomicAdder(), new SimpleAdder()},
                {new AtomicAdder(), new SyncAdder(), new SimpleAdder()},
                {new AtomicAdder(), new SimpleAdder(), new SyncAdder()}
        };

        int repeatTimes = 100;
        int threadSize = 10;
        TreeMap<String, Long> due = new TreeMap<>();
        System.out.println("AtomicAdder; SimpleAdder; SyncAdder");
        for(int x=0; x<repeatTimes; x++){
            for(Adder[] adderGroup : adders){
                for(int i=0; i<adderGroup.length; i++){
                    long begin = System.currentTimeMillis();
                    Thread[] threads = new Thread[threadSize];
                    for(int j=0; j<threadSize; j++){
                        int finalI = i;
                        threads[j] = new Thread(()->{
                            for(int k=0; k<1000000; k++){
                                adderGroup[finalI].increment(k);
                            }
                        });
                        threads[j].start();
                    }

                    for (Thread thread : threads){
                        thread.join();
                    }
                    long end = System.currentTimeMillis();
                    due.put(adderGroup[i].getClass().getSimpleName(), end-begin);
                }
                System.out.println(due.values().stream()
                        .map(Object::toString)
                        .collect(Collectors.joining("; ")));
            }
        }
    }

}

/**
 * Adder 接口，用来记录增加的数值，可多线程访问。
 */
interface Adder{
    void increment(long val);
    long get();
}

/**
 * 普通实现方式，存在线程安全问题，多线程访问时将导致结果不正确。
 */
class SimpleAdder implements Adder {

    private long sum;

    @Override
    public void increment(long val) {
        sum += val;
    }

    @Override
    public long get() {
        return sum;
    }
}

/**
 * 使用 synchronize 实现，线程安全。
 * synchronize 本身就可以保证可见性，所以不需要使用 volatile 修饰属性。
 *
 * 不过 synchronize 在每次更新 sum 之前需要先获得锁，未获取到锁就进入阻塞状态。
 * 进入阻塞状态和从阻塞中恢复都需要内核来进行操作，开销很大。
 */
class SyncAdder implements Adder{

    private long sum;

    @Override
    public synchronized void increment(long val) {
        sum += val;
    }

    @Override
    public long get() {
        return sum;
    }
}

/**
 * 使用 CAS + volatile。CAS (Compare And Swap) 不需要进行阻塞，完全在用户态完成工作，效率高。
 * 而 volatile 保证了内存可见性。
 *
 * CAS 操作需要使用到 Unsafe 里面的 compareAndSwapLong(Object obj, long offset, long expect, long update) 方法。其中，
 *      第 1 个参数 obj 表示要操作的对象
 *      第 2 个参数 offset 表示 expect 在 obj 内部的地址偏移量
 *      第 3 个参数 expect 表示期望的要替换的属性的值
 *      第 4 个参数 update 表示要替换的新的值。
 * 改方法返回 true 表示替换成功了，返回 false 表示替换未成功。使用该方法时一般使用一个循环来进行操作。类似的方法还有两个
 *      compareAndSwapInt
 *      compareAndSwapObject
 */
class AtomicAdder implements Adder{

    private AtomicLong sum = new AtomicLong();

    @Override
    public void increment(long val) {
        long expect, update;
        do{
            expect = sum.get();
            update = expect + val;
        }while(!sum.compareAndSet(expect, update));
    }

    @Override
    public long get() {
        return sum.get();
    }
}



