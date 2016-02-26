package com.sml.zhuolin.smlkeepalive;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.sml.zhuolin.smlkeepalive.library.IntentUtils;
import com.sml.zhuolin.smlkeepalive.library.KeepAliveUtils;

public class MyService extends Service {
    public static final String KEEP_ALIVE_ACTION = "com.sml.zhuolin.test.Action";
    public MyService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
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
}
