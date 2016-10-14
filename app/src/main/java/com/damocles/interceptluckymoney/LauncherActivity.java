package com.damocles.interceptluckymoney;

import com.damocles.interceptluckymoney.util.Utils;
import com.tencent.stat.StatConfig;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;

public class LauncherActivity extends BaseActivity {

    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initMta();
        setContentView(R.layout.activity_launcher);
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

    private void startCountDown() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(LauncherActivity.this, MainActivity.class));
                LauncherActivity.this.finish();
            }
        }, 500);
    }

    private void initMta() {
        String channel = Utils.getChannel(this);
        Log.i("launcher", "channel = " + channel);
        if (!TextUtils.isEmpty(channel)) {
            StatConfig.setInstallChannel(channel);
        }
    }
}
