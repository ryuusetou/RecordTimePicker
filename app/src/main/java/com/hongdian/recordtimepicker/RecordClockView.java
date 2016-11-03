package com.hongdian.recordtimepicker;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by ryuusetou on 2016/10/10.
 */

public class RecordClockView extends View {
    protected ClockChangeListener mClockListener;

    protected RecordClockLayoutState mCurrentState;

    protected float mTrackRadius;

    protected float mMarginToEdge;

    protected float mOvalRadius;

    protected Paint mPointerPaint;

    protected RectF mOvalRectF;
    protected Paint mOvalPaint;

    protected Paint mIndicatorPaint;

    protected double mAngle;

    protected boolean mShowIndicator;

    protected RetreatListener retreatListener;

    public RecordClockView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RecordClockView);
        mMarginToEdge = a.getDimension(R.styleable.RecordClockView_marginToEdge, 0.0f);
        mOvalRadius = a.getDimension(R.styleable.RecordClockView_pointBallRadius, 0.0f);
        a.recycle();

        mOvalRectF = new RectF();

        mPointerPaint = new Paint();
        mPointerPaint.setAntiAlias(true);
        mPointerPaint.setStrokeWidth(4);
        mPointerPaint.setColor(getResources().getColor(R.color.colorIndicate));

        mOvalPaint = new Paint();
        mOvalPaint.setAntiAlias(true);
        mOvalPaint.setColor(getResources().getColor(R.color.colorIndicate));

        mIndicatorPaint = new Paint();
        mIndicatorPaint.setAntiAlias(true);
        mIndicatorPaint.setStrokeWidth(4);
        mIndicatorPaint.setColor(Color.WHITE);

        retreatListener = new RetreatListener();
    }


    public void drawPointer(Canvas canvas, double angle, boolean drawIndicator){
        mTrackRadius = getWidth() / 2 - mMarginToEdge - mOvalRadius;

        float pointerEndX = getWidth() / 2 + (float)(mTrackRadius * Math.sin(angle));
        float pointerEndY = getHeight() / 2 - (float)(mTrackRadius * Math.cos(angle));

        mOvalRectF.left = pointerEndX - mOvalRadius;
        mOvalRectF.top = pointerEndY - mOvalRadius ;
        mOvalRectF.right = pointerEndX + mOvalRadius;
        mOvalRectF.bottom = pointerEndY + mOvalRadius ;

        canvas.drawLine(
                getWidth() / 2,
                getHeight() / 2 ,
                pointerEndX,
                pointerEndY,
                mPointerPaint);

        canvas.drawOval(mOvalRectF, mOvalPaint);

        if (drawIndicator) {
            canvas.drawPoint(pointerEndX, pointerEndY, mIndicatorPaint);
        }
    }

    public interface RecordClockLayoutState {
        void occupy(RecordClockView clockLayout);

        void retreat(RecordClockView clockLayout,
                     RetreatListener listener, RecordClockLayoutState occupyState);

        double clickAlignAngle(double angle);

        double drawAlignAngle(double angle);

        int getValue(double angle);

        boolean needShowIndicator(int value);

        interface RetreatListener{
            void onRetreatEnd(RecordClockLayoutState occupyState);
        }
    }

    public interface ClockChangeListener {
        void onValueChanging(int value);

        void onValueDecided(int value);
    }

    public void setAngle(double angle){
        mAngle = angle;
        invalidate();
    }

    protected class RetreatListener implements RecordClockLayoutState.RetreatListener {
        @Override
        public void onRetreatEnd(RecordClockLayoutState occupyState) {
            occupyState.occupy(RecordClockView.this);
            mCurrentState = occupyState;
        }
    }

    public void setClockLayoutState(RecordClockLayoutState state){
        if (mCurrentState != null) {
            mCurrentState.retreat(this, retreatListener, state);
        } else {
            retreatListener.onRetreatEnd(state);
        }
    }

    public void setOnClockChangeListener(ClockChangeListener clockListener){
        mClockListener = clockListener;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int measureWidth = MeasureSpec.getSize(widthMeasureSpec);
        int measureHeight = MeasureSpec.getSize(heightMeasureSpec);

        setMeasuredDimension(measureWidth, measureHeight);
    }

    protected long fingerDownStamp;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);

        int orientX = getWidth() / 2;
        int orientY = getHeight() / 2;

        float x;
        float y;

        double tmpAngle = 0;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                fingerDownStamp = System.currentTimeMillis();

            case MotionEvent.ACTION_MOVE:

                x = event.getX();
                y = event.getY();

                if (orientY != y) {
                    tmpAngle = Math.atan(((double)(x - orientX))
                            / ((double)(orientY - y)));

                    if (x >= orientX && orientY > y) {

                    } else if (x >= orientX && orientY < y) {
                        tmpAngle = Math.PI + tmpAngle;
                    } else if (x <= orientX && orientY < y) {
                        tmpAngle = Math.PI + tmpAngle;
                    } else if (x <= orientX && orientY > y) {
                        tmpAngle = Math.PI * 2 + tmpAngle;
                    }
                } else {
                    if (x > orientX)
                        tmpAngle = Math.PI / 2;
                    else if (orientX > x)
                        tmpAngle = Math.PI / 2 * 3;
                }

                // Log.d("ANGLE", tmpAngle / Math.PI * 180 + "");
                if (mCurrentState != null)
                    mAngle = mCurrentState.drawAlignAngle(tmpAngle);


                if (mClockListener != null && mCurrentState != null) {

                    int value = mCurrentState.getValue(mAngle);
                    mClockListener.onValueChanging(value);
                }

                break;

            case MotionEvent.ACTION_UP:
                if (System.currentTimeMillis() - fingerDownStamp <= 200) {
                    if (mCurrentState != null)
                        mAngle = mCurrentState.clickAlignAngle(mAngle);
                } else {
                    if (mCurrentState != null)
                        mAngle = mCurrentState.drawAlignAngle(mAngle);
                }

                if (mClockListener != null && mCurrentState != null) {
                    int value = mCurrentState.getValue(mAngle);
                    mClockListener.onValueDecided(value);
                }

                break;
        }

        invalidate();

        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mCurrentState != null)
            mShowIndicator = mCurrentState.needShowIndicator(mCurrentState.getValue(mAngle));
        drawPointer(canvas, mAngle, mShowIndicator);
    }

}
