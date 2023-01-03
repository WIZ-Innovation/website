package com.wiz.threads;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import com.wiz.logs.LogFactory;
import io.vertx.core.impl.ConcurrentHashSet;

public class ThreadFactoryImpl implements ThreadFactory {
    private static final LogFactory LOGGER = LogFactory.getLogger(ThreadFactoryImpl.class);
    private static final Set<ThreadImpl> currentThread = new ConcurrentHashSet<>();

    private static final Runnable CLEANER = new Runnable() {
        @Override
        public void run() {
            LocalDateTime now = LocalDateTime.now();
            for (ThreadImpl thread : currentThread) {
                if (now.isAfter(thread.getLocalDateTime().plusSeconds(30)) && thread.isAlive()) {
                    synchronized (ThreadFactoryImpl.class) {
                        try {
                            thread.interrupt();
                            thread.join();
                        } catch (InterruptedException e) {
                            LOGGER.error(e);
                        }
                    }
                }
            }

        }

    };

    static {
        Executors.newScheduledThreadPool(1).scheduleWithFixedDelay(CLEANER, 30, 30,
                TimeUnit.SECONDS);
    }

    private static final String POOL_NAME = "monitor-pool";
    private static final String THREAD_NAME = "thread";

    private static final AtomicInteger poolNumber = new AtomicInteger(1);
    private final ThreadGroup group;
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    private final String namePrefix;

    ThreadFactoryImpl() {
        SecurityManager s = System.getSecurityManager();
        group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
        namePrefix = POOL_NAME + "-" + poolNumber.getAndIncrement() + "-" + THREAD_NAME + "-";
    }

    @Override
    public Thread newThread(Runnable r) {
        ThreadImpl t = new ThreadImpl(group, r, namePrefix + threadNumber.getAndIncrement(), 0);
        if (t.isDaemon())
            t.setDaemon(false);
        if (t.getPriority() != Thread.NORM_PRIORITY)
            t.setPriority(Thread.NORM_PRIORITY);

        currentThread.add(t);

        return t;
    }
}
