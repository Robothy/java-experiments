import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.charset.StandardCharsets;

public class DatagramChannelRead {

    public static void main(String[] args) throws IOException {
        DatagramChannel channel = DatagramChannel.open(); // 打开通道
        channel.bind(new InetSocketAddress(9090));   // 绑定要监听的端口
        ByteBuffer buf = ByteBuffer.allocate(1024);       // 分配缓冲区

        while (true){
            SocketAddress address = channel.receive(buf);  // 接收数据，获取发送方地址
            buf.flip(); // 缓冲区切换为读模式
            int len = buf.limit(); // 获取 buff 中数据的长度
            System.out.println("Client -> " + new String(buf.array(), 0, len, StandardCharsets.UTF_8)); // 打印 buf 中的内容
            buf.clear(); // 清空缓冲区，切换到写模式

            buf.put(String.format("Received %4d bytes.", len).getBytes()); // 将要返回给发送端的消息填入缓冲区
            buf.flip();
            channel.send(buf, address); // send 一次性最多只能发送 65535 - 8 字节的数据，如果 buf 很大的话需要用一个循环去发送。
            buf.clear();
        }
    }
}
