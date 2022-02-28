import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

/**
 * 测试 TwinsLock，控制台打印的内容的时间戳将成对出现。
 */
public class TwinsLockTest {

    public static void main(String[] args) {

        int threadCount = 10;

        TwinsLock lock = new TwinsLock();

        for (int i = 0; i < threadCount; i++) {
            new Thread(() -> {
                lock.lock();
                try{
                    System.out.printf("%s  %s  started.\n",
                            new SimpleDateFormat("HH:mm:ss:SSS").format(new Date(System.currentTimeMillis())),
                            Thread.currentThread().getName());
                    Thread.sleep(1000);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }finally {
                    lock.unlock();
                }
            }).start();
        }

    }

}

/**
 * TwinsLock 表示一种锁，每次可以有两个线程获取。
 * <p>
 * 因为有 2 个线程，所以实现的时候应该用共享模式；同时，用 state 表示锁的数量。
 */
class TwinsLock {

    private static class Sync extends AbstractQueuedSynchronizer {

        public Sync() {
            setState(2);
        }

        /**
         * 返回值大于等于 0 表示未获取到锁，用 state 表示锁的数量，线程每获取一次，数量 -1.
         * 由于存在多个线程去获取，所以需要用 CAS。
         */
        @Override
        protected int tryAcquireShared(int arg) {
            for (; ; ) {
                int expect = getState();
                int update = expect - 1;
                if(expect <= 0) return -1;
                if(compareAndSetState(expect, update)) return 1;
            }
        }

        /**
         * 返回值为 true 表示释放锁成功。每释放 1 次，state 的值 +1 。
         * 由于可能存在多个线程同时释放，所以得用 CAS 操作
         */
        @Override
        protected boolean tryReleaseShared(int arg) {
            for (; ; ) {
                int expect = getState();
                int update = expect + 1;
                if(expect == 2) throw new IllegalMonitorStateException();
                if (compareAndSetState(expect, update)) {
                    return true;
                }
            }
        }
    }

    Sync sync;

    public TwinsLock() {
        this.sync = new Sync();
    }

    void lock() {
        sync.acquireShared(0);
    }

    void unlock() {
        sync.releaseShared(0);
    }

}
