package com.gugoo.game_2048.common.view;

import android.content.Context;
import android.graphics.Point;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.GridLayout;
import android.widget.Toast;

import com.gugoo.game_2048.Game;
import com.gugoo.game_2048.config.Config;

import java.util.ArrayList;

/**
 * Created by nothisboy on 2016/2/7.
 */
public class GamePanel extends GridLayout implements View.OnTouchListener {

    private int mScoreHistory;
    private int mGameLines;
    private int mTarget;
    private GameItem[][] mGameMatrix;
    private int[][] mGameMatrixHistory;
    private ArrayList<Integer> mCalList;
    private ArrayList<Point> mBlanks;
    private int mHighScore;
    private int mStartX;
    private int mStartY;
    private int mEndX;
    private int mEndY;
    private int mKeyItemNum = -1;

    public GamePanel(Context context) {
        super(context);
        initGameMatrix();
    }

    public GamePanel(Context context, AttributeSet attrs) {
        super(context, attrs);
        initGameMatrix();
    }

    /**
     * 初始化view
     */
    public void initGameMatrix() {
        //初始化矩阵
        removeAllViews();
        mScoreHistory = 0;
        Config.SCORE = 0;
        Config.mGameLines = Config.mSp.getInt(Config.KEY_GAME_LINES, 4);
        mGameLines = Config.mGameLines;
        mTarget = Config.mSp.getInt(Config.KEY_GAME_GOAL, 2048);

        if (Config.mSp == null) {
            Log.i("mGameLines", "+++++" + mGameLines);
        } else {
            Log.i("init mSp is not null", "------" + mGameLines);
        }

        mGameMatrix = new GameItem[mGameLines][mGameLines];
        mGameMatrixHistory = new int[mGameLines][mGameLines];
        mCalList = new ArrayList<>();
        mBlanks = new ArrayList<>();
        mHighScore = Config.mSp.getInt(Config.KEY_HIGH_SCORE, 0);
        setColumnCount(mGameLines);
        setRowCount(mGameLines);
        setOnTouchListener(this);
        //初始化view参数
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        display.getMetrics(metrics);
        Config.mItemSize = metrics.widthPixels / Config.mGameLines;
        initGameView(Config.mItemSize);
    }

    private void initGameView(int cardSize) {
        removeAllViews();
        GameItem card;

        for (int i = 0; i < mGameLines; i++) {
            for (int j = 0; j < mGameLines; j++) {
                card = new GameItem(getContext(), 0);
                addView(card, cardSize, cardSize);
                //初始化GameMatrix全部为0 空格List为所有
                mGameMatrix[i][j] = card;
                mBlanks.add(new Point(i, j));
            }
        }
        //添加随机数字
        addRandomNum();
        addRandomNum();
    }

    private void addRandomNum() {
        getBlanks();
        if (mBlanks.size() > 0) {
            int randomNum = (int) (Math.random() * mBlanks.size());
            Point randomPoint = mBlanks.get(randomNum);
            mGameMatrix[randomPoint.x][randomPoint.y].setNum(Math.random() > 0.2d ? 2 : 4);
            animCreate(mGameMatrix[randomPoint.x][randomPoint.y]);
        }
    }

