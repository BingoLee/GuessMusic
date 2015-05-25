package com.bingoh.guessmusic.UI;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.bingoh.guessmusic.MusicJson.MyJson;
import com.bingoh.guessmusic.MyUI.MyGridView;
import com.bingoh.guessmusic.R;
import com.bingoh.guessmusic.model.IWordButtonClickListener;
import com.bingoh.guessmusic.model.PlaySong;
import com.bingoh.guessmusic.model.Song;
import com.bingoh.guessmusic.model.WordButton;
import com.bingoh.guessmusic.util.Util;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Random;


public class MainActivity extends Activity implements IWordButtonClickListener {
    //盘片
    private Animation mPanAnim;
    private LinearInterpolator mPanLin;
    //开始播杆
    private Animation mBarInAnim;
    private LinearInterpolator mBarInLin;
    //结束播杆
    private Animation mBarOutAnim;
    private LinearInterpolator mBarOutLin;
    //盘片图片和播杆图片
    private ImageView mViewPan;
    private ImageView mViewPanBar;
    //开始按钮
    private ImageButton mBtnPlayStart;
    //播放进行旗帜
    private boolean mIsRunning = false;
    //供选择的24个文字数组
    private ArrayList<WordButton> mWordList;
    //待选择文字框数组
    private ArrayList<WordButton> mSelectWordList;
    //自定义GridView
    private MyGridView mMyGridView;
    //用于加载待选文字框的布局
    private LinearLayout mLinearWord;
    //当前加载歌曲
    private Song mCurrentSong;
    //当前歌曲的关数
    private int mSongIndex = 0;
    //已选择的文字个数
    private int mSelectSum = 0;
    //存储所有随机生成的文字
    private String[] mStr;
    //结束按钮
    private ImageButton mEndButton;
    //开始游戏让歌曲进行缓冲
    private ProgressDialog mProDia;
    //歌曲播放地址前缀
    private static String Path = Environment.getExternalStorageDirectory()+"";

    public Handler mHandler;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == 1) {
                    mProDia.dismiss();
                    initWordData();
                }
                super.handleMessage(msg);
            }
        };



        //下载第一首歌曲
        down();
        mProDia = ProgressDialog.show(this, "提示", "正在下载歌曲");

        mMyGridView = (MyGridView) findViewById(R.id.gridview);
        mLinearWord = (LinearLayout) findViewById(R.id.word_select_container);
        mEndButton = (ImageButton) findViewById(R.id.btn_bar_back);

        //注册监听器
        mMyGridView.registOnWordButtonClick(this);

        //初始化盘片动画
        mPanAnim = AnimationUtils.loadAnimation(this, R.anim.rotate_pan);
        mPanLin = new LinearInterpolator();
        mPanAnim.setInterpolator(mPanLin);
        mPanAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mViewPanBar.startAnimation(mBarOutAnim);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        //初始化播杆开始动画
        mBarInAnim = AnimationUtils.loadAnimation(this, R.anim.rotate_in);
        mBarInLin = new LinearInterpolator();
        mBarInAnim.setFillAfter(true);
        mBarInAnim.setInterpolator(mBarInLin);
        mBarInAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mViewPan.startAnimation(mPanAnim);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        //初始化播杆结束动画
        mBarOutAnim = AnimationUtils.loadAnimation(this, R.anim.rotate_out);
        mBarOutLin = new LinearInterpolator();
        mBarOutAnim.setFillAfter(true);
        mBarOutAnim.setInterpolator(mBarOutLin);
        mBarOutAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mIsRunning = false;
                mBtnPlayStart.setVisibility(View.VISIBLE);
                PlaySong.stop();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        mViewPan = (ImageView) findViewById(R.id.imageView1);
        mViewPanBar = (ImageView) findViewById(R.id.imageView2);
        mBtnPlayStart = (ImageButton) findViewById(R.id.play_button_start);

        /**
         * 播放按钮的监听事件
         */
        mBtnPlayStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handlePlayButton();
            }
        });

        /**
         * 退出按钮的监听事件
         */
        mEndButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.this.finish();
            }
        });

