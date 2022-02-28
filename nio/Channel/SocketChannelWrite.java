import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

public class SocketChannelWrite {

    public static void main(String[] args) throws IOException, InterruptedException {
        SocketChannel channel = SocketChannel.open(); // 打开通道，此时还没有打开 TCP 连接
        channel.connect(new InetSocketAddress("localhost", 9090)); // 连接到服务端
        ByteBuffer buf = ByteBuffer.allocate(10); // 分配一个 10 字节的缓冲区，不实用，容量太小
        Scanner scanner = new Scanner(System.in); // 扫描控制台输入
        scanner.useDelimiter("\n");
        while(true){
            String msg = scanner.next() + "\r\n"; // 读取控制台输入的消息，再拼接上换行符
            for(int i=0; i<msg.length(); i++){    // 逐个字符遍历输入的内容
                buf.put((byte)msg.charAt(i));     // 将字符逐个放入缓冲区
                if(buf.position() == buf.limit() || i == msg.length()-1){ // 缓冲区已满或者
                    buf.flip();         // 缓冲区切换到读模式
                    channel.write(buf); // 往通道写入数据
                    buf.clear();        // 清空缓冲区，缓冲区切换到写入模式
                }
            }
            if("Bye\r\n".equals(msg)){
                channel.shutdownOutput(); // 关闭 TCP 输出，此时客户端会发送 -1 给服务端
                channel.close();          // 关闭通道
                break;
            }
        }
    }
}
