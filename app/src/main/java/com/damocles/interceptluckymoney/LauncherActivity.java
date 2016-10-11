package com.damocles.interceptluckymoney;

import com.damocles.interceptluckymoney.util.SharedPreferencesUtil;
import com.damocles.interceptluckymoney.util.Utils;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class LauncherActivity extends BaseActivity {

    private TextView skipTextView;
//    private ImageView imageView;
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
//        imageView = (ImageView) findViewById(R.id.launcher_img);
//        if (!SharedPreferencesUtil.getInstance().getFirstOpen(this)) {
//            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) imageView.getLayoutParams();
//            lp.topMargin = Utils.dp2Px(this, 480);
//            imageView.setImageResource(R.drawable.launcher_btn_selector);
//            imageView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    handler.removeCallbacks(jumpRunnable);
//                    startActivity(new Intent(LauncherActivity.this, MainActivity.class));
//                    openAccessibilityServiceSettings();
//                    LauncherActivity.this.finish();
//                }
//            });
//        } else {
//            imageView.setVisibility(View.GONE);
//        }

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

    private void startCountDown() {
        skipTextView.setText(leftSeconds + "S跳转");
        handler.postDelayed(jumpRunnable, 1000);
    }

    private Runnable jumpRunnable = new Runnable() {
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
    };
}
