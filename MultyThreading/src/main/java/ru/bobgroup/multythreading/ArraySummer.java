package ru.bobgroup.multythreading;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public final class ArraySummer {

    private ArraySummer() {
    }

    public static long sumSequentially(int[] values) {
        long sum = 0;
        for (int value : values) {
            sum += value;
        }
        return sum;
    }

    public static long sumInParallel(int[] values, int requestedThreadCount) {
        if (requestedThreadCount <= 0) {
            throw new IllegalArgumentException("Thread count must be positive");
        }
        if (values.length == 0) {
            return 0;
        }

        int workerCount = Math.min(requestedThreadCount, values.length);
        int chunkSize = (values.length + workerCount - 1) / workerCount;

        try (ExecutorService executor = Executors.newFixedThreadPool(workerCount)) {
            List<Future<Long>> partialSums = new ArrayList<>(workerCount);

            for (int start = 0; start < values.length; start += chunkSize) {
                int from = start;
                int to = Math.min(start + chunkSize, values.length);
                partialSums.add(executor.submit(() -> sumRange(values, from, to)));
            }

            long total = 0;
            for (Future<Long> partialSum : partialSums) {
                total += partialSum.get();
            }
            return total;
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Summation was interrupted", exception);
        } catch (ExecutionException exception) {
            throw new IllegalStateException("Worker failed", exception.getCause());
        }
    }

    private static long sumRange(int[] values, int from, int to) {
        long sum = 0;
        for (int index = from; index < to; index++) {
            sum += values[index];
        }
        return sum;
    }
}
