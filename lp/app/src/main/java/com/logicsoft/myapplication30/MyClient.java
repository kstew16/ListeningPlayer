package com.logicsoft.myapplication30;

import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

// client


public class MyClient extends Thread{
    Socket sck;
    private InputStream  is;
    private OutputStream os;
    private final String TAG ="CLIENT_SOCKET";
    private String addr ="192.168.0.4";
    private int portNum = 3030;

    public MyClient(){}
    public MyClient(String addr, int portNum){
        this.addr = addr;
        this.portNum = portNum;
    }
    private void connect(String addr, int portNum) throws IOException{

        sck = new Socket();
        sck.connect(new InetSocketAddress(addr, portNum));

        is =  sck.getInputStream();
        os = sck.getOutputStream();
    }

    private int read(byte[] b, int offset, int len)throws IOException{
        int ret = is.read(b,offset, len);
        return ret;
    }

    private void write(byte[]b, int offset, int len) throws IOException{
        os.write(b,offset,len);
    }


    public boolean getFile(String filename, File fileDir){
        final int SIZE = 1<<15;
        try {
            // 1. connect
            connect(addr, portNum);

            // 2. write
            byte[] b = new byte[0];
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                b = filename.getBytes(StandardCharsets.UTF_8);
            }
            write(b, 0, b.length);

            byte[]barr = new byte[SIZE];
            // 3. open file stream
            int ret = 0;
            boolean isFirst = true;
            //OutputStream fos = new FileOutputStream(filename); // TODO
            //BufferedWriter fbw = new BufferedWriter(new FileWriter(fileDir + filename));
            // 5. reading message and write on file
            while(ret>=0){
                if (isFirst){
                    isFirst=false;
                    String temp = new String(barr, 0, ret, "UTF-8");
                    if(temp.startsWith("None")){
                        // Error!
                        //fbw.close(); // TODO
                        return false;
                    }
                }

                if(ret !=0){
                    Log.d("JS",new String(barr,0,ret, "UTF-8") );
                    //fbw.write(barr, 0, ret); // TODO
                    //fbw.write();
                    //fbw.flush(); // TODO
                }
                // System.out.println(new String(barr,0,ret, "UTF-8"));

                ret = read(barr,0,SIZE);
            }

            // 6. close file stream
            //fbw.close(); // TODO
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public void quitServer(){
        try{
            connect(addr, portNum);

            byte[] b = new byte[0];
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                b = "quit".getBytes(StandardCharsets.UTF_8);
            }
            write(b, 0, b.length);
        } catch (IOException e){
            e.printStackTrace();
        }
    }
    public List<String> getList() {
        final int SIZE = 1 << 15;
        StringBuilder sb;
        try {
            // 1. connect
            connect(addr, portNum);

            // 2. write
            byte[] b = new byte[0];
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                b = "list".getBytes(StandardCharsets.UTF_8);
            }
            write(b, 0, b.length);

            byte[] barr = new byte[SIZE];
            // 3. open file stream
            int ret = 0;
            boolean isFirst = true;
            sb = new StringBuilder();

            // 5. reading message and write on file
            while (ret >= 0) {
                if (isFirst) {
                    isFirst = false;
                    String temp = new String(barr, 0, ret, "UTF-8");
                    if (temp.startsWith("None")) {
                        return new ArrayList<String>();
                    }
                }

                if (ret != 0) {
                    String str = new String(barr, 0, ret, "UTF-8");
                    Log.d("JS", str);
                    sb.append(str);
                }

                ret = read(barr, 0, SIZE);
            }

        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<String>();
        }
        StringTokenizer st = new StringTokenizer(sb.toString(), "\n");
        List<String> lst = new ArrayList<>(st.countTokens());
        while (st.hasMoreTokens())
            lst.add(st.nextToken());
        return lst;
    }

/*
    @Override
    public void run() {
        Log.d("JS", "client started");
        boolean ret = getFile("hello.txt","None");
        if (ret){
            Log.d("JS", "SUCCEEDED!");
        }else{
            Log.d("JS", "FAILED!");
        }

    }

 */

}
