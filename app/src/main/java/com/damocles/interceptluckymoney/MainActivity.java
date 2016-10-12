package com.damocles.interceptluckymoney;

import com.damocles.interceptluckymoney.service.InterceptLuckyMoneyService;
import com.damocles.interceptluckymoney.util.Constants;
import com.damocles.interceptluckymoney.util.Utils;
import com.tencent.stat.StatService;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Handler;
import android.provider.Settings;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private static MainActivity sInstance;

    public static MainActivity getInstance() {
        return sInstance;
    }

    private ImageButton moreImageButton;
    private Button button;
    private FrameLayout toastLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sInstance = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        showToast();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateRunningState();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(false);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void initViews() {
        // toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        int statusBarHeight = Utils.getStatusBarHeight(this);
        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, statusBarHeight);
        toolbar.setLayoutParams(params);
        setSupportActionBar(toolbar);
        // toast layout
        toastLayout = (FrameLayout) findViewById(R.id.main_layout_toast);
        ViewGroup.LayoutParams lp = toastLayout.getLayoutParams();
        lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        lp.height = statusBarHeight + Utils.dp2Px(this, 48);
        toastLayout.setLayoutParams(lp);
        toastLayout.setPadding(0, statusBarHeight, 0, 0);
        toastLayout.setVisibility(View.GONE);
        // more button
        moreImageButton = (ImageButton) findViewById(R.id.main_title_btn_more);
        moreImageButton.setOnClickListener(this);
        // button
        button = (Button) findViewById(R.id.main_btn_open_accessibility_service);
        button.setOnClickListener(this);
        updateRunningState();
    }

    public void updateRunningState() {
        if (button != null) {
            boolean isRunning = InterceptLuckyMoneyService.getRunningState();
            if (isRunning) {
                button.setText("关闭服务");
            } else {
                button.setText("开启服务");
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_title_btn_more:
                StatService.trackCustomEvent(this, "main_more");
                new MoreDialog(this).show();
                break;
            case R.id.main_btn_open_accessibility_service:
                StatService.trackCustomEvent(this, "main_button");
                if (!Utils.isAppInstalled(this, "com.tencent.mm")) {
                    StatService.trackCustomEvent(this, "wechat_uninstalled");
                    Toast.makeText(this, "尚未安装微信客户端", Toast.LENGTH_SHORT).show();
                    return;
                }
                // 只支持微信740及以上版本号
                int wechatVersion = Utils.getWechatVersion(this);
                if (wechatVersion < Constants.MIN_WECHAT_VERSION_SUPPORTED) {
                    StatService.trackCustomEvent(this, "wechat_outdated");
                    Toast.makeText(this, "当前微信版本过低，请升级到最新版本", Toast.LENGTH_SHORT).show();
                } else {
                    openAccessibilityServiceSettings();
                }

                break;
            default:
                break;
        }
    }

    /**
     * 打开辅助服务的设置
     */
    private void openAccessibilityServiceSettings() {
        try {
            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showToast() {
        boolean isRunning = InterceptLuckyMoneyService.getRunningState();
        if (isRunning) {
            toastLayout.setPivotY(0.0f);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    ObjectAnimator.ofFloat(toastLayout, View.SCALE_Y, 0.0f, 1.0f).setDuration(300).start();
                    toastLayout.setVisibility(View.VISIBLE);
                }
            }, 500);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    ObjectAnimator.ofFloat(toastLayout, View.SCALE_Y, 1.0f, 0.0f).setDuration(300).start();
                    toastLayout.setVisibility(View.VISIBLE);
                }
            }, 3000);
        } else {
            toastLayout.setVisibility(View.GONE);
        }
    }

}
