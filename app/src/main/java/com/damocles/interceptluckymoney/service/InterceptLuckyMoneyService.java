package com.damocles.interceptluckymoney.service;

import java.util.List;

import com.damocles.interceptluckymoney.util.AccessibilityUtils;
import com.damocles.interceptluckymoney.MainActivity;
import com.damocles.interceptluckymoney.util.Utils;

import android.accessibilityservice.AccessibilityService;
import android.app.Notification;
import android.app.PendingIntent;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
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
    private boolean isAutoOpen = false;

    /**
     * 当前页面名称
     */
    private String currentPage = "";

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
        Log.e(LOG_TAG, "onServiceConnected()");
        super.onServiceConnected();
        isRunning = true;
        MainActivity.updateRunningState();
        Toast.makeText(this, "自动抢红包服务已开启", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        Log.e(LOG_TAG, "onDestroy()");
        super.onDestroy();
        isRunning = false;
        MainActivity.updateRunningState();
        Toast.makeText(this, "自动抢红包服务已关闭", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        Log.d(LOG_TAG, "onAccessibilityEvent() pageName = " + event.getClassName().toString());
        switch (event.getEventType()) {
            //通知栏收到新通知消息
            case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:
                currentPage = event.getClassName().toString();
                notifyStateChanged(event);
                break;
            // 当前窗口发生变化
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                currentPage = event.getClassName().toString();
                mHandler.removeCallbacks(autoClickLuckyMoneyMsgRunnable);
                windowStateChanged(event);
                break;
            // 当前窗口的内容发生变化
            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
                windowContentChanged(event);
                break;
            default:
                currentPage = event.getClassName().toString();
                break;
        }
    }

    private void notifyStateChanged(AccessibilityEvent event) {
        boolean isLuckyMoneyNotify = AccessibilityUtils.isLuckyMoneyNotify(event);
        Log.i(LOG_TAG, "收到消息通知，是否是红包消息 : " + isLuckyMoneyNotify);
        // 不是红包消息，忽略
        if (!isLuckyMoneyNotify) {
            return;
        }
        // 是红包消息，但当前屏幕关闭，tts提醒
        if (!Utils.isScreenOn(this)) {
            // TODO:tts提醒
            Log.e(LOG_TAG, "收到红包消息，但屏幕关闭，忽略");
            return;
        }
        // 收到红包消息通知，且屏幕亮起，执行通知点击动作
        Notification notification = (Notification) event.getParcelableData();
        PendingIntent pendingIntent = notification.contentIntent;
        try {
            isReceiving = true;
            pendingIntent.send();
            mHandler.postDelayed(autoClickLuckyMoneyMsgRunnable, 500L);
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
            Log.e(LOG_TAG, "执行红包notify消息点击动作失败");
            isReceiving = false;
        }
    }

    private void windowStateChanged(AccessibilityEvent event) {
        String pageName = event.getClassName().toString();
        if (Utils.isLuckyMoneyDetailPage(pageName)) { // 红包详情页
            Log.i(LOG_TAG, "当前window发生变化，当前界面为：红包详情页");
            // 如果是自动打开详情页，则直接返回主界面
            // 如果是用户手动打开，不处理
            if (isAutoOpen) {
                Log.i(LOG_TAG, "自动打开红包详情页，直接返回");
                isAutoOpen = false;
                doubleBack();
            } else {
                Log.e(LOG_TAG, "用户手动打开红包详情页，不处理");
            }
        } else if (Utils.isLuckyMoneyPage(pageName)) { // 红包页面
            Log.i(LOG_TAG, "当前window发生变化，当前界面为：红包页面");
            clickLuckyMoneyButton();
        } else if (Utils.isChatPage(pageName)) {   // 聊天列表或对话页面
            Log.i(LOG_TAG, "当前window发生变化，当前界面为：聊天列表或对话页面 isReceiving = " + isReceiving);
            if (isReceiving) {
                clickLuckyMoneyMsg(false);
            }
        } else {
            Log.i(LOG_TAG, "当前window发生变化，当前界面为：" + pageName);
        }
    }

    private void windowContentChanged(AccessibilityEvent event) {
        // TODO:待补充
    }

    /**
     * 点击红包界面的“开”按钮
     */
    private void clickLuckyMoneyButton() {
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if (nodeInfo == null) {
            Log.e(LOG_TAG, "rootWindow为空");
            return;
        }
        // 查找“开”按钮
        AccessibilityNodeInfo buttonNode = null;
        for (int i = 0, len = nodeInfo.getChildCount(); i < len; i++) {
            AccessibilityNodeInfo node = nodeInfo.getChild(i);
            if (TextUtils.equals("android.widget.Button", node.getClassName())) {
                buttonNode = node;
                break;
            }
        }
        if (buttonNode != null) {   // 红包界面找到“开”按钮，执行click动作
            AccessibilityUtils.performClick(buttonNode);
        } else {    // 红包界面未找到“开”按钮，红包已打开过或者已被抢完
            // 如果是自动进入该界面，返回
            // 如果是用户手动打开，不处理
            if (isAutoOpen) {
                Log.i(LOG_TAG, "用户手动打开红包，未发现“开”按钮，直接返回");
                isAutoOpen = false;
                doubleBack();
            } else {
                Log.e(LOG_TAG, "用户手动打开红包详情页，不处理");
            }
        }
    }

    /**
     * 点击红包消息(消息列表页或者聊天对话页面)
     */
    private void clickLuckyMoneyMsg(boolean isChatPage) {
        Log.i(LOG_TAG, "clickLuckyMoneyMsg()   isChatPage = " + isChatPage);
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if (nodeInfo == null) {
            Log.e(LOG_TAG, "rootWindow为空");
            return;
        }
        // 确定在对话界面
        if (isChatPage) {
            Log.i(LOG_TAG, "目前在聊天对话界面，直接搜索\"领取红包\"关键字");
            List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByText("领取红包");
            if (!list.isEmpty()) {
                Log.i(LOG_TAG, "搜索到\"领取红包\"关键字");
                if (isReceiving) {
                    isReceiving = false;
                    isAutoOpen = true;
                    // 打开最后一个红包
                    AccessibilityNodeInfo node = list.get(list.size() - 1);
                    AccessibilityUtils.performClick(node);
                }
            } else {
                Log.e(LOG_TAG, "未搜索到\"领取红包\"关键字");
            }
        } else {
            List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByText("[微信红包]");
            if (!list.isEmpty()) {
                Log.i(LOG_TAG, "搜索到\"[微信红包]\"关键字，在聊天列表页");
                AccessibilityNodeInfo node = list.get(0);
                if (node != null && isReceiving) {
                    AccessibilityUtils.performClick(node);
                    // 进入聊天对话界面后，再次点击红包消息
                    clickLuckyMoneyMsg(true);
                }
            } else {
                Log.i(LOG_TAG, "未搜索到\"[微信红包]\"关键字，可能在聊天对话页面或者没有收到红包");
                list = nodeInfo.findAccessibilityNodeInfosByText("领取红包");
                if (!list.isEmpty()) {
                    Log.i(LOG_TAG, "搜索到\"领取红包\"关键字，在聊天对话页面");
                    if (isReceiving) {
                        isReceiving = false;
                        isAutoOpen = true;
                        // 打开最后一个红包
                        AccessibilityNodeInfo node = list.get(list.size() - 1);
                        AccessibilityUtils.performClick(node);
                    }
                } else {
                    Log.e(LOG_TAG, "未搜索到\"领取红包\"关键字，没有收到红包");
                }
            }
        }
    }

    private void doubleBack() {
        performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
            }
        }, 500L);
    }

    private Runnable autoClickLuckyMoneyMsgRunnable = new Runnable() {

        @Override
        public void run() {
            Log.e(LOG_TAG, "自动执行红包消息点击动作。。。。。。。。。。");
            clickLuckyMoneyMsg(false);
        }
    };
}
