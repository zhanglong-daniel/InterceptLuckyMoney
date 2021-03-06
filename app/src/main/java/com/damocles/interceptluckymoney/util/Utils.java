package com.damocles.interceptluckymoney.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;

import com.damocles.interceptluckymoney.R;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.PowerManager;
import android.os.Vibrator;
import android.text.TextUtils;

/**
 * Created by danielzhang on 16/2/14.
 */
public class Utils {

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

    public static int getNavigationBarHeight(Activity activity) {
        int navigationHeight = 0;
        try {
            Class<?> c = Class.forName("com.android.internal.R$dimen");
            Object obj = c.newInstance();
            Field field = c.getField("navigation_bar_height");
            int id = Integer.parseInt(field.get(obj).toString());
            navigationHeight = activity.getResources().getDimensionPixelSize(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return navigationHeight;
    }

    public static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    public static float getDensity(Context context) {
        return context.getResources().getDisplayMetrics().density;
    }

    public static int dp2Px(Context context, int dp) {
        return (int) (dp * getDensity(context));
    }

    /**
     * 读取assets中的channel，获得app渠道号
     */
    public static String getChannel(Context context) {
        InputStream inputStream = null;
        String channel = null;
        try {
            inputStream = context.getAssets().open("channel");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            channel = new String(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return channel;
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
        if (SharedPreferencesUtil.getInstance().getNotifySwitch(context)) {
            // vibrate
            if (SharedPreferencesUtil.getInstance().getVibrateSwitch(context)) {
                Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                long[] pattern = new long[] {300, 800, 300, 800};
                vibrator.vibrate(pattern, -1);
            }
            // sound
            if (SharedPreferencesUtil.getInstance().getSoundSwitch(context)) {
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
            }
        }
    }

}
