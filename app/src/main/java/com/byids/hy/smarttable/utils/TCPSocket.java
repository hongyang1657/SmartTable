package com.byids.hy.smarttable.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * 基于TCP的Socket通信
 *
 * Created by hy on 2016/6/22.
 */
public class TCPSocket {
    public static final int DEFAULT_PORT = 57816;
    public static final String IP_ADDRESS = "";

    public void doTCPSocket(){
        try {
            //1.创建客户端Socket，指定服务器地址和端口
            Socket socket = new Socket(IP_ADDRESS,DEFAULT_PORT);
            //2.获取输出流，向服务器端发送消息
            OutputStream os = socket.getOutputStream();//字节输出流
            PrintWriter pw = new PrintWriter(os);//将输出流包装为打印流
            pw.write("用户名：admin；密码：123");
            pw.flush();//刷新缓存
            socket.shutdownOutput();//关闭输出流
            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
