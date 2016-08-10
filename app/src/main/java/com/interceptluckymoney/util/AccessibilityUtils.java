package com.interceptluckymoney.util;

import android.view.accessibility.AccessibilityNodeInfo;

/**
 * Created by danielzhang on 16/2/15.
 */
public class AccessibilityUtils {

    /**
     * 执行点击事件
     */
    public static void performClick(AccessibilityNodeInfo nodeInfo) {
        if (nodeInfo == null) {
            return;
        }
        if (nodeInfo.isClickable()) {
            nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
        } else {
            performClick(nodeInfo.getParent());
        }
    }
}
