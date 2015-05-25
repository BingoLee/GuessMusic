package com.bingoh.guessmusic.MyUI;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;

import com.bingoh.guessmusic.R;
import com.bingoh.guessmusic.model.IWordButtonClickListener;
import com.bingoh.guessmusic.model.WordButton;
import com.bingoh.guessmusic.util.Util;

import java.util.ArrayList;

/**
 * Created by BingoH on 2015/5/16.
 */
public class MyGridView extends GridView {
    private ArrayList<WordButton> mArrayList = new ArrayList<WordButton>();
    private MyGridAdapter mAdapter;
    private Context mContext;
    private Animation mScaleAnim;
    private IWordButtonClickListener mWordButtonListener;

    public MyGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        this.mAdapter = new MyGridAdapter();
        this.setAdapter(mAdapter);
    }

    /**
     * 更新MyGridView的数据
     * @param list 包含24个按钮的数组
     */
    public void updateData(ArrayList<WordButton> list) {
        mArrayList = list;
        setAdapter(mAdapter);
    }

    /**
     * 自定义Adapter
     */
    class MyGridAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mArrayList.size();
        }

        @Override
        public Object getItem(int position) {
            return mArrayList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View v, ViewGroup parent) {
            final WordButton holder;
            if(v==null) {
                v = Util.getView(mContext, R.layout.gridview_item);
                holder = mArrayList.get(position);

                mScaleAnim = AnimationUtils.loadAnimation(mContext, R.anim.scale);
                mScaleAnim.setStartOffset(position*100);


                holder.mIndex = position;
                holder.mViewButton = (Button) v.findViewById(R.id.item_btn);

                //注册每一个按钮的监听事件
                holder.mViewButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mWordButtonListener.onWordButtonClick(holder);
                    }
                });

                v.setTag(holder);
            }else {
                holder = (WordButton) v.getTag();
            }

            v.startAnimation(mScaleAnim);

            //holder按钮设置文字
            holder.mViewButton.setText(holder.mWordString);
            return v;
        }
    }

    //引入监听事件
    public void registOnWordButtonClick(IWordButtonClickListener listener) {
        mWordButtonListener = listener;
    }
}
