import java.util.concurrent.Callable;

public class SimpleFutureTaskTest {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        SimpleFutureTask<Long> calculator = new SimpleFutureTask<Long>() {
            @Override
            public Long call() throws Exception {
                long sum = 0;
                for(int i=0; i<100; i++){
                    sum += i;
                }
                return sum;
            }
        };

        calculator.start();

        System.out.println(calculator.get());

    }

}

/**
 * 需要进行协作的线程是执行任务的线程和调用任务的线程。
 *
 * 协作的条件是主线程调用 get() 方法时等待执行任务的线程完成任务。
 *
 * 由共享条件设计共享变量： boolean finish
 *
 * get() 方法进行等待，等待 finish 置为 true；
 * run() 方法在执行结束之后将 finish 置为 true，并且调用 signal 唤醒所有阻塞在 get 方法上的线程。
 *
 * @param <V>
 */
abstract class SimpleFutureTask<V> extends Thread implements Callable<V> {

    private boolean finished;

    private V res;

    private ExecutionException exception;

    private final Object lock = new Object();

    @Override
    public void run() {
        try {
            this.res = call();
        } catch (Exception e) {
            this.exception = new ExecutionException(e);
        }finally {
            synchronized (lock){
                this.finished = true;
                lock.notifyAll();
            }
        }
    }

    public V get() throws InterruptedException, ExecutionException {
        synchronized (lock){
            while (!finished) lock.wait(); // 阻塞等待任务执行完成
        }
        if (exception != null) throw exception;
        return res;
    }
}


class ExecutionException extends Exception {
    public ExecutionException(Exception e) {
        super(e);
    }
}
