package com.sml.zhuolin.smlkeepalive;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.sml.zhuolin.smlkeepalive.library.IntentUtils;

public class BootReceiver extends BroadcastReceiver {
    public BootReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        if (!IntentUtils.isValid(intent)) {
            return;
        }
        MyService.start(context);
    }
}
