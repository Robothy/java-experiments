import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class ServerSocketChannelRead {

    public static void main(String[] args) throws IOException {
        ServerSocketChannel server = ServerSocketChannel.open(); // 打开通道
        server.bind(new InetSocketAddress(9090)); // 绑定端口
        ByteBuffer buff = ByteBuffer.allocate(10);  // 为了代码演示，只分配容量为 10 字节的缓冲区
        while (true) {
            SocketChannel client = server.accept(); // 阻塞，直到有连接过来
            System.out.println("Client connected.");
            while (true) {                          // 循环读取客户端发送过来的数据
                if(client.read(buff) == -1){        // 客户端关闭了输出之后，阻塞的 client.read(buf) 会立即返回 -1，此时 buf 中没有内容
                    client.close();                 // 关闭通道
                    System.out.println("Client closed the connection.");
                    break;
                }
                buff.flip();    // 切换到读模式
                while (buff.position() < buff.limit()) {
                    System.out.print((char) buff.get()); // 一个字符一个字符打印出来
                }
                buff.clear();   // 切换到写模式
            }
        }
    }
}
