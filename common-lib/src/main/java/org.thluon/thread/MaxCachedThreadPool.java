package org.thluon.thread;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * Custom mad CachedThreadPool!
 * Instead of always spawning thread when needed and no thread is idle,
 * the class only create to a fixed number of threads. (i.e., poolSize).
 * And clean threads when they are idle long enough.
 * In order word, it is a CachedThreadPool with maximum sized!
 */
@SuppressWarnings("ALL")
public class MaxCachedThreadPool extends AbstractExecutorService {
    private final ExecutorService executor;

    public MaxCachedThreadPool(ThreadPoolExecutor threadPoolExecutor) {
        this.executor = threadPoolExecutor;
        ((ThreadPoolExecutor) this.executor).allowCoreThreadTimeOut(true);
    }

    public MaxCachedThreadPool(int poolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        this.executor = new ThreadPoolExecutor(poolSize, poolSize, keepAliveTime, unit, workQueue);
        ((ThreadPoolExecutor) this.executor).allowCoreThreadTimeOut(true);
    }

    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks)
            throws InterruptedException {
        return this.executor.invokeAll(tasks);
    }

    public void invokeAllRunnable(Collection<? extends Runnable> tasks) throws InterruptedException {
        this.executor.invokeAll(tasks.stream().map(Executors::callable).collect(Collectors.toList()));
    }

    public Future<?> submit(Runnable task) {
        return this.executor.submit(task);
    }

    @Override
    public void shutdown() {
        this.executor.shutdown();
    }

    @Override
    public List<Runnable> shutdownNow() {
        return this.executor.shutdownNow();
    }

    @Override
    public boolean isShutdown() {
        return this.executor.isShutdown();
    }

    @Override
    public boolean isTerminated() {
        return this.executor.isTerminated();
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return this.executor.awaitTermination(timeout, unit);
    }

    public <T> Future<T> submit(Callable<T> task) {
        return this.executor.submit(task);
    }

    public void execute(Runnable command) {
        this.executor.execute(command);
    }
    public ThreadPoolExecutor getThreadPoolExecutor() {
        return (ThreadPoolExecutor) this.executor;
    }
}