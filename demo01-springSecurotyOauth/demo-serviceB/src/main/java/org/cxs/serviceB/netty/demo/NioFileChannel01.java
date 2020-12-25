package org.cxs.serviceB.netty.demo;

import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @author chenxinsui
 * @date 2020/12/18 18:28
 * @description: 示例1，通过channel写数据
 **/
public class NioFileChannel01 {

    public static void main(String[] args) throws Exception {
        String msg = "hello world, see you later";
        // 1.创建输出流
        FileOutputStream fileOutputStream = new FileOutputStream("D:/aaa.txt");
        // 2.创建 channel
        FileChannel fileChannel = fileOutputStream.getChannel();
        // 3.创建 buffer
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        buffer.put(msg.getBytes());
        buffer.flip();
        fileChannel.write(buffer);
        fileOutputStream.close();


    }
}
