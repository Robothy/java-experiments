/**
 * join() 规则，如果线程 A 中执行 threadB.join()，则 JMM 确保线程 A 中 threadB.join() 返回之前，线程 B 执行完成；
 * 且执行结果对线程 A 中 threadB.join() 之后的指令可见。
 *
 * 下面代码会产生死锁。
 */
public class JoinTest {

    public static void main(String[] args) throws InterruptedException {

        Thread[] threads = new Thread[2];

        threads[0] = new Thread(()-> {
            try {
                threads[1].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        threads[1] = new Thread(()->{
            try {
                threads[0].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        threads[0].start();
        threads[1].start();
    }

}
