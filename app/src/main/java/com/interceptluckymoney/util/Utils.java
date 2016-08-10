package com.interceptluckymoney.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

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

}
