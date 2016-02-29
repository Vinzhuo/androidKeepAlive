package com.sml.zhuolin.smlkeepalive;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.sml.zhuolin.smlkeepalive.library.KeepAliveUtils;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void start(View view) {
        boolean result = KeepAliveUtils.keepAlive(this, MyService.class, MyService.KEEP_ALIVE_ACTION);
        if (result) {
            Toast.makeText(this, R.string.keepalive, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, R.string.keepalivefailure, Toast.LENGTH_SHORT).show();
        }
        finish();
    }

}
