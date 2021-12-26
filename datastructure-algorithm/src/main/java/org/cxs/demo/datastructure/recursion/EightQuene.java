package org.cxs.demo.datastructure.recursion;

import java.util.Arrays;

/**
 * note:
 *
 * @author cxs
 * @date 2021/12/26 6:45
 **/
public class EightQuene {

    static int max = 8;
    static int[] arr = new int[max];
    static int count = 0;

    public static void main(String[] args) {
        check(0);
        System.out.printf("一共有%d解法", count);
    }


    public static void check(int n) {
        if (n == max) {
            count++;
            System.out.println(Arrays.toString(arr));
            return;
        }
        for (int i = 0; i < max; i++) {
            arr[n] = i;
            if (judge(n)) {
                check(n + 1);
            }
        }
    }


    public static boolean judge(int n) {
        for (int i = 0; i < n; i++) {
            if (arr[i] == arr[n] || Math.abs(n - i) == Math.abs(arr[n] - arr[i])) {
                return false;
            }
        }
        return true;
    }
}
