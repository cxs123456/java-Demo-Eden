package org.cxs.serviceB;

import java.nio.IntBuffer;
import java.util.Scanner;

/**
 * @author chenxinsui
 * @date 2020/12/18 18:08
 * @description:
 **/
public class DemoNetty {

    public static void main(String[] args) {
        IntBuffer intBuffer = IntBuffer.allocate(10);
        StringBuffer stringBuffer = new StringBuffer("123");

        //发送数据给服务器端
        Scanner scanner = new Scanner(System.in);

        while (scanner.hasNextLine()) {
            String s = scanner.nextLine();
            System.out.println(s);
        }
    }
}
