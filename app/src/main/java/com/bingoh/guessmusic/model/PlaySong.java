package com.bingoh.guessmusic.model;

import android.app.Activity;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.View;

import com.bingoh.guessmusic.MusicJson.MyJson;
import com.bingoh.guessmusic.R;

import java.io.IOException;

/**
 * Created by BingoH on 2015/5/18.
 */
public class PlaySong {
    private static MediaPlayer mp3;

    public static  void play(Activity context, String Path, int id) {
        mp3 = new MediaPlayer();
        try {
            mp3.reset();
            mp3.setDataSource(Path);
            mp3.prepare();
            mp3.start();
        } catch (IOException e) {
            MyJson test = new MyJson(context, id+"", null);
            Thread t1 = new Thread(test,"t1");
            t1.start();
        }

    }

    public static void stop() {
        if(mp3!=null) {
            mp3.stop();
        }
    }
}
