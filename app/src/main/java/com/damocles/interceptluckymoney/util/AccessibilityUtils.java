package com.damocles.interceptluckymoney.util;

import java.util.List;

import android.view.accessibility.AccessibilityEvent;
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

    /**
     * 判断是否是红包通知
     *
     * @return
     */
    public static boolean isLuckyMoneyNotify(AccessibilityEvent event) {
        List<CharSequence> texts = event.getText();
        if (texts.isEmpty()) {
            return false;
        }
        String text = String.valueOf(texts.get(0));
        return text.contains("[微信红包]");
    }
}
