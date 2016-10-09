package com.damocles.interceptluckymoney.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by zhanglong02 on 16/10/4.
 */
public class SharedPreferencesUtil {

    private SharedPreferences sharedPreferences;

    private SharedPreferencesUtil() {

    }

    private static class InstanceHolder {
        private static final SharedPreferencesUtil INSTANCE = new SharedPreferencesUtil();
    }

    public static SharedPreferencesUtil getInstance() {
        return InstanceHolder.INSTANCE;
    }

    public void putNotifySwitch(Context context, boolean isChecked) {
        putBoolean(context, "switch_notify", isChecked);

    }

    public boolean getNotifySwitch(Context context) {
        return getBoolean(context, "switch_notify", true);
    }

    public void putVibrateSwitch(Context context, boolean isChecked) {
        putBoolean(context, "switch_vibrate", isChecked);
    }

    public boolean getVibrateSwitch(Context context) {
        return getBoolean(context, "switch_vibrate", false);
    }

    public void putSoundSwitch(Context context, boolean isChecked) {
        putBoolean(context, "switch_sound", isChecked);
    }

    public boolean getSoundSwitch(Context context) {
        return getBoolean(context, "switch_sound", true);
    }

    private void putBoolean(Context context, String key, boolean value) {
        initSharedPreferences(context);
        sharedPreferences.edit().putBoolean(key, value).commit();
    }

    private boolean getBoolean(Context context, String key, boolean defValue) {
        initSharedPreferences(context);
        return sharedPreferences.getBoolean(key, defValue);
    }

    private void initSharedPreferences(Context context) {
        if (sharedPreferences == null) {
            sharedPreferences = context.getSharedPreferences("prefs_setting", Context.MODE_PRIVATE);
        }
    }

}
