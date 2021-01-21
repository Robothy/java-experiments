/**
 * Daemon 线程是 Java 中的一种特殊线程，当虚拟机种没有非 Daemon 线程时，虚拟机将退出。
 *
 * 下面代码启动时有 main 线程和一个 Daemon 线程，主线程退出之后，虚拟机将退出，Daemon 线程里面的内容将不被执行。
 * 下面程序运行之后，控制台将看不到任何输出。
 */
public class DaemonThreadTest {

    public static void main(String[] args) {

        Thread daemonThread = new Thread(() -> {
            try {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } finally {
                System.out.println("Daemon Thread Finished.");
            }
        });

        daemonThread.setDaemon(true); // Daemon 线程的设置必须在启动线程之前设置
        daemonThread.start();
    }

}
