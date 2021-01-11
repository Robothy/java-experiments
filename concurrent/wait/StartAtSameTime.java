import java.util.ArrayList;
import java.util.List;

public class StartAtSameTime {

    public static void main(String[] args) throws InterruptedException {
        int size = 10;
        FireFlag fireFlag = new FireFlag();
        List<Calculator> list = new ArrayList<>(size);
        for (int i=0; i<size; i++){
            Calculator calculator = new Calculator(fireFlag);
            calculator.start();
            list.add(calculator);
        }

        // 确保所有线程全部执行了 start() 之后，进入 WAITING 状态
        Thread.sleep(100);

        // 修复 flag 标志为 true，并且 notifyAll()，让所有的线程进入 RUNNABLE 状态
        fireFlag.go();

        // 确保执行完成
        for (Calculator calculator : list) {
            calculator.join();
        }
    }



}

/**
 * 共享的变量是 flag，等待条件是 flag 的值为 false
 */
class FireFlag {

    private volatile boolean flag;

    public synchronized void ready() throws InterruptedException {
        while (!flag) wait();
    }

    public synchronized void go(){
        flag = true;
        notifyAll();
    }
}

/**
 * 一个 Calculator 对象表示一个线程
 */
class Calculator extends Thread{

    private FireFlag fireFlag;

    public Calculator(FireFlag fireFlag){
        this.fireFlag = fireFlag;
    }

    @Override
    public void run() {
        try {
            this.fireFlag.ready();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        long sum = 0;
        for(int i=0; i<10000; i++){
            sum += i;
        }
        System.out.printf("NO. %d finished calculation %d. Result is %d \n", Thread.currentThread().getId(), System.currentTimeMillis(), sum);
    }
}
