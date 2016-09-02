package com.damocles.interceptluckymoney;

import com.damocles.interceptluckymoney.service.InterceptLuckyMoneyService;
import com.damocles.interceptluckymoney.util.Constants;
import com.damocles.interceptluckymoney.util.Utils;
import com.tencent.stat.StatService;

import android.content.Intent;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private static TextView stateTextView;

    private Button button;
    private ImageView bottomImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    protected void onResume() {
        super.onResume();
        StatService.onResume(this);
        updateRunningState();
    }

    @Override
    protected void onPause() {
        super.onPause();
        StatService.onPause(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private void initViews() {
        toolbar = Utils.initToolbar(this, R.id.main_toolbar);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                new AboutDialog(MainActivity.this).show();
                return true;
            }
        });
        stateTextView = (TextView) findViewById(R.id.main_txt_state);
        button = (Button) findViewById(R.id.main_btn_open_accessibility_service);
        button.setOnClickListener(this);
        bottomImageView = (ImageView) findViewById(R.id.image_bottom);
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) bottomImageView.getLayoutParams();
        lp.width = LinearLayout.LayoutParams.MATCH_PARENT;
        lp.height = Utils.getScreenWidth(this) * 208 / 360;
        bottomImageView.setLayoutParams(lp);
        bottomImageView.setScaleType(ImageView.ScaleType.FIT_XY);
        updateRunningState();
    }

    public static void updateRunningState() {
        if (stateTextView != null) {
            boolean isRunning = InterceptLuckyMoneyService.getRunningState();
            stateTextView.setText("服务运行状态：" + (isRunning ? "开启" : "关闭"));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_btn_open_accessibility_service:
                StatService.trackCustomEvent(this, "button");
                if (!Utils.isAppInstalled(this, "com.tencent.mm")) {
                    Toast.makeText(this, "尚未安装微信客户端", Toast.LENGTH_SHORT).show();
                    return;
                }
                // 只支持微信740及以上版本号
                int wechatVersion = Utils.getWechatVersion(this);
                if (wechatVersion < Constants.MIN_WECHAT_VERSION_SUPPORTED) {
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

}
