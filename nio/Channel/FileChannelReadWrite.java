import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class FileChannelReadWrite {

    public static void main(String[] args) throws IOException {
        String fileName = "data.txt";
        String text = "Hello, we中文lcome to Robothy's blog.";
        writeText(fileName, text);
        System.out.println(readText(fileName));

    }

    static String readText(String fileName) throws IOException {
        FileChannel channel = FileChannel.open(Paths.get(fileName), StandardOpenOption.READ);// 获取文件通道
        ByteBuffer buf = ByteBuffer.allocate(10); // 分配字节缓存
        StringBuilder text = new StringBuilder();
        while (channel.read(buf) != -1){ // 读取通道中的数据，并写入到 buf 中
            buf.flip(); // 缓存区切换到读模式
            while (buf.position() < buf.limit()){ // 读取 buf 中的数据
                text.append((char)buf.get());
            }
            buf.clear(); // 清空 buffer，缓存区切换到写模式
        }
        channel.close(); // 关闭通道
        return text.toString();
    }

    static void writeText(String fileName, String text) throws IOException {
        FileChannel channel = FileChannel.open(Paths.get(fileName), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING); // 获取文件通道
        ByteBuffer buf = ByteBuffer.allocate(10); // 创建字节缓冲区
        for (int i = 0; i < text.length(); i++) {
            buf.put((byte)text.charAt(i)); // 填充缓冲区，需要将 2 字节的 char 强转为 1 自己的 byte
            if (buf.position() == buf.limit() || i == text.length() - 1) { // 缓存区已满或者已经遍历到最后一个字符
                buf.flip(); // 将缓冲区由写模式置为读模式
                channel.write(buf); // 将缓冲区的数据写到通道
                buf.clear(); // 清空缓存区，将缓冲区置为写模式，下次才能使用
            }
        }
        channel.force(false); // 将数据刷出到磁盘，不刷出文件元数据
        channel.close(); // 关闭通道
    }

}