//        initWordData();

    }

    /**
     * 播杆开始动画
     */
    private void handlePlayButton() {
        if(mViewPanBar!=null) {
            if (!mIsRunning) {
                PlaySong.play(this,Path+"/"+mSongIndex+".mp3",mSongIndex);
                mIsRunning = true;
                mViewPanBar.startAnimation(mBarInAnim);
                mBtnPlayStart.setVisibility(View.INVISIBLE);

            }
        }
    }

    /**
     * 退出结束动画
     */
    @Override
    protected void onPause() {
        mViewPan.clearAnimation();
        mViewPanBar.clearAnimation();
        PlaySong.stop();
        super.onPause();
    }

    /**
     * 返回当前关信息
     * @param index
     * @return
     */
    private Song CurrentSongInfo(int index) {
        Song song = new Song();

        song.setSongName(MyJson.name);

        return song;
    }

    /**
     * 调用GridView的updateData()
     */
    public void initWordData() {
        //获取当前关歌曲信息
        //mCurrentSong = CurrentSongInfo(++mSongIndex);
        mCurrentSong = CurrentSongInfo(mSongIndex);

        mWordList = initWordArray();
        //设置待选文字框
        mSelectWordList = initWordSelect();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(140,140);
        mLinearWord.removeAllViews();
        //动态更新文字框
        for(int i = 0;i<mSelectWordList.size();i++) {
            mLinearWord.addView(mSelectWordList.get(i).mViewButton,params);
        }
        //更新文字
        mMyGridView.updateData(mWordList);
    }

    /**
     * 初始化文字ArrayList
     * @return
     */
    private ArrayList<WordButton> initWordArray() {
        ArrayList<WordButton> data = new ArrayList<WordButton>();

        mStr = getAllCH();

        for(int i = 0;i<24;i++) {
            WordButton button = new WordButton();
            button.mWordString = mStr[i];
            data.add(button);
        }
        return data;
    }

    /**
     * 初始化已选择文字框
     * @return
     */
    private ArrayList<WordButton> initWordSelect() {
        ArrayList<WordButton> data = new ArrayList<WordButton>();
        for(int i = 0;i<mCurrentSong.getNameLength();i++) {
            View v = Util.getView(MainActivity.this,R.layout.gridview_item);
            WordButton holder = new WordButton();
            holder.mViewButton = (android.widget.Button) v.findViewById(R.id.item_btn);
            holder.mViewButton.setTextColor(Color.WHITE);
            holder.mViewButton.setText("");
            holder.mIsVisiable = false;
            holder.mViewButton.setBackgroundResource(R.drawable.game_wordblank);
            data.add(holder);
        }
        return data;
    }

    /**
     * 初始化24个随机字符
     * @return
     */
    private String[] getAllCH() {
        String[] ch = new String[24];
        //插入歌曲姓名
        for(int i = 0;i < mCurrentSong.getNameLength();i++) {
            ch[i] = mCurrentSong.getNameCharacters()[i] + "";
        }
        //插入干扰中文
        for(int i = mCurrentSong.getNameLength();i < 24;i++) {
            ch[i] = getRandomCH() + "";
        }

        for(int i = 0;i < 5;i++) {
            Random random = new Random();
            int x = Math.abs(random.nextInt(24));
            String temp;
            temp = ch[x];
            ch[x] = ch[i];
            ch[i] = temp;
        }

        return ch;
    }

    /**
     * 获取一个随机字符
     * @return
     */
    private char getRandomCH() {
        String str = "";
        int highPos;
        int lowPos;

        Random random = new Random();

        highPos = (176+Math.abs(random.nextInt(39)));
        lowPos = (161+Math.abs(random.nextInt(93)));

        byte[] ch = new byte[2];
        ch[0] = (Integer.valueOf(highPos)).byteValue();
        ch[1] = (Integer.valueOf(lowPos)).byteValue();

        try {
            str = new String(ch, "GBK");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return str.charAt(0);
    }


    /**
     * getView()中为每一个按钮注册的监听事件
     * @param wordButton
     */
    @Override
    public void onWordButtonClick(WordButton wordButton) {
        if(mSelectSum<mSelectWordList.size()) {
            mSelectWordList.get(mSelectSum).mViewButton.setText(mStr[wordButton.mIndex]);
            mSelectSum++;

//            if(mSelectSum==1) {//按下答案自动开始下载下一首音乐
//                down();
//            }
            if(mSelectSum==mSelectWordList.size()) {
                for(int i = 0;i<mSelectWordList.size();i++) {//判断答案
                    String temp = mSelectWordList.get(i).mViewButton.getText().toString();
                    String answer = new String(mCurrentSong.getNameCharacters());
                    answer = answer.substring(i,i+1);

                    if(temp.equals(answer)) {//如果当前字符与答案相对字符匹配
                        if(i==mSelectWordList.size()-1) {
                            if(mSongIndex+1==12) {//暂时只有12关
                                Toast.makeText(MainActivity.this,"已经通关了，请等待更新",Toast.LENGTH_SHORT).show();
                            }else {
                                mProDia.show();
                                down();
                                mSongIndex++;
                                mSelectSum = 0;
                                mViewPan.clearAnimation();
                                mViewPanBar.clearAnimation();
                                //重新初始化供选择的24个文字和待选文字框个数
//                                initWordData();
                            }
                        }
                        continue;
                    } else {
                        for(int j = 0;j<mSelectWordList.size();j++) {//清空已选文字框
                            mSelectWordList.get(j).mViewButton.setText("");
                            mSelectSum = 0;
                        }
                        break;
                    }

                }
            }
        }
    }

    /**
     * 下载歌曲
     */
    private void down() {

        if(mSongIndex == 0) {
            ++mSongIndex;
            MyJson mj = new MyJson(this, mSongIndex+"", mHandler);
            Thread mThread = new Thread(mj, "t1");
            mThread.start();
        }else {
            int index = mSongIndex + 1;
            MyJson mj = new MyJson(this, index + "", mHandler);
            Thread mThread = new Thread(mj, "t1");
            mThread.start();
        }
    }

    public ProgressDialog getProDia() {
        return mProDia;
    }
}
