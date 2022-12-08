package com.logicsoft.myapplication30;
// author: Jaesun Park (Univ. of Seoul)
import androidx.annotation.RequiresApi;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

// client

public class MyClient extends Thread{
    // author: Jaesun Park (Univ. of Seoul)
    Socket sck;
    private InputStream  is;
    private OutputStream os;
    private final String TAG ="CLIENT_SOCKET";
    private String addr ="localhost";
    private int portNum = 3030;

    public MyClient(){}
    public MyClient(String addr, int portNum){
        this.addr = addr;
        this.portNum = portNum;
    }
    public void connect(String addr, int portNum) throws IOException{

        sck = new Socket();
        sck.connect(new InetSocketAddress(addr, portNum));

        is =  sck.getInputStream();
        os = sck.getOutputStream();
    }

    public int read(byte[] b, int offset, int len)throws IOException{
        int ret = is.read(b,offset, len);
        return ret;
    }

    public void write(byte[]b, int offset, int len) throws IOException{
        os.write(b,offset,len);
    }

    public boolean getFile(String filename){
        final int SIZE = 1<<15;
        try {
            connect(addr, portNum);
            byte[] b = new byte[0];
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                b = filename.getBytes(StandardCharsets.UTF_8);
            }
            write(b, 0, b.length);

            byte[]barr = new byte[SIZE];

            int ret = 1;
            boolean isFirst = true;
            OutputStream fos = new FileOutputStream(filename);
            while(true){
                ret = read(barr,0,SIZE);

                if (ret<=0) break;
                if (isFirst){
                    isFirst=false;
                    String temp = new String(barr, 0, ret, "UTF-8");
                    if(temp.startsWith("None")){
                        // Error!
                        fos.close();
                        return false;
                    }
                }
                fos.write(barr, 0, ret);

                fos.flush();
                // System.out.println(new String(barr,0,ret, "UTF-8"));
            }
            fos.close();

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    @Override
    public void run(){
        try{
            final int SIZE = 1024;

            byte[] barr  =new byte[SIZE];
            int i=0;

            while(true){
                // 1. connect
                connect(addr, portNum);
                System.out.println(TAG+"socket connected: "+addr +":"+portNum);
                // Log.d(TAG,"socket connected: "+addr +":"+portNum);

                // 2. make a message to send
                Arrays.fill(barr, (byte)0);
                byte[] temp_msg = new byte[0];
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                    temp_msg = (""+(++i)).getBytes(StandardCharsets.UTF_8);
                }
                for(int x=0; x<temp_msg.length; x++){
                    barr[x] = temp_msg[x];
                }

                // 3. send
                write(barr,0,SIZE);

                // 4. receive
                int ret = 1;
                while(true){
                    ret = read(barr,0,SIZE);
                    if (ret<=0) break;
                    String msg = new String(barr, 0, ret, "UTF-8");
                    System.out.println(TAG+" received: "+ msg);
                }

                // 5. close
                sck.close();

                Thread.sleep(1000);
                if(i==10) break;
            }

        }catch (IOException e){
            e.printStackTrace();
        } catch (InterruptedException e){
            e.printStackTrace();
        }


    }

}
