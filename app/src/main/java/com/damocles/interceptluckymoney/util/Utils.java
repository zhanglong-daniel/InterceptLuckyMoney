package com.damocles.interceptluckymoney.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.PowerManager;
import android.text.TextUtils;

/**
 * Created by danielzhang on 16/2/14.
 */
public class Utils {

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

}
