import java.util.concurrent.locks.AbstractQueuedSynchronizer;

/**
 * 基于 AQS 模拟 CountDownLatch 的实现。
 *
 * 定义若干个线程，直到这若干个线程都已经执行了 countDown() 方法之后，主线程才继续往下走。
 * 现象是：主线成打印的 "GO!" 总是在最后，其它线程打印的内容顺序不确定。
 *
 */
public class SimpleCountDownLatchTest {

    public static void main(String[] args) {

        int threadSize = 10;
        SimpleCountDownLatch latch = new SimpleCountDownLatch(threadSize);

        for(int i=0; i<threadSize; i++){
            new Thread(()-> {
                System.out.printf("NO. %d Ready. \n", Thread.currentThread().getId());
                latch.countDown();
            }).start();
        }

        latch.await();
        System.out.println("GO!");
    }

}

class SimpleCountDownLatch {

    private static class Sync extends AbstractQueuedSynchronizer {

        Sync(int count) {
            super.setState(count);
        }

        /**
         * 表示有没有获取到共享锁，如果获取到了，则返回整数或 0 ，此时线程不阻塞；如果没有获取到，则返回负数，此时线程阻塞。
         *
         * @return 负数 - 表示没有获得锁；0或正数 - 表示获取到了锁
         *
         */
        @Override
        protected int tryAcquireShared(int arg) {
            return -getState();
        }

        /**
         *
         * @return true - 表示要唤醒因为 acquire 失败的线程；false - 则不进行唤醒操作
         */
        @Override
        protected boolean tryReleaseShared(int arg) {
            for(;;){
                int expect = getState();
                int update = expect - arg;
                if(compareAndSetState(expect, update)){
                    return update == 0;
                }
            }
        }

    }

    private final Sync sync;

    public SimpleCountDownLatch(int count) {
        this.sync = new Sync(count);
    }

    /**
     * 每调用一次 countDown，释放 1 个资源
     */
    void countDown() {
        sync.releaseShared(1);
    }

    /**
     * await 可以抽象为获取锁，只有当资源数目为 0 时，获取锁才能够成功
     */
    void await() {
        sync.acquireShared(1);
    }

}

