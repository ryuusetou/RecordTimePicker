package com.hongdian.recordtimepicker;

import android.util.Log;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by ryuusetou on 2016/10/12.
 */

public class TimeFragListUtil {

    public static List<TimeFrag> split(List<TimeFrag> src, int scopeStart, int scopeEnd){
        List<TimeFrag> result = new LinkedList<>();
        TimeFrag newOne = null;

        for (TimeFrag frag : src) {
//            Log.d("UTIL", "start:" + scopeStart + " end:" + scopeEnd
//                    + " frag_start:" + frag.start + " frag_end;" + frag.end);

            if (scopeStart <= frag.start &&
                    (scopeEnd <= frag.end && scopeEnd > frag.start)) {
                newOne = new TimeFrag(frag.start - scopeStart, scopeEnd - scopeStart,
                        scopeEnd - scopeStart);

            } else if (scopeStart <= frag.start && scopeEnd >= frag.end) {
                newOne = new TimeFrag(frag.start - scopeStart, frag.end - scopeStart,
                        scopeEnd - scopeStart);

            } else if ((scopeStart >= frag.start && scopeStart < frag.end) &&
                    scopeEnd >= frag.end) {
                newOne = new TimeFrag(0, frag.end - scopeStart,
                        scopeEnd - scopeStart);

            } else if (scopeStart >= frag.start && scopeEnd <= frag.end) {
                newOne = new TimeFrag(0, scopeEnd - scopeStart,
                        scopeEnd - scopeStart);
            }

            if (newOne != null) {
//                Log.d("UTIL", "newOne " + newOne.start + " " + newOne.end);
                result.add(newOne);
            }

            newOne = null;
        }

        return result;
    }

    public static boolean isInclude(List<TimeFrag> src, int value) {
        for (TimeFrag frag : src) {
            if (value >= frag.start && value <= frag.end)
                return true;
        }

        return false;
    }


}
