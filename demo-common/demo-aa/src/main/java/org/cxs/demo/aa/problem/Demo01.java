package org.cxs.demo.aa.problem;

/**
 * 面试题：用程序实现有三个线程，要求让它们交替输出 1、2、3。
 */
public class Demo01 {
    int temp = 1;
    int count = 1;

    public static void main(String[] args) {
        final Demo01 demoA = new Demo01();

        // 方式1
        for (int i = 1; i < 4; i++) {
            new Thread(() -> {
                while (true) {
                    synchronized (demoA) {
                        try {
                            while (demoA.temp != Integer.parseInt(Thread.currentThread().getName())
                                    && demoA.count < 100) {
                                demoA.wait();
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (demoA.count > 99) {
                            demoA.notifyAll();
                            break;
                        }
                        System.out.println(Thread.currentThread().getName() + ": " + demoA.count++);
                        demoA.temp++;
                        if (demoA.temp > 3) {
                            demoA.temp = 1;
                        }
                        demoA.notifyAll();
                    }
                }

            }, i + "").start();
        }

        // 方式2
    }
}
