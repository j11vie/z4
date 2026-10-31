import java.util.concurrent.atomic.AtomicInteger;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        Checker checker = new Checker();
        checker.test();
    }
}

class Checker {
    static final int LIMIT = 100_000_000;
    private final AtomicInteger calls = new AtomicInteger(0);
    final AtomicInteger total = new AtomicInteger(0);

    boolean isDone() {
        return calls.incrementAndGet() > LIMIT;
    }

    void test() throws InterruptedException {
        Thread first = new Thread(new Worker(this));
        Thread second = new Thread(new Worker(this));

        first.start();
        second.start();
        first.join();
        second.join();

        System.out.println("Expected: " + LIMIT);
        System.out.println("Result: " + total.get());
    }
}

class Worker implements Runnable {
    private final Checker checker;

    Worker(Checker checker) {
        this.checker = checker;
    }

    @Override
    public void run() {
        while (!checker.isDone()) {
            // обычный total++ не атомарный: два потока могли прочитать одно и то же
            // значение (например 12) и оба записать 13 вместо 14, поэтому значения терялись.
            // incrementAndGet() делает это за одну операцию, и ничего не теряется
            checker.total.incrementAndGet();
        }
    }
}
