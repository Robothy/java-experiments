import java.util.Random;
import java.util.function.Supplier;

public class ConsumerProducer {

    public static void main(String[] args) throws InterruptedException {
        final SimpleBlockingQueue<Integer> repository = new SimpleBlockingQueue<>(5);

        final Random random = new Random();

        Thread produceThread = new Thread(()->{
            Producer<Integer> producer = new Producer<>(repository, random::nextInt );
            int total = 20;
            while (total-->0) {
                try {
                    System.out.println("Produce -> " + producer.produce());
                } catch (IllegalAccessException | InstantiationException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        Thread consumeThread = new Thread(()->{
            Consumer<Integer> consumer = new Consumer<>(repository);
            while (true) {
                try {
                    System.out.println( "Consume -> " + consumer.consume());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        produceThread.start();
        consumeThread.start();
        consumeThread.join();
    }

}

/**
 * 消费者
 * @param <T> 消费的类型
 */
class Consumer<T> {

    private SimpleBlockingQueue<T> repository;

    public Consumer(SimpleBlockingQueue<T> repository){
        this.repository = repository;
    }

    public T consume() throws InterruptedException {
        return this.repository.take();
    }
}

/**
 * 生产者
 * @param <T> 生产的类型
 */
class Producer<T> {
    private SimpleBlockingQueue<T> repository;
    private Supplier<T> supplier;
    public Producer(SimpleBlockingQueue<T> repository, Supplier<T> supplier){
        this.repository = repository;
        this.supplier = supplier;
    }

    public T produce() throws IllegalAccessException, InstantiationException, InterruptedException {
        T e = supplier.get();
        repository.put(e);
        return e;
    }
}


