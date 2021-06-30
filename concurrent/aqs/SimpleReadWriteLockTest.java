import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

public class SimpleReadWriteLockTest {

    public static void main(String[] args) {
        SimpleReadWriteLock simpleReadWriteLock = new SimpleReadWriteLock();
        SimpleReadWriteLock.ReadLock readLock = simpleReadWriteLock.readLock();
        SimpleReadWriteLock.WriteLock writeLock = simpleReadWriteLock.writeLock();

        // 5 个线程可以同时持有写锁
        for (int i = 0; i < 5; i++) {
            new Thread(() -> {
                readLock.lock();
                System.out.printf("%s %s got read lock.\n", now(), Thread.currentThread().getName());
                sleep(1000);
                readLock.unlock();
            }).start();
        }

        // 5 个线程只有 1 个线程能够有持有读锁，打印内容间隔 1s 打印 1 次
        for (int i = 0; i < 5; i++) {
            new Thread(()->{
                writeLock.lock();
                System.out.printf("%s %s got write lock.\n", now(), Thread.currentThread().getName());
                sleep(1000);
                writeLock.unlock();
            }).start();
        }

    }

    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static String now() {
        return new SimpleDateFormat("HH:mm:ss:SSS")
                .format(new Date(System.currentTimeMillis()));
    }

}

/**
 * 读写锁，可以多个线程同时读；只能有一个线程执行写
 */
class SimpleReadWriteLock {

    private static class Sync extends AbstractQueuedSynchronizer {

        /**
         * 获得写锁，排他模式，只有当 state 等于 0 的时候才能获得，然后将 state 置为 -1.
         * 获得锁时考虑多个线程争抢，所以应该用 CAS 更新 state 状态
         */
        @Override
        protected boolean tryAcquire(int arg) {
            int expect = getState();
            if(expect==-1) throw new IllegalMonitorStateException();
            int update = -1;
            return expect == 0 && compareAndSetState(expect, update);
        }

        /**
         * 释放写锁，将 state 置为 0，考虑到释放写锁时只有一个线程，所以不需要 CAS
         */
        @Override
        protected boolean tryRelease(int arg) {
            int state = getState();
            if (-1 != state) throw new IllegalMonitorStateException();
            setState(0);
            return true;
        }

        /**
         * 获得读锁，读锁可以由多个线程获得，所以应该用共享模式。每获得一次读锁，state 值 +1
         */
        @Override
        protected int tryAcquireShared(int arg) {
            for (; ; ) {
                int expect = getState();
                if (expect < 0) return -1;
                int update = expect + 1;
                if (compareAndSetState(expect, update)) return 1;
            }
        }

        @Override
        protected boolean tryReleaseShared(int arg) {
            for (; ; ) {
                int expect = getState();
                if (expect <= 0) throw new IllegalMonitorStateException();
                int update = expect - 1;
                if (compareAndSetState(expect, update)) return true;
            }
        }
    }

    static class ReadLock {

        private Sync sync;

        ReadLock(Sync sync) {
            this.sync = sync;
        }

        void lock() {
            sync.acquireShared(-1);
        }

        void unlock() {
            sync.releaseShared(1);
        }
    }

    static class WriteLock {

        private Sync sync;

        WriteLock(Sync sync) {
            this.sync = sync;
        }

        void lock() {
            sync.acquire(-1);
        }

        void unlock() {
            sync.release(-1);
        }

    }

    private ReadLock readLock;

    private WriteLock writeLock;

    public SimpleReadWriteLock() {
        Sync sync = new Sync();
        this.readLock = new ReadLock(sync);
        this.writeLock = new WriteLock(sync);
    }

    ReadLock readLock() {
        return readLock;
    }

    WriteLock writeLock() {
        return writeLock;
    }

}
