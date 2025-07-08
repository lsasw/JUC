package com.mashibing.juc.a_000;

import java.util.concurrent.locks.ReentrantLock;

public class MyReentrantLock {
    private ReentrantLock lock = new ReentrantLock();
    // 尝试获取，第一个线程获取成功则返回true，第二个线程获取失败则返回false
    public void executeIfFree(String threadName) {
        if (lock.tryLock()) {
            try {
                System.out.println(threadName + " 执行任务--查询文件大小");
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                lock.unlock();
            }
        } else {
            System.out.println(threadName + " 跳过任务--查询文件大小");
        }
    }

    public static void main(String[] args) {
        MyReentrantLock mrl = new MyReentrantLock();
        new Thread(() -> mrl.executeIfFree("Thread-1")).start();
        // 睡眠2秒
        try {
            Thread.sleep(2000);
        }
        catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        mrl.executeIfFree("Main-Thread");
    }
}
