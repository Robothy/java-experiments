/**
 * wait() 使线程进入 WAITING 状态，而线程进入 synchronized 时未获取到锁的情况下会进入 BLOCKED 状态。
 *
 * WAITING 状态被中断之后会立即抛出 InterruptedException，从 wait() 方法中返回；而 BLOCKED 状态仅仅是将
 * 中断标志设置为 true，不会立即响应。
 *
 */
public class WaitAndSynchronizedTest {

    private static Object mutex = new Object();

    private static void f()  {
        synchronized (mutex){
            System.out.println(Thread.currentThread().getName() + " --> Interrupted: " + Thread.currentThread().isInterrupted());
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(WaitAndSynchronizedTest::f, "t1");
        Thread t2 = new Thread(WaitAndSynchronizedTest::f, "t2");
        t1.start();
        Thread.sleep(1000); // 确保 t1 启动成功并且拿到了锁
        t2.start(); // 启动 t2，此时因为 t1 没有执行完，所以 t2 阻塞
        t2.interrupt(); // 中断 t2，此时 t2 的中断标志设置未了 true,但是不会立即响应，
        // 直到 t1 释放锁之后，t2 能够查询到中断标志，并且 t2 在执行后续的 sleep 方法时会立即抛出 InterruptedException
        //
        // 不过，如果将 Thread.currentThread().isInterrupted() 替换成静态方法 Thread.interrupted()，将中断标志清除，
        // 则后面执行 sleep() 方法时不会抛出异常。
    }

}
