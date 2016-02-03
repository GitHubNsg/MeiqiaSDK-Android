package com.meiqia.meiqiasdk.util;

import android.support.annotation.ColorRes;


public class MQConfig {
    public static final int DEFAULT = -1;

    public static boolean isVoiceSwitchOpen = true; // 语音开关
    public static boolean isSoundSwitchOpen = true; // 声音开关
    public static boolean isLoadMessagesFromNativeOpen = false; // 加载本地数据开关
    @ColorRes
    public static int bgColorTitle = DEFAULT; // 标题栏背景颜色
    @ColorRes
    public static int textColorTitle = DEFAULT; // 标题栏文字颜色
    @ColorRes
    public static int bgColorChatBubbleLeft = DEFAULT; // 左边气泡背景颜色
    @ColorRes
    public static int bgColorChatBubbleRight = DEFAULT; // 右边气泡背景颜色
    @ColorRes
    public static int textColorChatBubbleLeft = DEFAULT; // 左边气泡文字颜色
    @ColorRes
    public static int textColorChatBubbleRight = DEFAULT; // 右边气泡文字颜色
}

