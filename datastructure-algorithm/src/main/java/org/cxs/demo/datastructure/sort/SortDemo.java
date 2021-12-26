package org.cxs.demo.datastructure.sort;

import java.util.Random;

/**
 * note:
 *
 * @author cxs
 * @date 2021/12/26 8:16
 **/
public class SortDemo {

    public static void main(String[] args) {

        int[] arr = new Random().ints(5000000, 0, 5000000).toArray();
        long start = System.currentTimeMillis();
        //System.out.println(Arrays.toString(arr));
        // 5W 数据 效率比较
        //bubbleSort(arr); // 6000 ms
        //selectSort(arr);// 6000 ms
        //insertSort(arr);// 1200 ms
        shellSort(arr);  // 11 ms
        //quickSort(arr, 0, arr.length - 1); // 11 ms
        long end = System.currentTimeMillis();
        System.out.printf("排序花费时间：%d 毫秒\n", (end - start));


        //System.out.println(Arrays.toString(arr));
    }

    /**
     * <p>冒泡排序（ Bubble Sorting） 的基本思想：</p>
     * 通过对待排序序列从前向后（ 从下标较小的元素开始） ,依次比较相邻元素的值， 若发现逆序则交换，
     * 使值较大的元素逐渐从前移向后部， 就象水底下的气泡一样逐渐向上冒。
     *
     * @param arr
     */
    public static void bubbleSort(int[] arr) {
        for (int i = 0; i < arr.length - 1; i++) {
            for (int j = 0; j < arr.length - 1 - i; j++) {
                if (arr[j] > arr[j + 1]) {
                    arr[j + 1] ^= arr[j];
                    arr[j] ^= arr[j + 1];
                    arr[j + 1] ^= arr[j];
                }
            }
        }
    }

    /**
     * <p>选择排序（ select sorting）的基本思想：</p>
     * 第一次从 arr[0]~arr[n-1]中选取最小值，与 arr[0]交换，
     * 第二次从 arr[1]~arr[n-1]中选取最小值， 与 arr[1]交换，
     * 第三次从 arr[2]~arr[n-1]中选取最小值， 与 arr[2]交换， …，
     * 第 i 次从 arr[i-1]~arr[n-1]中选取最小值， 与 arr[i-1]交换， …,
     * 第 n-1 次从 arr[n-2]~arr[n-1]中选取最小值，与 arr[n-2]交换，
     * 总共通过 n-1 次， 得到一个按排序码从小到大排列的有序序列。
     *
     * @param arr
     */
    public static void selectSort(int[] arr) {
        for (int i = 0; i < arr.length - 1; i++) {
            for (int j = i + 1; j < arr.length; j++) {
                if (arr[i] > arr[j]) {
                    arr[i] ^= arr[j];
                    arr[j] ^= arr[i];
                    arr[i] ^= arr[j];
                }
            }
        }
    }

    /**
     * <p>插入排序（ Insertion Sorting） 的基本思想：</p>
     * 把 n 个待排序的元素看成为一个有序表和一个无序表， 开始时有
     * 序表中只包含一个元素， 无序表中包含有 n-1 个元素， 排序过程中每次从无序表中取出第一个元素， 把它的排
     * 序码依次与有序表元素的排序码进行比较， 将它插入到有序表中的适当位置， 使之成为新的有序表。
     * <p>
     * 假定前n-1个数已经排好序，现在将第n个数插到前面的有序数列中，
     * 使得这n个数也是排好顺序的。如此反复循环，直到全部排好顺序。
     *
     * @param arr
     */
    public static void insertSort(int[] arr) {
        for (int i = 1; i < arr.length; i++) {

            // 方式1： 移动法
            /*int insetVal = arr[i];
            int j = i - 1;
            for (; j >= 0 && insetVal < arr[j]; j--) {
                arr[j + 1] = arr[j];
            }
            if (j + 1 != i) {
                arr[j + 1] = insetVal;
            }*/

            // 方式2： 交换法 + break
            for (int j = i - 1; j >= 0; j--) {
                if (arr[j + 1] < arr[j]) {
                    arr[j + 1] ^= arr[j];
                    arr[j] ^= arr[j + 1];
                    arr[j + 1] ^= arr[j];
                } else {
                    break;
                }
            }
        }
    }

    /**
     * 希尔排序
     * <b>基本思想：使用希尔增量+插入排序
     *
     * @param arr
     */
    public static void shellSort(int[] arr) {

        for (int gap = arr.length / 2; gap > 0; gap /= 2) {
            for (int i = gap; i < arr.length; i++) {
                for (int j = i - gap; j >= 0; j -= gap) {
                    if (arr[j + gap] < arr[j]) {
                        arr[j + gap] ^= arr[j];
                        arr[j] ^= arr[j + gap];
                        arr[j + gap] ^= arr[j];
                    } else {
                        break;
                    }
                }
            }
        }


    }

    /**
     * 快速排序
     * 关键点：给定一组数列，随机抽取一个数pivot作为中间数，要求中间数左边的数小于中间数，中间数右边的数大于中间数
     *
     * @param arr
     * @param left
     * @param right
     */
    public static void quickSort(int[] arr, int left, int right) {
        if (left >= right) return;
        int l = left;
        int r = right;
        int pivot = arr[right];
        while (l < r) {
            while (arr[l] < pivot) l++;// 从右找到大于pivot 的 索引值，没有就 ++
            while (arr[r] > pivot) r--;// 从左找到小于pivot 的 索引值，没有就 ++

            if (l < r) {// 当进行最后一次循环时，l > r，防止交换
                // 交换
                if (arr[l] != arr[r]) { // 值相等时，异或的值为0，交换出错
                    arr[l] ^= arr[r];
                    arr[r] ^= arr[l];
                    arr[l] ^= arr[r];
                }
                //System.out.printf("%s %d %d %d\n", Arrays.toString(arr), l, r, pivot);
                if (arr[l] == pivot && arr[r] == pivot) { // 当pivot重复时候，防止无限循环
                    l++;
                }
            }
        }
        quickSort(arr, left, l - 1);
        quickSort(arr, l + 1, right);

        //System.out.printf("l=%d,r=%d \n", l, r);
    }

    /**
     * <h3>快速排序</h3>
     * <b>基本思想：先从数列中取出一个数作为key值；
     * 将比这个数小的数全部放在它的左边，
     * 大于或等于它的数全部放在它的右边；
     * 对左右两个小数列重复第二步，直至各区间只有1个数。
     * 挖坑填数+分治
     */
    public static void quickSort2(int[] data, int start, int end) {
        if (data == null || start >= end)
            return;
        int i = start, j = end;
        int pivotKey = data[start];
        while (i < j) {
            while (i < j && data[j] >= pivotKey)// 从右向左找第一个小于key的值
                j--;
            // 挖坑填数
            if (i < j)
                data[i++] = data[j];

            while (i < j && data[i] <= pivotKey)// 从左向右找第一个大于key的值
                i++;
            // 挖坑填数
            if (i < j)
                data[j--] = data[i];
        }
        // 循环退出时，i等于j，将第首次的中位数填到这个坑中。
        data[i] = pivotKey;
        // 分治
        quickSort(data, start, i - 1);
        quickSort(data, i + 1, end);
    }


}
