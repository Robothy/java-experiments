import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.Semaphore;

public class AsyncServerSocketChannel {

  public static void main(String[] args) throws IOException, InterruptedException {
    AsynchronousServerSocketChannel serverSocketChannel = AsynchronousServerSocketChannel.open();
    serverSocketChannel.bind(new InetSocketAddress("127.0.0.1", 9090));
    serverSocketChannel.accept(null, new CompletionHandler<>() { // 异步建立连接
      @Override
      public void completed(AsynchronousSocketChannel socketChannel, Object attachment) { // 成功建立连接
        serverSocketChannel.accept(null, this);           // 接收下一个连接

        ByteBuffer buf = ByteBuffer.allocate(8); // 分配一个 8 字节的缓冲区
        socketChannel.read(buf, null, new CompletionHandler<>() { // 异步读取数据
          @Override
          public void completed(Integer len, Object attachment) {           // 成功读取到数据
            if (-1 != len) { // 客户端未关闭通道
              System.out.print(new String(buf.array(), 0, len));
              buf.clear();    // 清除缓冲区，为下一次写入数据做准备
              socketChannel.read(buf, null, this);        // 继续读取下一批数据
            } else {
              try {
                socketChannel.close(); // 关闭通道
              } catch (IOException e) {
                e.printStackTrace();
              }
              System.out.println();
            }
          }

          @Override
          public void failed(Throwable exc, Object attachment) {
            exc.printStackTrace();
          }
        });
      }

      @Override
      public void failed(Throwable exc, Object attachment) {
        exc.printStackTrace();
      }
    });

    new Semaphore(0).acquire(); // 阻塞主线程
  }

}