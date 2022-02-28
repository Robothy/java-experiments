import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;

public class AsyncFileChannel {

  public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
    Path path = Paths.get("data.txt"); // 准备一些数据

    /* 异步写入数据 */
    byte[] data = "This is an example of AsynchronousFileChannel".getBytes(StandardCharsets.UTF_8);
    ByteBuffer buff = ByteBuffer.allocate(4); // 分配一个大小为 4 的字节缓冲区
    AsynchronousFileChannel writeChan = AsynchronousFileChannel.open(path, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
    long position = 0; // 记录写入数据在文件中的起始位置
    for (int i = 0; i<data.length; i+=buff.capacity()) {
      buff.put(data, i, Math.min(buff.capacity(), data.length - i)); // 将数据放入缓冲区
      buff.flip(); // 将缓冲区变为读模式
      int len;     // 记录成功写入的字节长度
      while (buff.hasRemaining()) {
        Future<Integer> future = writeChan.write(buff, position); // 异步写入数据，并立即返回
        len = future.get(); // 阻塞等待异步操作完成，效率低
        position += len;    // 更新 position 位置
      }
      buff.clear(); // 清空缓冲区，将缓冲区变为写模式
    }
    writeChan.force(false);
    writeChan.close();

    /* 异步读取数据 */
    Semaphore semaphore = new Semaphore(0);
    AsynchronousFileChannel readChan = AsynchronousFileChannel.open(path, StandardOpenOption.READ); // 打开一个异步文件通道
    readChan.read(buff, 0, null, new CompletionHandler<>() { // 从位置 0 开始读取数据，数据读取到缓冲区 buff 中
      long readSize = 0; // 已经读取的字节数
      @Override
      public void completed(Integer result, Object attachment) {
        // 打印读取到的数据
        System.out.println(Thread.currentThread() + new String(buff.array(), 0, result));
        try {
          if ( (readSize = readSize + result) < readChan.size()) { // 已读取字节数少于文件总字节数，继续读取
            buff.clear(); // 将 buff 的 position 移动到起始位置，使其变为可写状态
            readChan.read(buff, readSize, null, this); // 递归，继续读取，注意改变读取位置，Handler 直接使用 this。
          } else {
            semaphore.release();
          }
        } catch (IOException e) {
          e.printStackTrace();
        }
      }

      @Override
      public void failed(Throwable exc, Object attachment) {
        exc.printStackTrace();
      }
    });

    // 主线程等待文件数据读取结束。
    semaphore.acquire();
  }

}

class AsyncFileChannel2 {
  public static void main(String[] args) throws IOException, InterruptedException {
    Path path = Paths.get("data.txt");
    AsynchronousFileChannel asyncFileChan = AsynchronousFileChannel.open(path, StandardOpenOption.READ);
    Context context = new Context();          // 自定义类，存放上下文信息，上下文信息可根据需要设定
    context.asyncFileChan = asyncFileChan;
    context.buffer = ByteBuffer.allocate(4);
    AsyncReadDataHandler callback = new AsyncReadDataHandler(); // 创建一个处理器对象
    asyncFileChan.read(context.buffer, 0, context, callback); // 执行异步读取数据
    callback.waitForEnd();
  }
}

/** 定义上下文类 */
class Context {
  AsynchronousFileChannel asyncFileChan;
  ByteBuffer buffer;
}

/** 回调实现类 */
class AsyncReadDataHandler implements CompletionHandler<Integer, Context> {

  private long readSize = 0; // 统计已读取的字节数

  private final Semaphore semaphore = new Semaphore(0); // 信号量

  @Override
  public void completed(Integer size, Context context) {
    System.out.print(new String(context.buffer.array(), 0, size));
    context.buffer.clear();
    try {
      if ( (readSize = readSize + context.buffer.limit()) < context.asyncFileChan.size()) {
        // 还有数据，继续读。数据放入到 context.buffer 中，从 readSize 位置开始读，附件是 context，处理器是当前对象
        context.asyncFileChan.read(context.buffer, readSize, context, this);
      } else {
        semaphore.release();
      }
    } catch (IOException e) {
      e.printStackTrace();
      semaphore.release();
    }
  }

  @Override
  public void failed(Throwable cause, Context context) {
    cause.printStackTrace();
    semaphore.release();
  }

  // 等待结束
  public void waitForEnd() throws InterruptedException {
    semaphore.acquire();
  }
}
