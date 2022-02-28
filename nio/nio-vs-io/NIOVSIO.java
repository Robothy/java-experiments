import java.io.*;
import java.net.InetSocketAddress;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class NIOVSIO {

    public static void main() throws IOException {


    }

    void io() throws IOException {
        File file = new File("data.csv");
        InputStream in = new FileInputStream(file);
        OutputStream out = new FileOutputStream(file);

        byte[] buf = new byte[1024];
        int len = in.read(buf);
        out.write(buf, 0, len);
        in.close();
        out.close();
    }

    void nio() throws IOException {
        FileChannel channel = FileChannel.open(Paths.get("data.csv"), StandardOpenOption.READ, StandardOpenOption.WRITE);
        new FileOutputStream(new File("data.csv")).getChannel();

        SocketChannel localhost = SocketChannel.open(new InetSocketAddress("localhost", 9090));
        localhost.configureBlocking(false);

        ServerSocketChannel server = ServerSocketChannel.open();
        SocketChannel chan = server.accept();

    }

}
