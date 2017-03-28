package com.damocles.interceptluckymoney;

import com.damocles.interceptluckymoney.util.SharedPreferencesUtil;
import com.damocles.interceptluckymoney.util.Utils;
import com.tencent.stat.StatService;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;

public class SettingActivity extends BaseActivity implements View.OnClickListener {

    private Switch notifySwitch;
    private Switch vibrateSwitch;
    private Switch soundSwitch;

    private RelativeLayout notifyLayout;
    private RelativeLayout vibrateLayout;
    private RelativeLayout soundLayout;

    private LinearLayout detailLayout;

    private SharedPreferencesUtil sharedPreferencesUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        sharedPreferencesUtil = SharedPreferencesUtil.getInstance();
        initViews();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.setting_btn_back:
                StatService.trackCustomEvent(this, "setting_back");
                finish();
                break;
            case R.id.setting_layout_notify:
                notifySwitch.setChecked(!notifySwitch.isChecked());
                break;
            case R.id.setting_layout_vibrate:
                vibrateSwitch.setChecked(!vibrateSwitch.isChecked());
                break;
            case R.id.setting_layout_sound:
                soundSwitch.setChecked(!soundSwitch.isChecked());
                break;
            default:
                break;
        }

    }

    private void initViews() {
        // toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.setting_toolbar);
        int statusBarHeight = Utils.getStatusBarHeight(this);
        int w = LinearLayout.LayoutParams.MATCH_PARENT;
        int h = statusBarHeight;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(w, h);
        toolbar.setLayoutParams(params);
        setSupportActionBar(toolbar);
        // back button
        findViewById(R.id.setting_btn_back).setOnClickListener(this);
        // detail layout
        boolean notifyEnable = sharedPreferencesUtil.getNotifySwitch(this);
        detailLayout = (LinearLayout) findViewById(R.id.setting_layout_detail);
        detailLayout.setVisibility(notifyEnable ? View.VISIBLE : View.GONE);
        // notify layout
        notifyLayout = (RelativeLayout) findViewById(R.id.setting_layout_notify);
        notifyLayout.setOnClickListener(this);
        // notify switch
        notifySwitch = (Switch) findViewById(R.id.setting_switch_notify);
        notifySwitch.setChecked(notifyEnable);
        notifySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                StatService.trackCustomEvent(SettingActivity.this, "setting_notify", String.valueOf(isChecked));
                sharedPreferencesUtil.putNotifySwitch(SettingActivity.this, isChecked);
                // detailLayout.setVisibility(View.VISIBLE);
                // detailLayout.setPivotY(0.0f);
                if (isChecked) {
                    // ObjectAnimator.ofFloat(detailLayout, View.SCALE_Y, 0.0f, 1.0f).setDuration(250).start();
                    TranslateAnimation showAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                            Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                            -1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
                    showAnimation.setDuration(300);
                    detailLayout.startAnimation(showAnimation);
                    detailLayout.setVisibility(View.VISIBLE);
                } else {
                    // ObjectAnimator.ofFloat(detailLayout, View.SCALE_Y, 1.0f, 0.0f).setDuration(250).start();
                    TranslateAnimation hiddenAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
                            0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                            Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                            -1.0f);
                    hiddenAnimation.setDuration(300);
                    detailLayout.startAnimation(hiddenAnimation);
                    detailLayout.setVisibility(View.GONE);
                }
            }
        });
        // vibrate layout
        vibrateLayout = (RelativeLayout) findViewById(R.id.setting_layout_vibrate);
        vibrateLayout.setOnClickListener(this);
        // vibrate switch
        vibrateSwitch = (Switch) findViewById(R.id.setting_switch_notify_vibrate);
        vibrateSwitch.setChecked(sharedPreferencesUtil.getVibrateSwitch(this));
        vibrateSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                StatService.trackCustomEvent(SettingActivity.this, "setting_vibrate", String.valueOf(isChecked));
                sharedPreferencesUtil.putVibrateSwitch(SettingActivity.this, isChecked);
            }
        });
        // sound layout
        soundLayout = (RelativeLayout) findViewById(R.id.setting_layout_sound);
        soundLayout.setOnClickListener(this);
        // sound switch
        soundSwitch = (Switch) findViewById(R.id.setting_switch_notify_sound);
        soundSwitch.setChecked(sharedPreferencesUtil.getSoundSwitch(this));
        soundSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                StatService.trackCustomEvent(SettingActivity.this, "setting_sound", String.valueOf(isChecked));
                sharedPreferencesUtil.putSoundSwitch(SettingActivity.this, isChecked);
            }
        });
    }

}
