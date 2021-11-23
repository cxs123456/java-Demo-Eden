package org.cxs.demo.aa;

/**
 * 面试题：用程序实现两个线程交替打印 0~100 的奇偶数。
 */
public class DemoA {

    int count = 0;

    public static void main(String[] args) throws InterruptedException {
        final DemoA demoA = new DemoA();

        Thread odd = new Thread(() -> {
            while (true) {
                synchronized (demoA) {
                    try {
                        while ((demoA.count & 1) == 0) {
                            demoA.wait();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println(Thread.currentThread().getName() + ": " + demoA.count++);
                    demoA.notifyAll();
                    if (demoA.count > 99) {
                        break;
                    }
                }
            }

        }, "奇数");

        Thread even = new Thread(() -> {
            while (true) {
                synchronized (demoA) {
                    try {
                        while ((demoA.count & 1) == 1) {
                            demoA.wait();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    System.out.println(Thread.currentThread().getName() + ": " + demoA.count++);
                    demoA.notifyAll();
                    if (demoA.count > 99) {
                        break;
                    }
                }
            }

        }, "偶数");

        odd.start();
        even.start();


        odd.join();
        even.join();

        System.out.println("执行完成...");
    }
}
