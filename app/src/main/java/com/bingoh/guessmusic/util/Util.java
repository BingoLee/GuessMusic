package com.bingoh.guessmusic.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

/**
 * Created by BingoH on 2015/5/16.
 * 获取layoutID的布局
 */
public class Util {
    public static View getView(Context context, int layoutID) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(layoutID, null);
        return layout;
    }
}
