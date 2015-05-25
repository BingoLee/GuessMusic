package com.bingoh.guessmusic.MusicJson;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by BingoH on 2015/5/18.
 */
public class HttpConnect implements Runnable {

    private String id;
    private String url;
    private String name;
    private Context context;
    private Handler handler;

    public HttpConnect(Context context, String id, String url, String name,Handler handler) {
        this.context = context;
        this.id = id;
        this.url = url;
        this.name = name;
        this.handler = handler;
    }

    @Override
    public void run() {
        try {
            String Path = Environment.getExternalStorageDirectory() + "/" + id + ".mp3";
            File download_file = new File(Path);

            if(download_file.exists()) {
                return;
            }else {
                URL httpURL = new URL(url);
                HttpURLConnection conn = (HttpURLConnection) httpURL.openConnection();

                //设置请求超时检验
                conn.setReadTimeout(5000);
                conn.setRequestMethod("GET");
                conn.connect();
                InputStream input = conn.getInputStream();
                FileOutputStream output = new FileOutputStream(download_file);

                int length;
                byte[] buffer = new byte[2 * 1024];

                while ((length = input.read(buffer)) != -1) {
                    output.write(buffer, 0, length);
                }

                input.close();
                output.flush();
                output.close();


            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //发送下载完成代码
            handler.sendEmptyMessage(1);
        }
    }
}
