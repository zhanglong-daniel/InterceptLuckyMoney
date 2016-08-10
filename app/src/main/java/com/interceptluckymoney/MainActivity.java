package com.interceptluckymoney;

import java.util.List;

import com.interceptluckymoney.service.InterceptLuckyMoneyService;
import com.interceptluckymoney.util.Constants;
import com.interceptluckymoney.util.Utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static TextView stateTextView;
    private TextView wechatVersionTextView;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateRunningState();
        getForegroundActivity();
    }

    private void initViews() {
        wechatVersionTextView = (TextView) findViewById(R.id.main_txt_wechat_version);
        stateTextView = (TextView) findViewById(R.id.main_txt_state);
        button = (Button) findViewById(R.id.main_btn_open_accessibility_service);
        button.setOnClickListener(this);
        // 只支持微信740及以上版本号
        int wechatVersion = Utils.getWechatVersion(this);
        if (wechatVersion < Constants.MIN_WECHAT_VERSION_SUPPORTED) {
            wechatVersionTextView.setText("当前微信版本：" + wechatVersion + "；微信版本太低，暂不支持，请尽快升级微信");
            button.setVisibility(View.GONE);
        } else {
            wechatVersionTextView.setText("当前微信版本：" + wechatVersion);
        }

        updateRunningState();
    }

    public static void updateRunningState() {
        if (stateTextView != null) {
            boolean isRunning = InterceptLuckyMoneyService.getRunningState();
            stateTextView.setText("插件运行状态：" + (isRunning ? "正在运行..." : "关闭"));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_btn_open_accessibility_service:
                openAccessibilityServiceSettings();
                break;
            default:
                break;
        }
    }

    /**
     * 打开辅助服务的设置
     */
    private void openAccessibilityServiceSettings() {
//        try {
//            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
//            startActivity(intent);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        Intent intent = new Intent();
        intent.setClassName("com.android.settings","com.android.settings.Settings$AccessibilitySettingsActivity");
        startActivity(intent);

    }

    private Handler mHandler = new Handler();

    private void getForegroundActivity() {
        Log.e("fuck", "================================================");
        ActivityManager am = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(3);
        for (ActivityManager.RunningTaskInfo info : list) {
            Log.e("fuck", "activity = " + info.topActivity.toString());
        }
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getForegroundActivity();
            }
        }, 2000);
    }
}
