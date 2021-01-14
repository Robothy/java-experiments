import java.util.concurrent.atomic.AtomicInteger;

/**
 * 与 synchronized 锁相比， AtomicInteger 中的 CAS 更新是乐观的。
 *
 * synchronized 假定更新回产生冲突，所以在更新前先获得锁，再去更新；如果获得失败，则阻塞。
 * 而 CAS 更新假定更新不会冲突，先去更新，然后再检查更新结果；如果有冲突，则继续重试，直到成功为止。不需要阻塞，但是消耗 CPU 资源。
 *
 * 不过可以利用 CAS 的原子特性，实现一把悲观的，阻塞的锁。
 */
public class AtomicIntegerBlockingLock {

    private AtomicInteger atomicInteger;

    public void lock(){
        // 一直等待，直到成功设置为 1，表示拿到了锁
        while (!atomicInteger.compareAndSet(0, 1)) {
            Thread.yield();
        }
    }

    public void unlock(){
        // 将锁标志设置为 0
        atomicInteger.compareAndSet(1, 0);
    }
}
