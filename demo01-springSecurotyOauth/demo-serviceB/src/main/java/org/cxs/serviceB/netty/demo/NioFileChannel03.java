package org.cxs.serviceB.netty.demo;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

/**
 * @author chenxinsui
 * @date 2020/12/18 18:28
 * @description: 示例3，通过 channel 实现文件拷贝
 **/
public class NioFileChannel03 {

    public static void main(String[] args) throws Exception {

        FileInputStream fileInputStream = new FileInputStream("D:/aaa.txt");
        FileOutputStream fileOutputStream = new FileOutputStream("D:/bbb.txt");
        FileChannel inputChannel = fileInputStream.getChannel();
        FileChannel outputChannel = fileOutputStream.getChannel();
        outputChannel.transferFrom(inputChannel,0,inputChannel.size());
        inputChannel.close();
        outputChannel.close();
        fileInputStream.close();
        fileOutputStream.close();
    }
}
