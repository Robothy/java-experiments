import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Random;

/**
 * MappedByteBuffer, HeapByteBuffer, InputStream/OutoutStream 文件 IO 性能比较。
 * <p>
 * 设置堆内存 -Xmx2048MB -Xms2048MB，避免堆动态扩展影响实验结果
 */
public class JavaIOPerformanceComparison {

    public static void main(String[] args) throws IOException {

        HeapByteBufferReadWrite heapByteBufferReadWrite = new HeapByteBufferReadWrite();
        heapByteBufferReadWrite.test("Write");
        heapByteBufferReadWrite.test("Read");
        heapByteBufferReadWrite.delete();

        MappedByteBufferReadWrite mappedByteBufferReadWrite = new MappedByteBufferReadWrite();
        mappedByteBufferReadWrite.test("Write");
        mappedByteBufferReadWrite.test("Read");
        mappedByteBufferReadWrite.delete();

        StreamReadWrite streamReadWrite = new StreamReadWrite();
        streamReadWrite.test("Write");
        streamReadWrite.test("Read");
        streamReadWrite.delete();
    }
}

abstract class ReadWriteBenchmark {

    static int _1KB = 1024, _8KB = 8 * _1KB, _64KB = 64 * _1KB;
    static int _1MB = 1024 * _1KB, _32MB = 32 * _1MB, _512MB = 512 * _1MB;
    static int _1GB = 1024 * _1MB;

    static int[] sizes = new int[]{_1KB, _8KB, _64KB, _1MB, _32MB, _512MB, _1GB};

    /**
     * 入口方法
     *
     * @param type READ 测试读；WRITE 测试写
     */
    public void test(String type) throws IOException {
        for (int size : sizes) {
            String fileName = "_" + size + ".dat";
            long begin = System.currentTimeMillis();
            if ("READ".equalsIgnoreCase(type)) {
                read(fileName);
            } else {
                write(fileName, data(size));
            }
            long end = System.currentTimeMillis();
            System.out.printf("%s %s %d KB  data costs %d ms \n", this.getClass().getSimpleName(), type, size >> 10, end - begin);
        }
        System.out.println();
        System.gc(); // 手动回收内存，避免下次运行测试代码时触发回收
    }

    abstract void read(String fileName) throws IOException;

    abstract void write(String fileName, byte[] data) throws IOException;

    // 清除产生的文件
    public void delete() throws IOException {
        for (long size : sizes) {
            String fileName = "_" + size + ".dat";
            while (true) {
                try {
                    Files.delete(Paths.get(fileName));
                    break;
                } catch (IOException e) {
                    System.err.println("Failed to delete " + fileName + ", will re-try immediately.");
                    try {
                        Thread.sleep(100L);
                    } catch (InterruptedException interruptedException) {
                        interruptedException.printStackTrace();
                    }
                }
            }
        }
    }

    // 产生指定字节数的数据
    byte[] data(int size) {
        byte[] d = new byte[size];
        new Random(size).nextBytes(d); // 随机数生成器，以 size 作种子，确保产生相同的数据
        return d;
    }
}

/**
 * 基于 MappedByteBuffer，内存映射，缓冲区在虚拟机堆外的直接内存
 */
class MappedByteBufferReadWrite extends ReadWriteBenchmark {

    @Override
    void read(String fileName) throws IOException {
        FileChannel channel = FileChannel.open(Paths.get(fileName), StandardOpenOption.READ);
        MappedByteBuffer mappedByteBuffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
        mappedByteBuffer.load(); // 加载磁盘文件到物理内存
        mappedByteBuffer.flip();
        while (mappedByteBuffer.position() < mappedByteBuffer.limit()) mappedByteBuffer.get();
        channel.close();
    }

    @Override
    void write(String fileName, byte[] data) throws IOException {
        FileChannel channel = FileChannel.open(Paths.get(fileName), StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.CREATE_NEW);
        MappedByteBuffer mappedByteBuffer = channel.map(FileChannel.MapMode.READ_WRITE, 0, data.length);
        mappedByteBuffer.put(data);
        mappedByteBuffer.force(); // 刷出数据到磁盘
        channel.close();
    }
}

/**
 * 基于 HeapByteBuffer 的文件读写操作，缓冲区在虚拟机堆中
 */
class HeapByteBufferReadWrite extends ReadWriteBenchmark {

    @Override
    void read(String fileName) throws IOException {
        FileChannel channel = FileChannel.open(Paths.get(fileName), StandardOpenOption.READ);
        ByteBuffer buffer = ByteBuffer.allocate((int) channel.size());
        channel.read(buffer); // 将通道中的数据读到缓冲区中
        assert buffer.position() == channel.size();
        while (buffer.position() < buffer.limit()) buffer.get();
        channel.close();
    }

    @Override
    void write(String fileName, byte[] data) throws IOException {
        FileChannel channel = FileChannel.open(Paths.get(fileName), StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.CREATE_NEW);
        ByteBuffer buffer = ByteBuffer.allocate(data.length);
        buffer.put(data);
        channel.force(false); // 刷出数据到磁盘
        channel.close();
    }
}

/**
 * 基于输入输出流的文件读写
 */
class StreamReadWrite extends ReadWriteBenchmark {

    @Override
    void read(String fileName) throws IOException {
        File file = new File(fileName);
        FileInputStream fileInputStream = new FileInputStream(file);
        while(fileInputStream.read() != -1) ;
        fileInputStream.close();
    }

    @Override
    void write(String fileName, byte[] data) throws IOException {
        File file = new File(fileName);
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        fileOutputStream.write(data);
        fileOutputStream.flush();
        fileOutputStream.close();
    }
}