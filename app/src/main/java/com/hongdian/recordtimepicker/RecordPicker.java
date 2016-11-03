package com.hongdian.recordtimepicker;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class RecordPicker extends AppCompatActivity implements View.OnClickListener {
    protected int mHourValue;

    protected int mMinValue;

    protected int mSecValue;

    protected TextView mHourView;

    protected TextView mMinView;

    protected TextView mSecView;

    protected FrameLayout mClockPan;

    protected RecordRing mRingView;

    protected RecordClockView mClockView;

    protected DialLayout mDialView;

    protected List<TimeFrag> mRecordDurations;

    protected TimeSelectMode hourSelectMode;

    protected TimeSelectMode minSelectMode;

    protected TimeSelectMode secSelectMode;

    protected TimeFrag.TimeFragComparator mTimeComparator;

    

    protected void updateRecordRing(List<TimeFrag> frags, Animation animation) {
        mRingView.update(frags, animation);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.record_picker);

        init();
    }

    void init(){

        mHourView = (TextView)findViewById(R.id.hour_tv);

        mMinView = (TextView)findViewById(R.id.min_tv);

        mSecView = (TextView)findViewById(R.id.sec_tv);

        mClockPan = (FrameLayout)findViewById(R.id.clock_pan);

        mRingView = (RecordRing)findViewById(R.id.ring_view);

        mClockView = (RecordClockView)findViewById(R.id.clock_view);

        mDialView = (DialLayout)findViewById(R.id.dial_view);

        mRecordDurations = null;

        hourSelectMode = new HourSelectMode();

        minSelectMode = new MinSelectMode();

        secSelectMode = new SecSelectMode();

        mTimeComparator = new TimeFrag.TimeFragComparator();

        List<TimeFrag> data = new LinkedList<>();
        data.add(new TimeFrag(7200, 9850, 3600 * 24));
        data.add(new TimeFrag(2333, 4566, 3600 * 24));
        data.add(new TimeFrag(12000, 15000, 3600 * 24));
        notifyData(data);
    }

    public void notifyData(List<TimeFrag> data) {

        // sort the time_list
        Collections.sort(data, mTimeComparator);
        mRecordDurations = data;

        // TextView 预设建议值
        mMinView.setText(String.format("%02d", mMinValue));
        mSecView.setText(String.format("%02d", mSecValue));

        Log.d("INITVAL", "notifyData: " + mHourValue + " " + mMinValue + " " + mSecValue);

        mHourView.setOnClickListener(this);
        mMinView.setOnClickListener(this);
        mSecView.setOnClickListener(this);

        mClockView.setClockLayoutState(hourSelectMode);
        mClockView.setOnClockChangeListener(hourSelectMode);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.hour_tv:
                mClockView.setOnClockChangeListener(hourSelectMode);
                mClockView.setClockLayoutState(hourSelectMode);

                mHourView.setTextColor(getResources().getColor(R.color.colorIndicate));
                mHourView.setText(String.format("%02d", mHourValue));
                mSecView.setTextColor(Color.GRAY);
                mMinView.setTextColor(Color.GRAY);
                break;
            case R.id.min_tv:
                mClockView.setOnClockChangeListener(minSelectMode);
                mClockView.setClockLayoutState(minSelectMode);

                mMinView.setTextColor(getResources().getColor(R.color.colorIndicate));
                mMinView.setText(String.format("%02d", mMinValue));
                mHourView.setTextColor(Color.GRAY);
                mSecView.setTextColor(Color.GRAY);
                break;
            case R.id.sec_tv:
                mClockView.setOnClockChangeListener(secSelectMode);
                mClockView.setClockLayoutState(secSelectMode);

                mSecView.setTextColor(getResources().getColor(R.color.colorIndicate));
                mSecView.setText(String.format("%02d", mSecValue));
                mMinView.setTextColor(Color.GRAY);
                mHourView.setTextColor(Color.GRAY);
                break;
        }
    }

    protected abstract class TimeSelectMode implements
            RecordClockView.RecordClockLayoutState,
            RecordClockView.ClockChangeListener {

        protected int visibleSplitterStep;

        protected int allSplitterCount;

        protected int visibleSplitterCount;

        protected AnimationSet inAnimation;
        protected AnimationSet outAnimation;

        protected abstract void setSplitterCount();

        protected void initAnimation(){
            inAnimation = new AnimationSet(false);

            ScaleAnimation scaleIn = new ScaleAnimation(
                    0.8f, 1.0f,
                    0.8f, 1.0f,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f);

            AlphaAnimation alphaIn = new AlphaAnimation(0.4f, 1.0f);

            scaleIn.setDuration(200);
            alphaIn.setDuration(200);

            inAnimation.addAnimation(scaleIn);
            inAnimation.addAnimation(alphaIn);

            outAnimation = new AnimationSet(false);

            ScaleAnimation scaleOut = new ScaleAnimation(
                    1.0f, 0.8f,
                    1.0f, 0.8f,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f);

            AlphaAnimation alphaOut = new AlphaAnimation(1.0f, 0.4f);

            scaleOut.setDuration(200);
            alphaOut.setDuration(200);

            outAnimation.addAnimation(scaleOut);
            outAnimation.addAnimation(alphaOut);
        }

        public TimeSelectMode(){
            setSplitterCount();

            initAnimation();
        }

        @Override
        public void retreat(RecordClockView clockLayout, final RetreatListener listener,
                            final RecordClockView.RecordClockLayoutState occupyState) {

            outAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    listener.onRetreatEnd(occupyState);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

            mRingView.startAnimation(outAnimation);
            mClockPan.startAnimation(outAnimation);
        }

        @Override
        public double clickAlignAngle(double angle) {
            double step = 2 * Math.PI / visibleSplitterCount;
            int vInt;
            double vDouble;

            vInt = (int)(angle / step);
            vDouble = angle / step;

            if (vDouble >= (double)vInt + 0.5) {
                vInt += 1;
                vInt %= visibleSplitterCount;
            }

            return vInt * step;
        }

        @Override
        public double drawAlignAngle(double angle) {
            double step = 2 * Math.PI / allSplitterCount;
            int vInt;
            double vDouble;

            vInt = (int)(angle / step);
            vDouble = angle / step;

            if (vDouble >= (double)vInt + 0.5) {
                vInt += 1;
                vInt %= allSplitterCount;
            }

            return vInt * step;
        }

        @Override
        public int getValue(double angle) {

            double step = 2 * Math.PI / allSplitterCount;
            int vInt;
            double vDouble;

            vInt = (int)(angle / step);
            vDouble = angle / step;

            if (vDouble >= (double)vInt + 0.5) {
                vInt += 1;
                vInt %= allSplitterCount;
            }

            return vInt;
        }

        @Override
        public boolean needShowIndicator(int value) {
            if (visibleSplitterCount == allSplitterCount)
                return false;
            else {
                if (value % visibleSplitterStep == 0)
                    return false;
                else
                    return true;
            }
        }

        public double getAngle(int value){
            return 2 * Math.PI * value / allSplitterCount;
        }
    }

    protected class HourSelectMode extends TimeSelectMode {

        @Override
        protected void setSplitterCount() {
            visibleSplitterStep = 1;
            visibleSplitterCount = 24;
            allSplitterCount = 24;
        }

        @Override
        public void onValueChanging(int value) {
            mHourView.setTextColor(getResources().getColor(R.color.colorIndicate));
            mHourView.setText(String.format("%02d", value));
            mHourValue = value;
        }

        @Override
        public void onValueDecided(int value) {
            onValueChanging(value);

            mClockView.setOnClockChangeListener(minSelectMode);
            mClockView.setClockLayoutState(minSelectMode);
        }

        @Override
        public void occupy(RecordClockView clockLayout) {
            for (int i = 0; i < 24; i++) {
                mDialView.setClockPadValue(i, String.format("%02d", i));
                mDialView.setClockPadVisibility(i, View.VISIBLE);
            }

            if (mRecordDurations == null) {
                mRingView.setVisibility(View.INVISIBLE);
            } else {
                // 获取建议值
                if (mRecordDurations.size() > 0) {
                    mHourValue = (int) (mRecordDurations.get(0).start / 3600);
                    mMinValue = (int) ((mRecordDurations.get(0).start % 3600) / 60);
                    mSecValue = (int) (mRecordDurations.get(0).start % 60);

                    Log.d("advise", "hour:" + mHourValue +
                            " min:" + mMinValue + " sec:" + mSecValue);
                } else {
                    mHourValue = 0;
                    mMinValue = 0;
                    mSecValue = 0;
                }

                updateRecordRing(mRecordDurations, inAnimation);
            }

            mHourView.setTextColor(getResources().getColor(R.color.colorIndicate));
            mHourView.setText(String.format("%02d", mHourValue));

            mMinView.setTextColor(Color.GRAY);
            mMinView.setText(String.format("%02d", mMinValue));

            mSecView.setTextColor(Color.GRAY);
            mSecView.setText(String.format("%02d", mSecValue));

            mClockView.setAngle(getAngle(mHourValue));
            mClockPan.startAnimation(inAnimation);
        }
    }


    protected class MinSelectMode extends TimeSelectMode {

        @Override
        protected void setSplitterCount() {
            visibleSplitterStep = 5;
            visibleSplitterCount = 12;
            allSplitterCount = 60;
        }

        @Override
        public void onValueChanging(int value) {
            mMinView.setTextColor(getResources().getColor(R.color.colorIndicate));
            mMinView.setText(String.format("%02d", value));
            mMinValue = value;
        }

        @Override
        public void onValueDecided(int value) {
            onValueChanging(value);

            mClockView.setOnClockChangeListener(secSelectMode);
            mClockView.setClockLayoutState(secSelectMode);
        }

        @Override
        public void occupy(RecordClockView clockLayout) {
            for (int i = 0; i < 24; i++) {
                if (i % 2 == 0) {
                    mDialView.setClockPadValue(i, String.format("%02d", i / 2 * 5));
                    mDialView.setClockPadVisibility(i, View.VISIBLE);
                } else {
                    mDialView.setClockPadVisibility(i, View.INVISIBLE);
                }
            }

            if (mRecordDurations == null) {
                mRingView.setVisibility(View.INVISIBLE);
            } else {
                Log.d("RING", "occupy: in min,cur_hour=" + mHourValue);

                List<TimeFrag> data = TimeFragListUtil.split(mRecordDurations,
                        3600 * mHourValue,
                        3600 * (mHourValue + 1));

                updateRecordRing(data, inAnimation);

                // 计算建议值
                if (data.size() > 0) {
                    Collections.sort(data, mTimeComparator);

                    mMinValue = (int) ((data.get(0).start % 3600) / 60);
                    mSecValue = (int) (data.get(0).start % 60);

                    Log.d("advise", "min:" + mMinValue + " sec:" + mSecValue);
                } else {
                    mMinValue = 0;
                    mSecValue = 0;
                }
            }

            mMinView.setTextColor(getResources().getColor(R.color.colorIndicate));
            mMinView.setText(String.format("%02d", mMinValue));

            mHourView.setTextColor(Color.GRAY);

            mSecView.setTextColor(Color.GRAY);
            mSecView.setText(String.format("%02d", mSecValue));

            mClockView.setAngle(getAngle(mMinValue));
            mClockPan.startAnimation(inAnimation);
        }
    }


    public class SecSelectMode extends TimeSelectMode {

        @Override
        protected void setSplitterCount() {
            visibleSplitterStep = 5;
            visibleSplitterCount = 12;
            allSplitterCount = 60;
        }

        @Override
        public void onValueChanging(int value) {
            mSecView.setTextColor(getResources().getColor(R.color.colorIndicate));
            mSecView.setText(String.format("%02d", value));
            mSecValue = value;
        }

        @Override
        public void onValueDecided(int value) {
            onValueChanging(value);
        }

        @Override
        public void occupy(RecordClockView clockLayout) {
            for (int i = 0; i < 24; i++) {
                if (i % 2 == 0) {
                    mDialView.setClockPadValue(i, String.format("%02d", i / 2 * 5));
                    mDialView.setClockPadVisibility(i, View.VISIBLE);
                } else {
                    mDialView.setClockPadVisibility(i, View.INVISIBLE);
                }
            }

            if (mRecordDurations == null) {
                mRingView.setVisibility(View.INVISIBLE);
            } else {
                Log.d("RING", "occupy: in sec, cur_hour=" + mHourValue +
                        " cur_min=" + mMinValue);

                List<TimeFrag> data = TimeFragListUtil.split(mRecordDurations,
                        3600 * mHourValue + 60 * mMinValue,
                        3600 * mHourValue + 60 * (mMinValue + 1));

                updateRecordRing(data, inAnimation);

                // 计算建议值
                if (data.size() > 0) {
                    Collections.sort(data, mTimeComparator);
                    mSecValue = (int) (data.get(0).start % 60);

                    Log.d("advise", "sec:" + mSecValue);
                } else {
                    mSecValue = 0;
                }
            }

            mSecView.setTextColor(getResources().getColor(R.color.colorIndicate));
            mSecView.setText(String.format("%02d", mSecValue));

            mHourView.setTextColor(Color.GRAY);
            mMinView.setTextColor(Color.GRAY);

            mClockView.setAngle(getAngle(mSecValue));
            mClockPan.startAnimation(inAnimation);
        }

    }
}
