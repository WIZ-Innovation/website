package com.wiz.threads;

import java.time.LocalDateTime;

public class ThreadImpl extends Thread {
    private LocalDateTime localDateTime;

    public ThreadImpl() {
        this.setLocalDateTime();
    }

    public ThreadImpl(Runnable target) {
        super(target);
        this.setLocalDateTime();
    }

    public ThreadImpl(String name) {
        super(name);
        this.setLocalDateTime();
    }

    public ThreadImpl(ThreadGroup group, Runnable target) {
        super(group, target);
        this.setLocalDateTime();
    }

    public ThreadImpl(ThreadGroup group, String name) {
        super(group, name);
        this.setLocalDateTime();
    }

    public ThreadImpl(Runnable target, String name) {
        super(target, name);
        this.setLocalDateTime();
    }

    public ThreadImpl(ThreadGroup group, Runnable target, String name) {
        super(group, target, name);
        this.setLocalDateTime();
    }

    public ThreadImpl(ThreadGroup group, Runnable target, String name, long stackSize) {
        super(group, target, name, stackSize);
        this.setLocalDateTime();
    }

    public ThreadImpl(ThreadGroup group, Runnable target, String name, long stackSize,
            boolean inheritThreadLocals) {
        super(group, target, name, stackSize, inheritThreadLocals);
        this.setLocalDateTime();
    }

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    @Override
    public void run() {
        this.setLocalDateTime();
        super.run();
    }

    @Override
    public synchronized void start() {
        this.setLocalDateTime();
        super.start();
    }

    public void setLocalDateTime() {
        this.localDateTime = LocalDateTime.now();
    }
}
