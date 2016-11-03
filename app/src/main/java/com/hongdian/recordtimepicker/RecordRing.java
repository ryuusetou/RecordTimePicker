package com.hongdian.recordtimepicker;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;

import java.util.LinkedList;
import java.util.List;


/**
 * Created by ryuus on 2016/10/8 0008.
 */

public class RecordRing extends View {
    protected float mInnerRadius;

    protected float mOuterRadius;

    protected Paint mPaint;

    protected RectF mOval;

    private int mPaintColour;

    protected List<TimeFrag> units;

    public RecordRing(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RecordRing);
        mOuterRadius = a.getDimension(R.styleable.RecordRing_outerRadius, 0);
        mInnerRadius = a.getDimension(R.styleable.RecordRing_innerRadius, 0);
        mPaintColour = a.getColor(R.styleable.RecordRing_ringColor, Color.BLUE);
        a.recycle();

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(mPaintColour);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mOuterRadius - mInnerRadius);

        mOval = new RectF();
        mOval.left = (mOuterRadius - mInnerRadius) / 2;
        mOval.top = (mOuterRadius - mInnerRadius) / 2;
        mOval.right = (3.0f * mOuterRadius + mInnerRadius) / 2;
        mOval.bottom = (3.0f * mOuterRadius + mInnerRadius) / 2;

    }

    public void update(List<TimeFrag> units, Animation animation) {
        this.units = units;

        setVisibility(VISIBLE);
        invalidate();

        if (animation != null)
            startAnimation(animation);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension((int)(mOuterRadius * 2), (int)(mOuterRadius * 2));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float startAngle = 0;
        float sweepAngle = 0;

//        super.onDraw(canvas);

        if (units != null) {
//            Log.d("RING", "START");
            for (TimeFrag u : units) {

                startAngle = ((float) u.start / (float) u.sum) * 360.0f - 90;
                sweepAngle = ((float) (u.end - u.start) / (float) u.sum) * 360.0f;

//                Log.d("RING", "DRAW start from" + startAngle + " sweep " + sweepAngle);

                canvas.drawArc(mOval, startAngle, sweepAngle, false, mPaint);
            }
//            Log.d("RING", "END");
        }
    }

}
