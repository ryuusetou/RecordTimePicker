package com.hongdian.recordtimepicker;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import static android.content.ContentValues.TAG;

/**
 * Created by ryuusetou on 2016/10/12.
 */

public class DialLayout extends ViewGroup {

    private float trackRadius;

    public DialLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.DialLayout);
        trackRadius = a.getDimension(R.styleable.DialLayout_trackRadius, 0);
        a.recycle();
    }

    public void setClockPadValue(int index, String value){
        if (index >= getChildCount())
            return;

        ((TextView)getChildAt(index)).setText(value);
    }

    public void setClockPadVisibility(int index, int visibility) {
        if (index >= getChildCount())
            return;

        getChildAt(index).setVisibility(visibility);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int measureWidth = MeasureSpec.getSize(widthMeasureSpec);
        int measureHeight = MeasureSpec.getSize(heightMeasureSpec);

        setMeasuredDimension(measureWidth, measureHeight);

        for(int i = 0;i < getChildCount();i++){
            int widthSpec;
            int heightSpec;

            View v = getChildAt(i);
            widthSpec = v.getMeasuredWidth();
            heightSpec = v.getMeasuredHeight();
            v.measure(widthSpec, heightSpec);
        }
    }

    @Override
    protected void onLayout(boolean changed, int i, int i1, int i2, int i3) {
        View v;

        double angleArc;

        int l,t,r,b,layoutWidth,layoutHeight,childWidth,childHeight,centreX,centreY,childCount;

        childCount = getChildCount();
        layoutWidth = getMeasuredWidth();
        layoutHeight = getMeasuredHeight();

        for (int index = 0; index < childCount; index++) {
            v = getChildAt(index);

            childWidth = v.getMeasuredWidth();
            childHeight = v.getMeasuredHeight();

            angleArc = 2 * Math.PI * index / childCount;

            centreX = (int)(layoutWidth / 2 + Math.sin(angleArc) * (double)trackRadius);
            centreY = (int)(layoutHeight / 2 - Math.cos(angleArc) * (double)trackRadius);

            l = centreX - childWidth / 2;
            t = centreY - childHeight / 2;
            r = centreX + childWidth / 2;
            b = centreY + childHeight / 2;

            v.layout(l, t, r, b);
        }
    }
}
