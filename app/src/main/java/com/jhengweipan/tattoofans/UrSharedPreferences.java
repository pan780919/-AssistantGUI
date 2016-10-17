package com.jhengweipan.tattoofans;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by HYXEN20141227 on 2015/10/14.
 */
public class UrSharedPreferences {
    public  static  final String NAME = "UrSharedPreferences";
    //第一次使用
    public static  final String KEY_FRIST_USED = "isFristUsed";
    public static void saveIsFirstUsed(Context context) {
    SharedPreferences sp = context.getSharedPreferences(NAME, context.MODE_PRIVATE);
    sp.edit().putBoolean(KEY_FRIST_USED, false).commit();
    }

    public static boolean getIsFirstUsed(Context context) {
        SharedPreferences sp = context.getSharedPreferences(NAME, context.MODE_PRIVATE);
        return sp.getBoolean(KEY_FRIST_USED, true);
    }
}