    /**
     * 生成动画
     *
     * @param target GameItem
     */
    private void animCreate(GameItem target) {
        ScaleAnimation sa = new ScaleAnimation(0.1f, 1, 0.1f, 1,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        sa.setDuration(400);
        target.setAnimation(null);
        target.startAnimation(sa);
    }

    /**
     * 检测当前空格数 刷新mBlanks列表
     */
    private void getBlanks() {
        mBlanks.clear();
        for (int i = 0; i < mGameLines; i++) {
            for (int j = 0; j < mGameLines; j++) {
                if (mGameMatrix[i][j].getNum() == 0) {
                    mBlanks.add(new Point(i, j));
                }
            }
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                saveHistoryMatrix();
                mStartX = (int) event.getX();
                mStartY = (int) event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                mEndX = (int) event.getX();
                mEndY = (int) event.getY();
                judgeDirection(mEndX - mStartX, mEndY - mStartY);
                if (isMoved()) {
                    addRandomNum();
                    //修改显示分数
                    // TODO: 2016/2/7
                    Game.getGameActivity().setScore(Config.SCORE);
                }
                // TODO: 2016/3/2 检查滑动事件是否已经结束
                checkComplete();
                break;
            default:
                break;
        }
        return true;
    }

    private void saveHistoryMatrix() {
        for (int i = 0; i < mGameLines; i++) {
            for (int j = 0; j < mGameLines; j++) {
                mGameMatrixHistory[i][j] = mGameMatrix[i][j].getNum();
            }
        }
    }

    /**
     * 根据偏移量判断移动方向
     *
     * @param offsetX offsetX
     * @param offsetY offsetY
     */
    private void judgeDirection(int offsetX, int offsetY) {
        int density = (int) getContext().getResources().getDisplayMetrics().density;
        int slideDis = 5 * density;
        int maxDis = 2000 * density;
        boolean flagNormal =
                (Math.abs(offsetX) > slideDis || Math.abs(offsetY) > slideDis) &&
                        (Math.abs(offsetX) < maxDis || Math.abs(offsetY) < maxDis);
        boolean flagSuper = Math.abs(offsetX) > maxDis || Math.abs(offsetY) > maxDis;
        if (flagNormal) {
            if (Math.abs(offsetX) > Math.abs(offsetY)) {
                if (offsetX > slideDis) {
                    swipeRight();
                } else {
                    swipeLeft();
                }
            } else {
                if (offsetY > slideDis) {
                    swipeDown();
                } else {
                    swipeUp();
                }
            }
        } else if (flagSuper) {
            // TODO: 2016/2/7 超级用户权限
        }
    }

    /**
     * 判断是否移动过（是否需要新增Item）
     *
     * @return 是否移动
     */
    private boolean isMoved() {
        for (int i = 0; i < mGameLines; i++) {
            for (int j = 0; j < mGameLines; j++) {
                if (mGameMatrixHistory[i][j] != mGameMatrix[i][j].getNum()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 判断是否结束
     * <p/>
     * 0：结束  1：正常  2：成功
     */
    public void checkComplete() {
        int result = checkNums();
        if (result == 0) {
            // TODO: 2016/3/2 弹框再来一遍+游戏结束
            ((Game) getContext()).gameOver();
        } else if (result == 2) {
            // TODO: 2016/3/2 弹框再来一遍+升级版
            ((Game) getContext()).gameWin();






        }
    }

    /**
     * 检测所有数字 看是否有满足条件的
     *
     * @return 0：结束  1：正常  2：成功
     */
    private int checkNums() {
        getBlanks();
        if (mBlanks.size() == 0) {
            for (int i = 0; i < mGameLines; i++) {
                for (int j = 0; j < mGameLines; j++) {
                    if (j < mGameLines - 1) {
                        if (mGameMatrix[i][j].getNum() == mGameMatrix[i][j].getNum()) {
                            return 1;
                        }
                    }

                    if (i < mGameLines - 1) {
                        if (mGameMatrix[i][j].getNum() == mGameMatrix[i + 1][j].getNum()) {
                            return 1;
                        }
                    }
                }
            }
            return 0;
        }

        for (int i = 0; i < mGameLines; i++) {
            for (int j = 0; j < mGameLines; j++) {
                if (mGameMatrix[i][j].getNum() == mTarget) {
                    return 2;
                }
            }
        }

        return 1;
    }

    /**
     * 撤销上次移动
     */
    public void revertGame() {
        //第一次不能撤销
        int sum = 0;
        for (int[] element : mGameMatrixHistory) {
            for (int i : element) {
                sum += i;
            }
        }
        if (sum != 0) {
            for (int i = 0; i < mGameLines; i++) {
                for (int j = 0; j < mGameLines; j++) {
                    mGameMatrix[i][j].setNum(mGameMatrixHistory[i][j]);
                    mGameMatrixHistory[i][j] = 0;
                }
            }
        } else {
            Toast.makeText(getContext(), "已经不能回退咯", Toast.LENGTH_SHORT).show();
        }
    }

    private void swipeLeft() {
        for (int i = 0; i < mGameLines; i++) {
            for (int j = 0; j < mGameLines; j++) {
                int currentNum = mGameMatrix[i][j].getNum();
                if (currentNum != 0) {
                    if (mKeyItemNum == -1) {
                        mKeyItemNum = currentNum;
                    } else {
                        if (mKeyItemNum == currentNum) {
                            mCalList.add(mKeyItemNum * 2);
                            Config.SCORE += mKeyItemNum * 2;

                            mKeyItemNum = -1;
                        } else {
                            mCalList.add(mKeyItemNum);
                            mKeyItemNum = currentNum;
                        }
                    }
                } else {
                    continue;
                }
            }
            if (mKeyItemNum != -1) {
                mCalList.add(mKeyItemNum);
            }
            //改变Item值
            for (int j = 0; j < mCalList.size(); j++) {
                mGameMatrix[i][j].setNum(mCalList.get(j));
            }
            for (int m = mCalList.size(); m < mGameLines; m++) {
                mGameMatrix[i][m].setNum(0);
            }
            //重置行参数
            mKeyItemNum = -1;
            mCalList.clear();
        }
    }

    private void swipeRight() {
        for (int i = 0; i < mGameLines; i++) {
            for (int j = mGameLines - 1; j >= 0; j--) {
                int currentNum = mGameMatrix[i][j].getNum();
                if (currentNum != 0) {
                    if (mKeyItemNum == -1) {
                        mKeyItemNum = currentNum;
                    } else {
                        if (mKeyItemNum == currentNum) {
                            mCalList.add(mKeyItemNum * 2);
                            Config.SCORE += mKeyItemNum * 2;

                            mKeyItemNum = -1;
                        } else {
                            mCalList.add(mKeyItemNum);
                            mKeyItemNum = currentNum;
                        }
                    }
                } else {
                    continue;
                }
            }
            if (mKeyItemNum != -1) {
                mCalList.add(mKeyItemNum);
            }
            //改变Item值
            for (int j = 0; j < mCalList.size(); j++) {
                mGameMatrix[i][mGameLines - 1 - j].setNum(mCalList.get(j));
            }
            for (int m = 0; m <= mGameLines - 1 - mCalList.size(); m++) {
                mGameMatrix[i][m].setNum(0);
            }
            //重置行参数
            mKeyItemNum = -1;
            mCalList.clear();
        }
    }

    private void swipeUp() {
        for (int j = 0; j < mGameLines; j++) {
            for (int i = 0; i < mGameLines; i++) {
                int currentNum = mGameMatrix[i][j].getNum();
                if (currentNum != 0) {
                    if (mKeyItemNum == -1) {
                        mKeyItemNum = currentNum;
                    } else {
                        if (mKeyItemNum == currentNum) {
                            mCalList.add(mKeyItemNum * 2);
                            Config.SCORE += mKeyItemNum * 2;

                            mKeyItemNum = -1;
                        } else {
                            mCalList.add(mKeyItemNum);
                            mKeyItemNum = currentNum;
                        }
                    }
                } else {
                    continue;
                }
            }
            if (mKeyItemNum != -1) {
                mCalList.add(mKeyItemNum);
            }
            //改变Item值
            for (int i = 0; i < mCalList.size(); i++) {
                mGameMatrix[i][j].setNum(mCalList.get(i));
            }
            for (int m = mCalList.size(); m < mGameLines; m++) {
                mGameMatrix[m][j].setNum(0);
            }
            //重置行参数
            mKeyItemNum = -1;
            mCalList.clear();
        }
    }

    private void swipeDown() {
        for (int j = 0; j < mGameLines; j++) {
            for (int i = mGameLines - 1; i >= 0; i--) {
                int currentNum = mGameMatrix[i][j].getNum();
                if (currentNum != 0) {
                    if (mKeyItemNum == -1) {
                        mKeyItemNum = currentNum;
                    } else {
                        if (mKeyItemNum == currentNum) {
                            mCalList.add(mKeyItemNum * 2);
                            Config.SCORE += mKeyItemNum * 2;

                            mKeyItemNum = -1;
                        } else {
                            mCalList.add(mKeyItemNum);
                            mKeyItemNum = currentNum;
                        }
                    }
                } else {
                    continue;
                }
            }
            if (mKeyItemNum != -1) {
                mCalList.add(mKeyItemNum);
            }
            //改变Item值
            for (int i = 0; i < mCalList.size(); i++) {
                mGameMatrix[mGameLines - 1 - i][j].setNum(mCalList.get(i));
            }
            for (int m = 0; m < mGameLines - mCalList.size(); m++) {
                mGameMatrix[m][j].setNum(0);
            }
            //重置行参数
            mKeyItemNum = -1;
            mCalList.clear();
        }
    }
}
