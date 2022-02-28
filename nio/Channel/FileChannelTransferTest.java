import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.Semaphore;

/**
 * 启动一个TCP服务端和TCP客户端，客户端从服务端下载一个叫 video.mp4 的文件；
 * 这个文件必须先存在于当前目录
 */
public class FileChannelTransferTest {


    public static void main(String[] args) throws InterruptedException, IOException {
        Semaphore semaphore = new Semaphore(1); // 使用一个信号量确保 Server 比 Client先启动
        startServer(semaphore); // 额外开一个线程运行
        startClient(semaphore); // 运行在主线程
    }

    /**
     * 客户端代码
     * */
    static void startClient(Semaphore semaphore) throws InterruptedException, IOException {
        semaphore.acquire();    // 等待 server 释放资源
        SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress("localhost", 9090)); // 打开一个 Socket 通道，并连接到服务端
        System.out.println("Client Started.");
        semaphore.release();    // 释放资源
        FileChannel fileChannel = FileChannel.open(Paths.get("video-downloaded.mp4"), StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE, StandardOpenOption.CREATE); // 打开文件通道
        fileChannel.transferFrom(socketChannel, 0, Long.MAX_VALUE); // 将 socket 通道的数据转到文件通道
        fileChannel.force(false);   // 确保数据刷出到 I/O 设备
        fileChannel.close();
        socketChannel.close();
        semaphore.release();
    }

    /**
     * 服务端代码
     */
    static void startServer(Semaphore semaphore) throws InterruptedException {
        semaphore.acquire();    // 获取资源
        new Thread(()->{        // 服务端线程
            try{
                ServerSocketChannel serverSocketChannel = ServerSocketChannel.open(); // 打开服务端通道
                serverSocketChannel.bind(new InetSocketAddress(9090));  // 绑定 9090 端口
                System.out.println("Server Started.");
                semaphore.release(); // 启动完成，释放资源
                SocketChannel clientChannel = serverSocketChannel.accept(); // 等待客户端连接
                FileChannel fileChannel = FileChannel.open(Paths.get("video.mp4"), StandardOpenOption.READ); // 以只读的方式打开文件通道

                long transfered = 0;
                while (transfered < fileChannel.size()){ // 循环调用 transferTo，确保数据传输完整
                    transfered += fileChannel.transferTo(transfered, fileChannel.size(), clientChannel);
                }

                fileChannel.close();            // 关闭文件通道
                clientChannel.close();          // 关闭 Socket 通道
                serverSocketChannel.close();    // 关闭服务 Socket 通道
            }catch (Exception e){
                e.printStackTrace();
            }
        }).start();
    }

}
