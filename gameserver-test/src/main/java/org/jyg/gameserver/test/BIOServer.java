package org.jyg.gameserver.test;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * create by jiayaoguang on 2020/10/18
 */
public class BIOServer {

    public static void main(String[] args) throws Exception {

        System.out.println("等待连接");
        ServerSocket ss=new ServerSocket(8081);
        Socket s=ss.accept();

        InputStream inputStream = s.getInputStream();
        byte[] bytes = new byte[2048];

        inputStream.read(bytes);

        System.out.println(new String(bytes));

        OutputStream os =s.getOutputStream();


        os.write("hello world".getBytes());
        os.close();
        s.close();



    }

}
