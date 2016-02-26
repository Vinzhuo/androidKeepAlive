package com.sml.zhuolin.smlkeepalive.library;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;


import java.util.List;

/**
 * Created by zhuolin on 15-9-14.
 */
public class KeepAliveUtils {

    private static int keepAlivePid = 0;

    public static void stopAlive() {
        if (Build.VERSION.SDK_INT > 19) {
            return;
        }
        if (getKeepAlivePid() > 0) {
            ProcessUtils.killProcess(keepAlivePid);
            keepAlivePid = 0;
        }
    }

    public static void keepAlive(Context context, Class service, @NonNull String action) {
        if (Build.VERSION.SDK_INT > 19 || getKeepAlivePid() > 0) {
            return;
        }
        Intent intent = new Intent(context, service);
        if (!TextUtils.isEmpty(action)) {
            intent.setAction(action);
        }
        int pid = KeepAlive.start(context, intent, 10);
        if (pid <= 0) {
            return;
        }
        keepAlivePid = pid;
    }

    private static int getKeepAlivePid() {
        if (keepAlivePid > 0) {
            return keepAlivePid;
        }
        List<Integer> childpids = ProcessUtils.getChildPid(android.os.Process.myPid());
        if(childpids.size() > 0) {
            keepAlivePid = childpids.get(0);
            return keepAlivePid;
        }
        return -1;
    }
}
