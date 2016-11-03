package com.hongdian.recordtimepicker;

import java.util.Comparator;

/**
 * Created by ryuus on 2016/10/8 0008.
 */

public class TimeFrag {
    public TimeFrag(long start, long end, long sum){
        this.start = start;
        this.end = end;
        this.sum = sum;
    }

    public long start;

    public long end;

    public long sum;

    public static class TimeFragComparator implements Comparator<TimeFrag> {
        @Override
        public int compare(TimeFrag timeFrag, TimeFrag t1) {
            return (int)(timeFrag.start - t1.start);
        }
    }
}
