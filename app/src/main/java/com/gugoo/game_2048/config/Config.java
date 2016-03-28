package com.gugoo.game_2048.config;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by nothisboy on 2016/2/4.
 */
public class Config extends Application {
    /**
     * SharedPreferences 对象
     */
    public static SharedPreferences mSp;

    /**
     * Game Goal
     */
    public static int mGameGoal;

    /**
     * Game View 行列数
     */
    public static int mGameLines;

    /**
     * Game Item 宽高
     */
    public static int mItemSize;

    /**
     * 记录分数
     */
    public static int SCORE = 0;

    public static String SP_HIGH_SCORE = "SP_HIGH_SCORE";

    public static String KEY_HIGH_SCORE = "KEY_HIGH_SCORE";

    public static String KEY_GAME_LINES = "KEY_GAME_LINES";

    public static String KEY_GAME_GOAL = "KEY_GAME_GOAL";

    @Override
    public void onCreate() {
        super.onCreate();
        mSp = getSharedPreferences(SP_HIGH_SCORE, 0);
        mGameLines = mSp.getInt(KEY_GAME_LINES, 4);
        mGameGoal = mSp.getInt(KEY_GAME_GOAL, 2048);
        mItemSize = 0;

        if (mSp == null) {
            Log.i("mSp is null", "+++++");
        } else {
            Log.i("mSp is not null", "------");
        }
    }
}
