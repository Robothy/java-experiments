import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Random;

/**
 * MappedByteBuffer, HeapByteBuffer, InputStream/OutoutStream 文件 IO 性能比较。
 * <p>
 * 堆内存： -Xmx2500MB -Xms1024MB
 */
public class JavaIOPerformanceComparison {

    public static void main(String[] args) throws IOException {
        System.out.printf("%26s; %5s; %12s; %13s; %10s \n", "I/O Type", "R/W", "Data Size", "Buffer Size", "Duration");
        new MappedByteBufferReadWrite().test();
        new HeapByteBufferReadWrite().test();
        new StreamReadWrite().test();
    }
}

abstract class ReadWriteBenchmark {

    static int _1K = 1024, _4K = 4 * _1K, _16K = 16 * _1K, _64K = 64 * _1K;

    static int _1M = 1024 * _1K, _4M = 4 * _1M, _16M = 16 * _1M, _64M = 64 * _1M;

    static int _256M = 256 * _1M, _1G = 1024 * _1M;

    static int[] dataSizes = new int[]{_1K, _4K, _16K, _64K, _1M, _4M, _16M, _64M, _256M, _1G};

    static int[] bufSizes = new int[]{_1M, _4M, 2 * _4M, _16M, -1}; // -1 表示 buffer 的大小与文件的大小一致

    /**
     * 入口方法
     */
    public void test() throws IOException {
        for (int bSize : bufSizes) {
            for (int dataSize : dataSizes) {
                String fileName = "_" + (dataSize >> 10) + ".dat";
                int bufSize = bSize <= 0 ? dataSize : bSize;

                System.gc(); // 手动回收内存，减小自动 GC 对实验结果的影响
                long begin = System.currentTimeMillis();
                write(fileName, data(dataSize), bufSize);
                long end = System.currentTimeMillis();
                System.out.printf("%26s; %5s; %12d; %12d; %10d \n", this.getClass().getSimpleName(), "W", dataSize >> 10, bufSize >> 10, end - begin);

                System.gc(); // 手动回收内存，减小自动 GC 对实验结果的影响
                begin = System.currentTimeMillis();
                read(fileName, bufSize);
                end = System.currentTimeMillis();
                System.out.printf("%26s; %5s; %12d; %12d; %10d \n", this.getClass().getSimpleName(), "R", dataSize >> 10, bufSize >> 10, end - begin);
            }
        }
    }

    abstract void read(String fileName, int bufSize) throws IOException;

    abstract void write(String fileName, byte[] data, int bufSize) throws IOException;

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
    void read(String fileName, int bufSize) throws IOException {
        FileChannel channel = FileChannel.open(Paths.get(fileName), StandardOpenOption.READ);
        for (int position = 0; position < channel.size(); position += bufSize) { // 一部分一部分加载文件
            MappedByteBuffer mappedByteBuffer = channel.map(FileChannel.MapMode.READ_ONLY, position, Math.min(bufSize, channel.size() - position));
            mappedByteBuffer.load(); // 加载磁盘文件到物理内存
        }
        channel.close();
    }

    @Override
    void write(String fileName, byte[] data, int bufSize) throws IOException {
        FileChannel channel = FileChannel.open(Paths.get(fileName), StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        for (int position = 0; position < data.length; position += bufSize) {
            int len = Math.min(bufSize, data.length - position);
            MappedByteBuffer mappedByteBuffer = channel.map(FileChannel.MapMode.READ_WRITE, position, len);
            mappedByteBuffer.put(data, position, len);
            mappedByteBuffer.force(); // 刷出数据到磁盘
            mappedByteBuffer.clear();
        }
        channel.close();
    }
}

/**
 * 基于堆缓冲区 HeapByteBuffer 和通道 Channel 的文件读写操作，缓冲区在虚拟机堆中
 */
class HeapByteBufferReadWrite extends ReadWriteBenchmark {

    @Override
    void read(String fileName, int bufSize) throws IOException {
        FileChannel channel = FileChannel.open(Paths.get(fileName), StandardOpenOption.READ);
        ByteBuffer buffer = ByteBuffer.allocate(bufSize);
        while (-1 != channel.read(buffer)) buffer.clear(); // 将通道中的数据读到缓冲区中
        channel.close();
    }

    @Override
    void write(String fileName, byte[] data, int bufSize) throws IOException {
        FileChannel channel = FileChannel.open(Paths.get(fileName), StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        ByteBuffer buffer = ByteBuffer.allocate(bufSize);
        for (int position = 0; position < data.length; position += bufSize) {
            buffer.put(data, position, Math.min(bufSize, data.length - position));
            channel.write(buffer);
            channel.force(false); // 刷出数据到磁盘
            buffer.clear();
        }
        channel.close();
    }
}

/**
 * 基于输入输出流的文件读写
 */
class StreamReadWrite extends ReadWriteBenchmark {

    @Override
    void read(String fileName, int bufSize) throws IOException {
        File file = new File(fileName);
        FileInputStream fileInputStream = new FileInputStream(file);
        byte[] buf = new byte[bufSize];
        while (-1 != fileInputStream.read(buf)) ;
        fileInputStream.close();
    }

    @Override
    void write(String fileName, byte[] data, int bufSize) throws IOException {
        File file = new File(fileName);
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        for (int position = 0; position < data.length; position += bufSize) {
            fileOutputStream.write(data, position, Math.min(bufSize, data.length - position));
            fileOutputStream.flush();
        }
        fileOutputStream.close();
    }
}