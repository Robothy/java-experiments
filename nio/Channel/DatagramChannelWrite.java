import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class DatagramChannelWrite {

    public static void main(String[] args) throws IOException, InterruptedException {
        DatagramChannel channel = DatagramChannel.open(); // 打开通道

        InetSocketAddress serverAddress = new InetSocketAddress("localhost", 9090); // 声明服务端的地址

        channel.configureBlocking(false); // 非阻塞模式

        // 用于接收服务端发送过来的消息
        Thread receiver = new Thread(()->{
            ByteBuffer buf = ByteBuffer.allocate(1024);     // 分配 1024 字节的缓冲区
            while(!Thread.currentThread().isInterrupted()){ // 检查中断标志，如果被中断，则结束线程
                try {
                    while (null == channel.receive(buf)) {  // 循环接收数据
                        Thread.sleep(10);             // 没有消息则 sleep 10ms
                    }
                    buf.flip();
                    System.out.println("Server -> " + new String(buf.array(), 0, buf.limit()));
                    buf.clear();
                } catch (IOException e) {
                    e.printStackTrace();
                }catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        Thread sender = new Thread(()->{
            try {
                ByteBuffer buf = ByteBuffer.allocate(1024);
                Scanner scanner = new Scanner(System.in);
                while (true){
                    String msg = scanner.nextLine();
                    if(msg.equals("Bye")) {
                        receiver.interrupt();
                        break;
                    }
                    buf.put(msg.getBytes(StandardCharsets.UTF_8));
                    buf.flip();
                    channel.send(buf, serverAddress);
                    buf.clear();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        sender.start();     // 启动 sender 线程
        receiver.start();   // 启动 receiver线程
        receiver.join();    // 等待 receiver
        channel.close();    // 关闭通道
    }
}
