package com.damocles.interceptluckymoney;

import com.damocles.interceptluckymoney.util.Utils;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;

public class AboutActivity extends BaseActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        initViews();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.about_btn_back:
                finish();
                break;
            case R.id.about_btn_feedback:
                Intent data = new Intent(Intent.ACTION_SENDTO);
                data.setData(Uri.parse("mailto:damocles2016@126.com"));
                data.putExtra(Intent.EXTRA_SUBJECT, "《自动抢红包》意见反馈");
                startActivity(data);
                break;
            default:
                break;
        }

    }

    private void initViews() {
        // toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.about_toolbar);
        int statusBarHeight = Utils.getStatusBarHeight(this);
        int w = LinearLayout.LayoutParams.MATCH_PARENT;
        int h = statusBarHeight;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(w, h);
        toolbar.setLayoutParams(params);
        setSupportActionBar(toolbar);
        // back button
        findViewById(R.id.about_btn_back).setOnClickListener(this);
        // feedback button
        findViewById(R.id.about_btn_feedback).setOnClickListener(this);
    }

}
