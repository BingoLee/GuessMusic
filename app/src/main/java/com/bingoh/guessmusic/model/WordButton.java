package com.bingoh.guessmusic.model;

import android.widget.Button;

/**
 * Created by BingoH on 2015/5/16.
 */
public class WordButton {
    public int mIndex;
    public boolean mIsVisiable;
    public String mWordString;
    public Button mViewButton;

    public WordButton() {
        mIsVisiable = true;
        mWordString = "";
    }
}
