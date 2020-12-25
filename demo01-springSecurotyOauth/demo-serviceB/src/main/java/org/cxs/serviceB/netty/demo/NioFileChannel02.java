package org.cxs.serviceB.netty.demo;

import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @author chenxinsui
 * @date 2020/12/18 18:28
 * @description: 示例2，通过channel读数据
 **/
public class NioFileChannel02 {

    public static void main(String[] args) throws Exception {
        File file = new File("D:/aaa.txt");
        FileInputStream fileInputStream = new FileInputStream(file);
        FileChannel fileChannel = fileInputStream.getChannel();
        ByteBuffer buffer = ByteBuffer.allocate((int) file.length());

        fileChannel.read(buffer);
        System.out.println(new String(buffer.array()));
        fileInputStream.close();

    }
}
