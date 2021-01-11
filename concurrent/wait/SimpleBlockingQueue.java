import java.util.ArrayDeque;
import java.util.Queue;

/**
 * 基于 wait 和 notify 实现阻塞队列。
 *
 * 在设计多线程协作时，应该先想清楚共享的变量是什么，协作的条件是什么。
 *
 * 本例中，共享的当然是队列，协作的条件是：
 *      put 时队列未满；
 *      take 时队列不为空。
 *
 * 注意，在是条件发生变化之后，应该调用 notifyAll() 将所有等待的线程唤醒，
 * 唤醒之后，各线程自动检查条件是否满足。
 *
 * 调用 wait 的线程必须是已经获得了锁，调用 wait() 之后会释放锁。
 *
 * @param <T>
 */
public class SimpleBlockingQueue <T> {

    private Queue<T> queue;

    private int capacity;

    public SimpleBlockingQueue(int capacity){
        this.capacity = capacity;
        queue = new ArrayDeque<>(capacity);
    }

    public synchronized void put(T e) throws InterruptedException {
        while (queue.size() >= capacity) wait();
        queue.offer(e);
        notifyAll();
    }

    public synchronized T take() throws InterruptedException {
        while (queue.size() == 0) wait();
        T e = queue.poll();
        notifyAll();
        return e;
    }

}
