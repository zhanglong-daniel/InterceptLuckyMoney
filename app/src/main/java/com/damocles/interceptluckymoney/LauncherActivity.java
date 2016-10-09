package com.damocles.interceptluckymoney;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.widget.TextView;

public class LauncherActivity extends BaseActivity {

    private TextView skipTextView;
    private int leftSeconds = 2;

    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        initViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startCountDown();
    }

    //防止用户返回键退出APP
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void initViews() {
        skipTextView = (TextView) findViewById(R.id.launcher_txt_skip);
    }

    private void startCountDown() {
        skipTextView.setText(leftSeconds + "S跳转");
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                leftSeconds--;
                if (leftSeconds == 0) {
                    startActivity(new Intent(LauncherActivity.this, MainActivity.class));
                    LauncherActivity.this.finish();
                } else {
                    startCountDown();
                }
            }
        }, 1000);
    }
}
