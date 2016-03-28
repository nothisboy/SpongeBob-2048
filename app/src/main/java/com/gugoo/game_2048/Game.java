package com.gugoo.game_2048;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.gugoo.game_2048.common.view.GamePanel;
import com.gugoo.game_2048.config.Config;
import com.gugoo.game_2048.config.Option;

public class Game extends AppCompatActivity {

    public static Game gameActivity;

    private TextView text_score;
    private TextView text_record;
    private TextView text_goal;
    private GamePanel game_panel;
    private Button btn_revert;
    private Button btn_restart;
    private Button btn_option;

    private static final int REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_game);
        gameActivity = this;

        initLayout();
        initValue();
        initListener();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (REQUEST_CODE == 1 && resultCode == RESULT_OK && data.getIntExtra(Option.KEY_RESULT_DATA, 0) == Option.RESULT_DATA) {
            text_goal.setText(Config.mSp.getInt(Config.KEY_GAME_GOAL, 2048) + "");
            game_panel.initGameMatrix();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void initLayout() {
        text_score = (TextView) findViewById(R.id.text_score);
        text_record = (TextView) findViewById(R.id.text_record);
        game_panel = (GamePanel) findViewById(R.id.game_panel);
        btn_revert = (Button) findViewById(R.id.btn_revert);
        btn_restart = (Button) findViewById(R.id.btn_restart);
        btn_option = (Button) findViewById(R.id.btn_option);
    }

    private void initValue() {
        text_record.setText(Config.mSp.getInt(Config.KEY_HIGH_SCORE, 0) + "");
        text_goal.setText(Config.mSp.getInt(Config.KEY_GAME_GOAL, 2048) + "");
        text_score.setText("0");
    }

    private void initListener() {
        btn_revert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                game_panel.revertGame();
            }
        });

        btn_restart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                game_panel.initGameMatrix();
            }
        });

        btn_option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(Game.this, Option.class), REQUEST_CODE);
            }
        });
    }

    public static Game getGameActivity() {
        return gameActivity;
    }

    public void setScore(int score) {
        text_score.setText(score + "");
    }

    public void gameOver() {
        Config.mSp.edit().putInt(Config.KEY_HIGH_SCORE, Integer.valueOf(text_score.getText().toString())).commit();
    }

    public void gameWin() {
        Config.mSp.edit().putInt(Config.KEY_HIGH_SCORE, Integer.valueOf(text_score.getText().toString())).commit();
    }
}
