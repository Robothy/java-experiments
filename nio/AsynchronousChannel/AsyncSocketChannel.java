import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;

public class AsyncSocketChannel {

  public static void main(String[] args) throws IOException, InterruptedException {
    AsynchronousSocketChannel socketChannel = AsynchronousSocketChannel.open(); // 打开一个异步的 Socket 通道
    InetSocketAddress serverAddress = new InetSocketAddress("127.0.0.1", 9090); // 服务端地址
    Semaphore semaphore = new Semaphore(0); // 定义一个信号量，用来确保主线程等待 Socket 将数据完成再退出
    socketChannel.connect(serverAddress, null, new CompletionHandler<>() {
      @Override
      public void completed(Void result, Object attachment) { // 成功建立连接之后触发
        String msg = "Hello, this is a TCP Client.";
        ByteBuffer data = ByteBuffer.wrap(msg.getBytes(StandardCharsets.UTF_8));  // 将要发送的数据放到缓冲区中
        socketChannel.write(data, null, new CompletionHandler<>() {     // 往通道中写（发）数据给服务端

          @Override
          public void completed(Integer result, Object attachment) {  // 成功写完一批数据后触发
            if (data.hasRemaining()) { // 缓冲区还有数据
              socketChannel.write(data, null, this); // 继续（写）发给服务端
            } else { // 缓冲区数据已经全部发送给了客户端
              try {
                socketChannel.shutdownOutput();   // 关闭输出，服务端调用 read 时收到返回值 -1
                socketChannel.close();            // 关闭通道
                semaphore.release();              // 释放信号量许可，让主线程可以继续往下走
              } catch (IOException e) {
                e.printStackTrace();
              }
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

    semaphore.acquire(); // 等到异步线程工作完成
  }
}

/**
 * 异步 Socket，返回 Future。
 */
class AsyncSocketChannel2 {
  public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
    AsynchronousSocketChannel socketChannel = AsynchronousSocketChannel.open();
    InetSocketAddress serverAddress = new InetSocketAddress("127.0.0.1", 9090);
    Future<Void> connect = socketChannel.connect(serverAddress); // 连接到服务端
    connect.get();  // 阻塞，等待连接建立成功

    byte[] data = "AsyncSocketChannel with Future.".getBytes(StandardCharsets.UTF_8);
    ByteBuffer buf = ByteBuffer.allocate(4);
    for (int i = 0; i < data.length; i += buf.capacity()) {
      buf.put(data, i, Math.min(buf.capacity(), data.length - i));
      buf.flip();                   // 使缓冲区变为可读状态
      while (buf.hasRemaining()) {  // 缓冲区中还有数据（缓冲区的数据不一定能够一次性就被发送出去）
        Future<Integer> future = socketChannel.write(buf); // 非阻塞发送数据
        future.get(); // 阻塞等待数据发送成功
      }
      buf.clear();    // 清空缓冲区，变为可写状态
    }
    socketChannel.shutdownOutput();
    socketChannel.close();
  }
}
