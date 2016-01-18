package com.meiqia.meiqiasdk.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.meiqia.core.bean.MQAgent;
import com.meiqia.core.bean.MQMessage;
import com.meiqia.meiqiasdk.model.Agent;
import com.meiqia.meiqiasdk.model.BaseMessage;
import com.meiqia.meiqiasdk.model.PhotoMessage;
import com.meiqia.meiqiasdk.model.TextMessage;
import com.meiqia.meiqiasdk.model.VoiceMessage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MQUtils {
    private static Handler sHandler = new Handler();

    public static void runInThread(Runnable task) {
        new Thread(task).start();
    }

    public static void runInUIThread(Runnable task) {
        sHandler.post(task);
    }

    public static void runInUIThread(Runnable task, long delayMillis) {
        sHandler.postDelayed(task, delayMillis);
    }

    public static BaseMessage parseMQMessageIntoChatBase(MQMessage message, BaseMessage baseMessage) {
        int itemType;
        if (MQMessage.TYPE_FROM_CLIENT.equals(message.getFrom_type())) {
            itemType = BaseMessage.TYPE_CLIENT;
        } else {
            itemType = BaseMessage.TYPE_AGENT;
        }
        baseMessage.setStatus(message.getStatus());
        baseMessage.setItemViewType(itemType);
        baseMessage.setContent(message.getContent());
        baseMessage.setContentType(message.getContent_type());
        baseMessage.setStatus(message.getStatus());
        baseMessage.setId(message.getId());
        baseMessage.setAgentNickname(message.getAgent_nickname());
        baseMessage.setCreatedOn(message.getCreated_on());
        baseMessage.setAvatar(message.getAvatar());
        baseMessage.setIsRead(message.is_read());
        if (MQMessage.TYPE_CONTENT_PHOTO.equals(message.getContent_type())) {
            ((PhotoMessage) baseMessage).setUrl(message.getMedia_url());
        } else if (MQMessage.TYPE_CONTENT_VOICE.equals(message.getContent_type())) {
            ((VoiceMessage) baseMessage).setUrl(message.getMedia_url());
        }
        return baseMessage;
    }

    public static BaseMessage parseMQMessageIntoChatBase(MQMessage message) {
        BaseMessage baseMessage;
        int itemType;
        if (MQMessage.TYPE_FROM_CLIENT.equals(message.getFrom_type())) {
            itemType = BaseMessage.TYPE_CLIENT;
        } else {
            itemType = BaseMessage.TYPE_AGENT;
        }
        if (MQMessage.TYPE_CONTENT_PHOTO.equals(message.getContent_type())) {
            // message.getMedia_url() 可能是本地路径
            baseMessage = new PhotoMessage();
            if (isLocalPath(message.getMedia_url())) {
                ((PhotoMessage) baseMessage).setLocalPath(message.getMedia_url());
            } else {
                ((PhotoMessage) baseMessage).setUrl(message.getMedia_url());
            }
            baseMessage.setContent("[photo]");
        } else if (MQMessage.TYPE_CONTENT_VOICE.equals(message.getContent_type())) {
            baseMessage = new VoiceMessage(message.getMedia_url());
            // message.getMedia_url() 可能是本地路径
            if (isLocalPath(message.getMedia_url())) {
                ((VoiceMessage) baseMessage).setLocalPath(message.getMedia_url());
            } else {
                ((VoiceMessage) baseMessage).setUrl(message.getMedia_url());
            }
            baseMessage.setContent("[voice]");
        } else {
            baseMessage = new TextMessage(message.getContent());
            baseMessage.setContent(message.getContent());
        }
        baseMessage.setStatus(message.getStatus());
        baseMessage.setItemViewType(itemType);
        baseMessage.setContentType(message.getContent_type());
        baseMessage.setStatus(message.getStatus());
        baseMessage.setId(message.getId());
        baseMessage.setAgentNickname(message.getAgent_nickname());
        baseMessage.setCreatedOn(message.getCreated_on());
        baseMessage.setAvatar(message.getAvatar());
        baseMessage.setIsRead(message.is_read());
        return baseMessage;
    }

    /**
     * 将 MQMessage 转换为 BaseMessage
     *
     * @param mqMessageList 待转换的消息
     * @return 转换后的消息
     */
    public static List<BaseMessage> parseMQMessageToChatBaseList(List<MQMessage> mqMessageList) {
        List<BaseMessage> baseMessages = new ArrayList<BaseMessage>();
        for (MQMessage mqMessage : mqMessageList) {
            baseMessages.add(parseMQMessageIntoChatBase(mqMessage));
        }
        return baseMessages;
    }

    public static Agent parseMQAgentToAgent(MQAgent mqAgent) {
        if (mqAgent == null) return null;
        Agent agent = new Agent();
        agent.setId(mqAgent.getId());
        agent.setNickname(mqAgent.getNickname());
        return agent;
    }

    private static boolean isLocalPath(String path) {
        return !TextUtils.isEmpty(path) && !path.startsWith("http");
    }

    private static long lastClickTime;

    public synchronized static boolean isFastClick() {
        long time = System.currentTimeMillis();
        if (time - lastClickTime < 500) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

    /**
     * 判断是否有外部存储设备sdcard
     *
     * @return true | false
     */
    public static boolean isSdcardAvailable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static boolean isFileExist(String filePath) {
        boolean isFileExist;
        try {
            File file = new File(filePath);
            isFileExist = file.exists();
        } catch (Exception e) {
            isFileExist = false;
        }
        return isFileExist;
    }

    public static String getPicStorePath(Context ctx) {
        File file = ctx.getExternalFilesDir(null);
        if (!file.exists()) {
            file.mkdir();
        }
        File imageStoreFile = new File(file.getAbsolutePath() + "/mq");
        if (!imageStoreFile.exists()) {
            imageStoreFile.mkdir();
        }
        return imageStoreFile.getAbsolutePath();
    }

    /**
     * 获取状态栏高度
     *
     * @param context
     * @return
     */
    public static int getStatusBarHeight(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        return resources.getDimensionPixelSize(resourceId);
    }

    /**
     * 获取屏幕高度
     *
     * @param context
     * @return
     */
    public static int getScreenHeight(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(dm);
        return dm.heightPixels;
    }

    /**
     * 显示吐司
     *
     * @param context
     * @param text
     */
    public static void show(Context context, CharSequence text) {
        if (!TextUtils.isEmpty(text)) {
            if (text.length() < 10) {
                Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, text, Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * 显示吐司
     *
     * @param context
     * @param resId
     */
    public static void show(Context context, @StringRes int resId) {
        show(context, context.getResources().getString(resId));
    }

    /**
     * 在子线程中显示吐司时使用该方法
     *
     * @param context
     * @param text
     */
    public static void showSafe(final Context context, final CharSequence text) {
        runInUIThread(new Runnable() {
            @Override
            public void run() {
                show(context, text);
            }
        });
    }

    /**
     * 在子线程中显示吐司时使用该方法
     *
     * @param context
     * @param resId
     */
    public static void showSafe(Context context, @StringRes int resId) {
        showSafe(context, context.getResources().getString(resId));
    }

    /**
     * 拷贝文档到黏贴板
     *
     * @param text
     */
    public static void clip(Context context, String text) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            android.text.ClipboardManager clipboardManager = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            clipboardManager.setText(text);
        } else {
            ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            clipboardManager.setPrimaryClip(ClipData.newPlainText("mq_content", text));
        }
    }

    /**
     * 关闭activity中打开的键盘
     *
     * @param activity
     */
    public static void closeKeyboard(Activity activity) {
        View view = activity.getWindow().peekDecorView();
        if (view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * 关闭dialog中打开的键盘
     *
     * @param dialog
     */
    public static void closeKeyboard(Dialog dialog) {
        View view = dialog.getWindow().peekDecorView();
        if (view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) dialog.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * 打开键盘
     *
     * @param context
     * @param editText
     */
    public static void openKeyboard(final Context context, final EditText editText) {
        runInUIThread(new Runnable() {
            @Override
            public void run() {
                editText.requestFocus();
                editText.setSelection(editText.getText().toString().length());
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(editText, InputMethodManager.SHOW_FORCED);
            }
        }, 300);
    }
}