package net.utils;

import android.app.Activity;
import android.util.DisplayMetrics;

public class ScreenUtils {
    public static int[] getScreenSize(Activity activity) {
        int[] result = new int[2];
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        result[0] = height;
        result[1] = width;
        return result;
    }
}
