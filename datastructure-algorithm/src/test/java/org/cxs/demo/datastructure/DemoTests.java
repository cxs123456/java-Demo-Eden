package org.cxs.demo.datastructure;

import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * note:
 *
 * @author cxs
 * @date 2021/12/25 6:05
 **/
public class DemoTests {

    @Test
    public void testIO() throws Exception {

        System.out.println("ssssss");

        int[][] chessArr1 = new int[11][11];
        chessArr1[1][2] = 1;
        chessArr1[2][3] = 2;
        chessArr1[4][5] = 2;
        System.out.println(Arrays.deepToString(chessArr1));
        ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream("D:/map.data"));
        outputStream.writeObject(chessArr1);
        outputStream.close();
    }

    @Test
    public void testIO2() {

        System.out.println("ssssss");
        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream("D:/map.data"))) {
            Object object = inputStream.readObject();
            int[][] chessArr1 = (int[][]) object;
            System.out.println(Arrays.deepToString(chessArr1));
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Test
    public void test00() {

    }

    @Test
    public void testRegex() {
        // 将表达式 转为 集合列表
        String exp = "11+ ((20+3) *4)- 55";
        ArrayList<String> list = new ArrayList<>();

        String rege = "\\d+|[\\+\\-\\*\\/\\(\\)]";
        Pattern pattern = Pattern.compile(rege);
        Matcher m = pattern.matcher(exp);
        while (m.find()) {
            list.add(m.group());
        }
        System.out.println(list);

        int a = 1;
        int b = 2;

        a ^= b;
        b ^= a;
        a ^= b;
        System.out.println(a);
        System.out.println(b);
        HashMap map = new HashMap<String, String>();
        map.get("");
    }


    @Test
    public void testAA() throws InterruptedException {

        for (int i = 0; i < 10; i++) {
            Thread.sleep(1000L);
            System.out.printf("\r当前序号i=%d", i);
        }

    }

    public static void main(String[] args) throws InterruptedException {
        for (int i = 0; i < 10; i++) {
            Thread.sleep(1000L);
            System.out.printf("\r当前序号i=%d", i);
        }
    }
}
