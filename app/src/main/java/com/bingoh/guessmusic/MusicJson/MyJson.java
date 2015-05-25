package com.bingoh.guessmusic.MusicJson;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.bingoh.guessmusic.UI.MainActivity;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Created by BingoH on 2015/5/18.
 */
public class MyJson implements Runnable {

    private static Context context;
    private static String id;
    private static String url;
    public static String name;
    private static Handler mHandler;

    public MyJson(Context context1, String id1, Handler handler) {
        id = id1;
        context = context1;
        this.mHandler = handler;
    }

    public static void DowloadMusic() {
        StartDownLoad();
    }

    /**
     * 获取服务器JSON
     * @return
     */
    public static boolean getJson() {

        HttpClient client = new DefaultHttpClient();
        //网址和响应的get信息
        HttpGet InfoGet = new HttpGet("http://yangserve.sinaapp.com/index.php/Home/Download/download.html?id="+id);

        StringBuilder builder = new StringBuilder();

        try {
            //获得响应信息
            HttpResponse response = client.execute(InfoGet);
            //获得响应信息的输入流
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    response.getEntity().getContent()));
            //读取输入流信息
            for (String s = reader.readLine(); s != null; s = reader.readLine()) {
                builder.append(s);
            }

            //将输入流信息转换成JSON
            JSONObject jsonObject = new JSONObject(builder.toString());

            id = jsonObject.getString("id");
            url = jsonObject.getString("url");
            name = jsonObject.getString("sname");

        } catch (Exception e) {
            Log.v("url response", "false");
            e.printStackTrace();
        }

        return true;
    }

    /**
     * 将获取到的JSON的信息传入HttpConnect进行下载
     */
    public static void StartDownLoad() {

        getJson();
        HttpConnect connect = new HttpConnect(context, id, url, name, mHandler);
        Thread t1 = new Thread(connect, "t1");
        t1.start();
    }

    @Override
    public void run() {
        DowloadMusic();
    }
}
