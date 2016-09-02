/*
 * Copyright (C) 2016 Baidu, Inc. All Rights Reserved.
 */
package com.damocles.interceptluckymoney;

import com.damocles.interceptluckymoney.util.Utils;

import android.app.Activity;
import android.app.Dialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

/**
 * Created by zhanglong02 on 16/8/16.
 */
public class AboutDialog implements View.OnClickListener {

    private Dialog mDialog;

    private Button mCancelBtn;

    public AboutDialog(Activity activity) {
        mDialog = new Dialog(activity, R.style.CustomDialog);
        View view = LayoutInflater.from(activity).inflate(R.layout.dialog_about, null);
        mCancelBtn = (Button) view.findViewById(R.id.dialog_about_cancel);
        mCancelBtn.setOnClickListener(this);
        mDialog.setContentView(view);
        // 设置对话框显示属性
        Window dialogWindow = mDialog.getWindow();
        WindowManager.LayoutParams params = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.CENTER); // 对话框显示方位
        dialogWindow.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND); // 背景变暗
        params.x = 0; // 基于显示方位的水平偏移量
        params.y = 0; // 基于显示方位的垂直偏移量
        params.width =
                Utils.getScreenWidth(activity) - (int) (60 * activity.getResources().getDisplayMetrics().density);
        // 水平填充效果
        params.height = WindowManager.LayoutParams.WRAP_CONTENT; // 垂直填充效果
        params.dimAmount = 0.7f; // 背景黑暗度
        dialogWindow.setAttributes(params);
        mDialog.show();  // 显示对话框

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_about_cancel:
                mDialog.cancel();
                break;
            default:
                break;
        }
    }

    public void show() {
        mDialog.show();
    }

}
