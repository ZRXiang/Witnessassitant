package com.example.phobes.witnessassitant.adpter;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * Created by phobes on 2016/6/30.
 */
public class MainActivityGirdView extends GridView {
    public MainActivityGirdView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MainActivityGirdView(Context context) {
        super(context);
    }

    public MainActivityGirdView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }


}
