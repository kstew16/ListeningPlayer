package com.logicsoft.myapplication30;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

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
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;


// client


public class MyClient {
    Socket sck;
    private InputStream is;
    private OutputStream os;
    private final String TAG = "CLIENT_SOCKET";
    private String addr = "192.168.0.4";
    private int portNum = 3030;

    public MyClient() {
    }

    public MyClient(String addr, int portNum) {
        this.addr = addr;
        this.portNum = portNum;
    }

    private void connect(String addr, int portNum) throws IOException {

        sck = new Socket();
        sck.connect(new InetSocketAddress(addr, portNum));

        is = sck.getInputStream();
        os = sck.getOutputStream();
    }

    private int read(byte[] b, int offset, int len) throws IOException {
        int ret = is.read(b, offset, len);
        return ret;
    }

    private void write(byte[] b, int offset, int len) throws IOException {
        os.write(b, offset, len);
    }


    public boolean getFile(String filename, FileOutputStream fos) {

        final int SIZE = 1 << 15;
        byte[] barr = new byte[SIZE];

        try {
            // 1. connect
            connect(addr, portNum);

            // 2. write
            byte[] b = new byte[0];
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                b = filename.getBytes(StandardCharsets.UTF_8);
            }
            write(b, 0, b.length);


            // 3. open file stream
            int ret = 0;
            boolean isFirst = true;

            // 5. reading message and write on file
            Log.d("JS", "get file thread");
            while (true) {
                ret = read(barr, 0, SIZE);
                if (ret < 0) break;
                if (ret == 0) continue;

//                Log.d("JS",new String(barr,0,ret, "UTF-8") );
                if (isFirst) {
                    isFirst = false;
                    String temp = new String(barr, 0, ret, "UTF-8");
                    if (temp.startsWith("None")) {
                        return false;
                    }
                }
                fos.write(barr, 0, ret);
                fos.flush();

            }

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public void quitServer() {
        try {
            connect(addr, portNum);

            byte[] b = new byte[0];
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                b = "quit".getBytes(StandardCharsets.UTF_8);
            }
            write(b, 0, b.length);
        } catch (IOException e) {
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
            while (true) {
                ret = read(barr, 0, SIZE);
                if (ret == -1) break;
                if (ret != 0) {
                    if (isFirst) {
                        isFirst = false;
                        String temp = new String(barr, 0, ret, "UTF-8");
                        if (temp.startsWith("None")) {
                            return new ArrayList<String>();
                        }
                    }
                    String str = new String(barr, 0, ret, "UTF-8");
//                    Log.d("JS", str);
                    sb.append(str);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<String>();
        }
        StringTokenizer st = new StringTokenizer(sb.toString(), "\n");
        HashSet<String> set = new HashSet<>(st.countTokens());
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            int dot = token.lastIndexOf('.');
            set.add(token.substring(0, dot));
        }
        return new ArrayList<>(set);
    }


}

class GetListThread extends Thread {

    MyClient serverManager;
    List<String> itemList;
    Context mainact;

    public GetListThread(MyClient serverMng, List<String> itemList, Context context) {
        this.serverManager = serverMng;
        this.itemList = itemList;
        mainact = context;
    }

    @Override
    public void run() {
        Log.d("JS", "get list thread run");
        itemList.clear();
        List<String> returnList = serverManager.getList();
        if (returnList.size() == 0) {
            itemList.add("None");
            Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mainact, "There are no files to get on the server.", Toast.LENGTH_SHORT).show();
                }
            }, 0);
        }
        for (int i = 0; i < returnList.size(); i++) {
            itemList.add(returnList.get(i));
        }
    }
}