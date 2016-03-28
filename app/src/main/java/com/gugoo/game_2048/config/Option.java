package com.gugoo.game_2048.config;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.gugoo.game_2048.R;

/**
 * Created by nothisboy on 2016/2/12.
 */
public class Option extends AppCompatActivity {

    private Button btn_op_game_lines;
    private Button btn_op_target_goal;
    private Button btn_op_back;
    private Button btn_op_done;
    private TextView text_op_contact_me;

    private int tGameLines;
    private int tTargetGoal;

    public static final int RESULT_DATA = 1;
    public static final String KEY_RESULT_DATA = "KEY_RESULT_DATA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_option);

        initLayout();
        initValues();
        initListener();
    }

    private void initLayout() {
        btn_op_game_lines = (Button) findViewById(R.id.btn_op_game_lines);
        btn_op_target_goal = (Button) findViewById(R.id.btn_op_target_goal);
        btn_op_back = (Button) findViewById(R.id.btn_op_back);
        btn_op_done = (Button) findViewById(R.id.btn_op_done);
        text_op_contact_me = (TextView) findViewById(R.id.text_op_contact_me);
    }

    private void initValues() {
        tGameLines = Config.mGameLines;
        btn_op_game_lines.setText(tGameLines + "");

        tTargetGoal = Config.mGameGoal;
        btn_op_target_goal.setText(tTargetGoal + "");
    }

    private void initListener() {
        btn_op_game_lines.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tGameLines == 4) {
                    tGameLines = 5;
                    btn_op_game_lines.setText(tGameLines + "");
                } else {
                    tGameLines = 4;
                    btn_op_game_lines.setText(tTargetGoal + "");
                }
            }
        });

        btn_op_target_goal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tTargetGoal == 2048) {
                    tTargetGoal = 4096;
                    btn_op_target_goal.setText(tTargetGoal + "");
                } else {
                    tTargetGoal = 2048;
                    btn_op_target_goal.setText(tTargetGoal + "");
                }
            }
        });

        btn_op_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btn_op_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("lines1", "" + tGameLines);

                Config.mSp.edit().putInt(Config.KEY_GAME_LINES, tGameLines).putInt(Config.KEY_GAME_GOAL, tTargetGoal).apply();
                // TODO: 2016/2/25 提醒游戏界面初始化
                Log.i("lines", "" + Config.mSp.getInt(Config.KEY_GAME_LINES, 4));

                Intent intent = new Intent();
                intent.putExtra(KEY_RESULT_DATA, RESULT_DATA);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
}
