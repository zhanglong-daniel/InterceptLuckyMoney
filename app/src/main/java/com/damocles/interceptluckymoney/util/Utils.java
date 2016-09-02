package com.damocles.interceptluckymoney.util;

import java.lang.reflect.Field;

import com.damocles.interceptluckymoney.R;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.PowerManager;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.widget.LinearLayout;

/**
 * Created by danielzhang on 16/2/14.
 */
public class Utils {

    public static Toolbar initToolbar(Activity activity, int id) {
        Toolbar toolbar = (Toolbar) activity.findViewById(id);
        int statusBarHeight = getStatusBarHeight(activity);
        int w = LinearLayout.LayoutParams.MATCH_PARENT;
        int h = (int) (activity.getResources().getDisplayMetrics().density * 48);
        if (Build.VERSION.SDK_INT > 19) {
            toolbar.setPadding(0, statusBarHeight, 0, 0);
            h += statusBarHeight;
        }
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(w, h);
        toolbar.setLayoutParams(params);
        if (activity instanceof AppCompatActivity) {
            ((AppCompatActivity) activity).setSupportActionBar(toolbar);
        }
        return toolbar;
    }

    public static int getStatusBarHeight(Activity activity) {
        int statusBarHeight = 0;
        try {
            Class<?> c = Class.forName("com.android.internal.R$dimen");
            Object obj = c.newInstance();
            Field field = c.getField("status_bar_height");
            int id = Integer.parseInt(field.get(obj).toString());
            statusBarHeight = activity.getResources().getDimensionPixelSize(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statusBarHeight;
    }

    public static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    /**
     * 获取app版本
     */
    public static int getAppVersion(Context context, String pkgName) {
        PackageInfo pkgInfo = null;
        try {
            pkgInfo = context.getPackageManager().getPackageInfo(pkgName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (pkgInfo == null) {
            return 0;
        }
        return pkgInfo.versionCode;
    }

    /**
     * 获取微信版本
     */
    public static int getWechatVersion(Context context) {
        return getAppVersion(context, Constants.PACKAGE_NAME_WECHAT);
    }

    /**
     * 屏幕是否亮起
     *
     * @return
     */
    public static boolean isScreenOn(Context context) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        return pm.isScreenOn();
    }

    /**
     * 是否聊天界面
     *
     * @param pageName
     *
     * @return
     */
    public static boolean isChatPage(String pageName) {
        return TextUtils.equals(pageName, Constants.ACTIVITY_NAME_CHAT);
    }

    /**
     * 是否聊天列表界面
     *
     * @param pageName
     *
     * @return
     */
    public static boolean isChatListPage(String pageName) {
        return isChatPage(pageName);
    }

    /**
     * 是否红包展示界面
     *
     * @param pageName
     *
     * @return
     */
    public static boolean isLuckyMoneyPage(String pageName) {
        return TextUtils.equals(pageName, Constants.ACTIVITY_NAME_LUCKY_MONEY);
    }

    /**
     * 是否红包详情界面
     *
     * @param pageName
     *
     * @return
     */
    public static boolean isLuckyMoneyDetailPage(String pageName) {
        return TextUtils.equals(pageName, Constants.ACTIVITY_NAME_LUCKY_MONEY_DETAIL);
    }

    public static boolean isAppInstalled(Context context, String packageName) {
        PackageManager packageManager = context.getPackageManager();
        try {
            packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 红包通知（语音，震动，静音）
     */
    public static void luckyMoneyNotify(Context context) {
        if (true) {
            final MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.notify);
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    if (mediaPlayer != null) {
                        mediaPlayer.release();
                    }
                }
            });
            mediaPlayer.start();
        } else {
            Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            long[] pattern = new long[] {300, 800, 300, 800};
            vibrator.vibrate(pattern, -1);
        }
    }

}
