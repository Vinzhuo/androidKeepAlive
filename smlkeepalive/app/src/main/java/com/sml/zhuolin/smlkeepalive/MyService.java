package com.sml.zhuolin.smlkeepalive;

import android.app.Notification;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import com.sml.zhuolin.smlkeepalive.library.IntentUtils;
import com.sml.zhuolin.smlkeepalive.library.KeepAliveUtils;

public class MyService extends Service {
    public static final String KEEP_ALIVE_ACTION = "com.sml.zhuolin.test.Action";
    private static final int ONGONGING = 10001;
    public MyService() {
    }
    BroadcastReceiver receiver;

    @Override
    public void onCreate() {
        super.onCreate();
        this.receiver = new ScreenReciver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        intentFilter.addAction(Intent.ACTION_USER_PRESENT);
        this.registerReceiver(receiver, intentFilter);
        keepForground();
        KeepAliveUtils.keepAlive(this, MyService.class, MyService.KEEP_ALIVE_ACTION);
    }

    private void keepForground() {
        try {
            Notification notification = new Notification();
            notification.flags |= Notification.FLAG_NO_CLEAR;
            notification.flags |= Notification.FLAG_ONGOING_EVENT;
            startForeground(0, notification); // 设置为前台服务避免kill，Android4.3及以上需要设置id为0时通知栏才不显示该通知；
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static void start(Context context) {
        if (context == null) {
            return;
        }
        Intent intent = new Intent(context, MyService.class);
        context.startService(intent);
    }

    private static class ScreenReciver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (!IntentUtils.isValid(intent)) {
                return;
            }
            String action = intent.getAction();
            if (Intent.ACTION_SCREEN_OFF.equals(action)) {
                KeepAliveUtils.stopAlive();
            } else if (Intent.ACTION_SCREEN_ON.equals(action)) {
                KeepAliveUtils.keepAlive(context, MyService.class, MyService.KEEP_ALIVE_ACTION);
            } else if (Intent.ACTION_USER_PRESENT.equals(action)) {
                KeepAliveUtils.keepAlive(context, MyService.class, MyService.KEEP_ALIVE_ACTION);
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        if (intent != null && IntentUtils.isValid(intent)) {
            String action = intent.getAction();
            if (KEEP_ALIVE_ACTION.equals(action)) {
                KeepAliveUtils.keepAlive(this, this.getClass(), KEEP_ALIVE_ACTION);
            }
        }
        Log.e("keepalive", "onstartCommand");
        return START_NOT_STICKY;
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
//        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(receiver);
    }
}
