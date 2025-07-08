package com.mashibing.juc.a_000;

import java.util.concurrent.locks.ReentrantLock;

public class MyReentrantLock {
    private ReentrantLock lock = new ReentrantLock();
    // ���Ի�ȡ����һ���̻߳�ȡ�ɹ��򷵻�true���ڶ����̻߳�ȡʧ���򷵻�false
    public void executeIfFree(String threadName) {
        if (lock.tryLock()) {
            try {
                System.out.println(threadName + " ִ������--��ѯ�ļ���С");
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                lock.unlock();
            }
        } else {
            System.out.println(threadName + " ��������--��ѯ�ļ���С");
        }
    }

    public static void main(String[] args) {
        MyReentrantLock mrl = new MyReentrantLock();
        new Thread(() -> mrl.executeIfFree("Thread-1")).start();
        // ˯��2��
        try {
            Thread.sleep(2000);
        }
        catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        mrl.executeIfFree("Main-Thread");
    }
}
