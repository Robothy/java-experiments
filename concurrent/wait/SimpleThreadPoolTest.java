import java.util.LinkedList;

class SimpleThreadPoolTest {

    public static void main(String[] args) throws InterruptedException {
        SimpleThreadPool threadPool = new SimpleThreadPool(5);
        for(int i=0; i<100; i++){
            threadPool.execute(()->System.out.printf("%s is executing this task.\n", Thread.currentThread().getName()));
        }

        Thread.sleep(5000);
        threadPool.shutdown();
    }

}

interface Job {
    void run();
}

/**
 * 定义线程池接口
 */
interface ThreadPool {
    /** 执行任务 */
    void execute(Job job);

    /** 关闭线程池，会等待正在执行的任务执行完成，未开始的任务将被忽略 */
    void shutdown();
}

class SimpleThreadPool implements ThreadPool {

    /** 线程池容量 */
    int poolSize;

    /** 存放线程 */
    Thread[] threads;

    /** 存放任务 */
    final LinkedList<Job> jobs = new LinkedList<>();

    /** 线程池构造器，传入线程池中线程的容量 */
    public SimpleThreadPool(int size){
        this.poolSize = size;
        threads = new Thread[poolSize];
        for (int i = 0; i< poolSize; i++){
            threads[i] = new Thread(this::work, "Worker " + i);
            threads[i].start();
        }
    }

    /** 执行一个任务 */
    @Override
    public void execute(Job job) {
        synchronized (jobs){
            jobs.offer(job);
        }
    }

    /** 关闭线程池，只需要将所有的线程中断即可 */
    @Override
    public void shutdown() {
        for(int i = 0; i< poolSize; i++){
            threads[i].interrupt();
        }
    }

    /** 工作方法，执行任务 **/
    private void work() {
        System.out.printf("%s started.\n", Thread.currentThread().getName());

        for (;;){
            Job job;
            synchronized (jobs){
                while (jobs.isEmpty()) {
                    try {
                        jobs.wait();
                    } catch (InterruptedException e) {
                        System.out.printf("%s exit.\n", Thread.currentThread().getName());
                        return;
                    }
                }
                job = jobs.poll();
            }
            job.run();
        }
    }
}
