/*
 * Copyright (C) 2016 Baidu, Inc. All Rights Reserved.
 */
package com.damocles.interceptluckymoney;

import com.damocles.interceptluckymoney.util.Utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by zhanglong02 on 16/8/16.
 */
public class MoreDialog implements View.OnClickListener {

    private Activity mActivity;

    private Dialog mDialog;
    private TextView mSettingTextView;
    //    private TextView deleteTextView;
    private TextView mAboutTextView;

    public MoreDialog(Activity activity) {
        mActivity = activity;
        mDialog = new Dialog(mActivity, R.style.CustomDialog);
        View view = LayoutInflater.from(mActivity).inflate(R.layout.dialog_more, null);
        mSettingTextView = (TextView) view.findViewById(R.id.dialog_more_setting);
        mSettingTextView.setOnClickListener(this);
        mAboutTextView = (TextView) view.findViewById(R.id.dialog_more_about);
        mAboutTextView.setOnClickListener(this);
        mDialog.setContentView(view);
        // 设置对话框显示属性
        Window dialogWindow = mDialog.getWindow();
        WindowManager.LayoutParams params = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.BOTTOM); // 对话框显示方位
        dialogWindow.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND); // 背景变暗
        params.x = 0; // 基于显示方位的水平偏移量
        params.y = 0; // 基于显示方位的垂直偏移量
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = (int) (112 * activity.getResources().getDisplayMetrics()
                .density);
        params.dimAmount = 0.5f; // 背景黑暗度
        dialogWindow.setAttributes(params);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_more_setting:
                mDialog.cancel();
                mActivity.startActivity(new Intent(mActivity, SettingActivity.class));
                break;
            case R.id.dialog_more_about:
                mDialog.cancel();
                mActivity.startActivity(new Intent(mActivity, AboutActivity.class));
                break;
            default:
                break;
        }
    }

    public void show() {
        mDialog.show();
    }

}
