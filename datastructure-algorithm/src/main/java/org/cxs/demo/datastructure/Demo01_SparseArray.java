package org.cxs.demo.datastructure;

/**
 * 数据结构：稀疏数组
 * 二维数组和稀疏数组相互转换 示例
 *
 * @author cxs
 * @date 2021/12/25 6:08
 **/
public class Demo01_SparseArray {

    public static void main(String[] args) {
        // 创建一个原始的二维数组 11 * 11
        // 0: 表示没有棋子， 1 表示 黑子 2 表蓝子
        int chessArr[][] = new int[11][11];
        chessArr[1][2] = 1;
        chessArr[2][3] = 2;
        chessArr[4][5] = 2;

        // 输出原始的二维数组
        System.out.println("原始的二维数组 ------>");
        for (int[] row : chessArr) {
            for (int data : row) {
                System.out.printf("%d\t", data);
            }
            System.out.println();
        }

        int[][] sparseArr = chessArrToSparseArr(chessArr);
        sparseArrToChessArr(sparseArr);
    }

    /**
     * 棋盘数组 转换为 稀疏数组
     *
     * @param chessArr 棋盘数组就是二维数组
     * @return 稀疏数组
     */
    public static int[][] chessArrToSparseArr(int[][] chessArr) {
        // 0、获取棋盘数组 行数和列数
        int rows = chessArr.length;//行数
        int columns = chessArr[0].length;//列数

        // 1、先遍历二维数组 得到非 0 数据的个数
        int sum = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if (chessArr[i][j] != 0) {
                    sum++;
                }
            }
        }

        // 2、创建对应的稀疏数组
        int sparseArr[][] = new int[sum + 1][3];
        // 给稀疏数组赋值
        sparseArr[0][0] = rows;
        sparseArr[0][1] = columns;
        sparseArr[0][2] = sum;
        // 遍历二维数组， 将非 0 的值存放到 sparseArr 中
        int count = 0; //count 用于记录是第几个非 0 数据
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if (chessArr[i][j] != 0) {
                    count++;
                    sparseArr[count][0] = i;
                    sparseArr[count][1] = j;
                    sparseArr[count][2] = chessArr[i][j];
                }
            }
        }
        //输出稀疏数组的形式
        System.out.println();
        System.out.println("得到稀疏数组为~~~~");
        for (int i = 0; i < sparseArr.length; i++) {
            System.out.printf("%d\t%d\t%d\t\n", sparseArr[i][0], sparseArr[i][1], sparseArr[i][2]);
        }
        System.out.println();
        return sparseArr;
    }

    /**
     * 稀疏数组 还原为 二维数组
     *
     * @param sparseArr 稀疏数组
     * @return
     */
    public static int[][] sparseArrToChessArr(int[][] sparseArr) {
        //1. 先读取稀疏数组的第一行， 根据第一行的数据， 创建原始的二维数组
        int chessArr[][] = new int[sparseArr[0][0]][sparseArr[0][1]];
        //2. 在读取稀疏数组后几行的数据(从第二行开始)， 并赋给 原始的二维数组 即可
        for (int i = 1; i < sparseArr.length; i++) {
            chessArr[sparseArr[i][0]][sparseArr[i][1]] = sparseArr[i][2];
        }
        //输出恢复后的二维数组
        System.out.println();
        System.out.println("恢复后的二维数组~~~~");
        for (int[] row : chessArr) {
            for (int data : row) {
                System.out.printf("%d\t", data);
            }
            System.out.println();
        }

        return chessArr;
    }
}
