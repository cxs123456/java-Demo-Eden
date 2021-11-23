package org.cxs.demo.aa.problem;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 三个线程，要求让它们交替输出 A、B、C
 * 使用 ReentrantLock  Condition
 *
 * @author Administrator
 * @date 2021/11/23 4:13
 **/
public class Demo02 {

    static int count = 0;
    static int flag = 1;

    public static void main(String[] args) {
        ReentrantLock lock = new ReentrantLock();
        Condition a = lock.newCondition();
        Condition b = lock.newCondition();
        Condition c = lock.newCondition();

        Thread t1 = new Thread(() -> {
            while (count < 100) {
                try {
                    lock.lock();
                    while (flag != 1) {
                        a.await();
                    }
                    System.out.println(Thread.currentThread().getName() + ", " + count);
                    flag = 2;
                    b.signal();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    count++;
                    lock.unlock();
                }
            }

        }, "A");
        Thread t2 = new Thread(() -> {
            while (count < 100) {
                try {
                    lock.lock();
                    while (flag != 2) {
                        b.await();
                    }
                    System.out.println(Thread.currentThread().getName() + ", " + count);
                    flag = 3;
                    c.signal();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    count++;
                    lock.unlock();
                }
            }

        }, "B");

        Thread t3 = new Thread(() -> {
            while (count < 100) {
                try {
                    lock.lock();
                    while (flag != 3) {
                        c.await();
                    }
                    System.out.println(Thread.currentThread().getName() + ", " + count);
                    flag = 1;
                    a.signal();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    count++;
                    lock.unlock();
                }
            }
        }, "C");
        t1.start();
        t2.start();
        t3.start();
    }
}
