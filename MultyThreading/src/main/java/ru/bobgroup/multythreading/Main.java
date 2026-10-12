package ru.bobgroup.multythreading;

import java.util.SplittableRandom;
import java.util.concurrent.TimeUnit;

public class Main {

    private static final int DEFAULT_ARRAY_SIZE = 50_000_000;
    private static final int RANDOM_BOUND = 1_000;
    private static final long RANDOM_SEED = 42L;

    public static void main(String[] args) {
        int arraySize = readPositiveArgument(args, 0, DEFAULT_ARRAY_SIZE, "array size");
        int threadCount = readPositiveArgument(
                args,
                1,
                Runtime.getRuntime().availableProcessors(),
                "thread count"
        );

        System.out.printf("Creating array: %,d numbers%n", arraySize);
        System.out.printf("Available processors: %d%n", Runtime.getRuntime().availableProcessors());
        System.out.printf("Worker threads: %d%n%n", threadCount);

        int[] values = createRandomArray(arraySize);

        Measurement sequential = measure(() -> ArraySummer.sumSequentially(values));
        Measurement parallel = measure(() -> ArraySummer.sumInParallel(values, threadCount));

        if (sequential.sum() != parallel.sum()) {
            throw new IllegalStateException("Results do not match");
        }

        System.out.printf("Sequential: sum=%,d, time=%,d ms%n", sequential.sum(), sequential.millis());
        System.out.printf("Parallel:   sum=%,d, time=%,d ms%n", parallel.sum(), parallel.millis());

        if (parallel.nanos() > 0) {
            double speedup = (double) sequential.nanos() / parallel.nanos();
            System.out.printf("Speedup:    %.2fx%n", speedup);
        }
    }

    private static int[] createRandomArray(int size) {
        int[] values = new int[size];
        SplittableRandom random = new SplittableRandom(RANDOM_SEED);
        for (int index = 0; index < values.length; index++) {
            values[index] = random.nextInt(RANDOM_BOUND);
        }
        return values;
    }

    private static Measurement measure(SumOperation operation) {
        long startedAt = System.nanoTime();
        long sum = operation.calculate();
        long elapsed = System.nanoTime() - startedAt;
        return new Measurement(sum, elapsed);
    }

    private static int readPositiveArgument(String[] args, int index, int defaultValue, String name) {
        if (args.length <= index) {
            return defaultValue;
        }

        int value;
        try {
            value = Integer.parseInt(args[index]);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(name + " must be an integer", exception);
        }

        if (value <= 0) {
            throw new IllegalArgumentException(name + " must be positive");
        }
        return value;
    }

    @FunctionalInterface
    private interface SumOperation {
        long calculate();
    }

    private record Measurement(long sum, long nanos) {
        long millis() {
            return TimeUnit.NANOSECONDS.toMillis(nanos);
        }
    }
}
