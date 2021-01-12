/**
 * 集合点，多个任务运行到同一位置暂停，等待其它任务运行到此位置
 *
 * 等待的条件是存在任务还没有运行到指定位置
 * 可以共享变量 taskSize 表示未运行到指定点的位置
 *
 */
public class AssemblePoint {

    private int taskSize;

    public AssemblePoint(int taskSize){
        this.taskSize = taskSize;
    }

    public synchronized void await() throws InterruptedException {
        taskSize--;
        while (taskSize > 0) wait();
    }

    public static void main(String[] args){
        int taskSize = 10;
        AssemblePoint assemblePoint = new AssemblePoint(taskSize);
        for(int i=0; i<taskSize; i++) {
            new Task(assemblePoint).start();
        }
    }

}

/**
 * 有集合点的任务
 */
class Task extends Thread{

    private final AssemblePoint assemblePoint;

    public Task(AssemblePoint assemblePoint){
        this.assemblePoint = assemblePoint;
    }

    @Override
    public void run() {
        try {
            Thread.sleep((long) (Math.random() * 2000));
            assemblePoint.await();
            System.out.printf("%d exit at %d", Thread.currentThread().getId(), System.currentTimeMillis());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}