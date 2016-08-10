package com.interceptluckymoney.service;

import java.util.List;

import com.interceptluckymoney.util.AccessibilityUtils;
import com.interceptluckymoney.util.Constants;
import com.interceptluckymoney.MainActivity;

import android.accessibilityservice.AccessibilityService;
import android.app.Notification;
import android.app.PendingIntent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

/**
 * Created by danielzhang on 16/2/14.
 */
public class InterceptLuckyMoneyService extends AccessibilityService {

    private static final String LOG_TAG = "intercept_lucky_money";

    /**
     * 服务是否在运行
     */
    private static boolean isRunning = false;

    /**
     * 是否收到新红包
     */
    private static boolean isReceiving = false;

    /**
     * 是否自动进入红包详情
     */
    private boolean isAutoOpenDetail = false;

    private CharSequence currentWindow = "";

    private Handler mHandler = new Handler(Looper.getMainLooper());

    public static boolean getRunningState() {
        return isRunning;
    }

    @Override
    public void onInterrupt() {
        Log.i(LOG_TAG, "onInterrupt()");
    }

    @Override
    protected boolean onKeyEvent(KeyEvent event) {
        Log.i(LOG_TAG, "onKeyEvent()");
        return super.onKeyEvent(event);
    }

    @Override
    protected void onServiceConnected() {
        Log.i(LOG_TAG, "onServiceConnected()");
        super.onServiceConnected();
        isRunning = true;
        MainActivity.updateRunningState();
        Toast.makeText(this, "自动抢红包服务已开启", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        Log.i(LOG_TAG, "onDestroy()");
        super.onDestroy();
        isRunning = false;
        MainActivity.updateRunningState();
        Toast.makeText(this, "自动抢红包服务已关闭", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        final int eventType = event.getEventType();
        String eventTypeStr = String.valueOf(eventType);
        switch (eventType) {
            case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:
                eventTypeStr = "TYPE_NOTIFICATION_STATE_CHANGED";
                break;
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                eventTypeStr = "TYPE_WINDOW_STATE_CHANGED";
                break;
            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
                eventTypeStr = "TYPE_WINDOW_CONTENT_CHANGED";
                break;
            default:
                break;
        }
        Log.i(LOG_TAG, "onAccessibilityEvent() : " + eventTypeStr);
        switch (eventType) {
            //通知栏收到新通知消息
            case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:
                handleNotificationEvent(event);
                break;
            // 当前窗口发生变化
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                handleLuckyMoney(event);
                break;
            // 当前窗口的内容发生变化
            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
                Log.e("fuck", event.toString());
                if (Constants.ACTIVITY_NAME_CHAT.equals(event.getClassName())) { //在聊天界面或聊天列表，处理
                    //                    if (isReceiving) {
                    Log.e("fuck", event.getText().get(0) + "");
                    handleLuckyMoney(event);
                    //                    }
                }
                break;
            default:
                break;
        }
    }

    /**
     * 处理通知栏事件
     *
     * @param event
     */
    private void handleNotificationEvent(AccessibilityEvent event) {
        Log.i(LOG_TAG, "handleNotificationEvent()");
        List<CharSequence> texts = event.getText();
        if (texts.isEmpty()) {
            return;
        }
        String text = String.valueOf(texts.get(0));
        Log.e(LOG_TAG, "收到一条微信消息 --> " + text);
        int index = text.indexOf(":");
        if (index != -1) {
            text = text.substring(index + 1);
        }
        text = text.trim();
        // 收到微信红包通知，直接打开
        if (text.contains("[微信红包]")) {
            // TODO:暂未处理手机锁屏等情况
            Notification notification = (Notification) event.getParcelableData();
            PendingIntent pendingIntent = notification.contentIntent;
            try {
                isReceiving = true;
                pendingIntent.send();
                // 收到消息时，正在消息列表页
                if (Constants.ACTIVITY_NAME_CHAT.equals(currentWindow)) {
                    clickLuckyMoney();
                }
            } catch (PendingIntent.CanceledException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 打开微信红包流程
     *
     * @param event
     */
    private void handleLuckyMoney(AccessibilityEvent event) {
        CharSequence className = event.getClassName();
        Log.e(LOG_TAG, "handleLuckyMoney() : className = " + className);
        currentWindow = className;
        // 1，在聊天列表页或者群聊界面,点击“领取红包”
        if (Constants.ACTIVITY_NAME_CHAT.equals(className)) {
            clickLuckyMoney();
        }
        // 2，点击领取红包后，进入红包展示页，点击“开”按钮
        else if (Constants.ACTIVITY_NAME_LUCKY_MONEY.equals(className)) {
            openLuckyMoney();
        }
        // 3，点击“开”按钮后，进入红包详情页，自动回到主界面
        else if (Constants.ACTIVITY_NAME_LUCKY_MONEY_DETAIL.equals(className)) {
            //            mCurrentWindow = WINDOW_LUCKYMONEY_DETAIL;
            // 如果是自动打开详情页，则直接返回主界面
            if (isAutoOpenDetail) {
                isAutoOpenDetail = false;
                back();
            }
        }
    }

    /**
     * 点击红包消息
     */
    private void clickLuckyMoney() {
        Log.e(LOG_TAG, "clickLuckyMoney()");
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if (nodeInfo == null) {
            Log.w(LOG_TAG, "rootWindow为空");
            return;
        }
        // 查找“领取红包”关键字（聊天页面）
        List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByText("领取红包");
        if (!list.isEmpty()) { // 聊天页面找到“领取红包”，领取最新的红包
            if (isReceiving) {
                isReceiving = false;
                isAutoOpenDetail = true;
                AccessibilityNodeInfo node = list.get(list.size() - 1);
                AccessibilityUtils.performClick(node);
            }
        } else { // 未找到“领取红包”，查找"[微信红包]"关键字（聊天列表）
            list = nodeInfo.findAccessibilityNodeInfosByText("[微信红包]");
            if (!list.isEmpty()) { // 聊天列表出现新红包
                AccessibilityNodeInfo node = list.get(0);
                if (node != null) {
                    isReceiving = true;
                    AccessibilityUtils.performClick(node);
                    clickLuckyMoney();
                }
            }
        }
    }

    /**
     * 打开红包
     */
    private void openLuckyMoney() {
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if (nodeInfo == null) {
            Log.w(LOG_TAG, "rootWindow为空");
            return;
        }
        // 查找“开”按钮
        AccessibilityNodeInfo buttonNode = null;
        for (int i = 0; i < nodeInfo.getChildCount(); i++) {
            AccessibilityNodeInfo node = nodeInfo.getChild(i);
            if ("android.widget.Button".equals(node.getClassName())) {
                buttonNode = node;
                break;
            }
        }
        if (buttonNode == null) {
            if (isAutoOpenDetail) {
                isAutoOpenDetail = false;
                back();
            }
            return;
        }
        AccessibilityUtils.performClick(buttonNode);
    }

    private void back() {
        performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
            }
        }, 300L);
    }
}
