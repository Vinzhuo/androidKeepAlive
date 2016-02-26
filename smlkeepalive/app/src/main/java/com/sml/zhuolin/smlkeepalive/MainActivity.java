package com.sml.zhuolin.smlkeepalive;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.sml.zhuolin.smlkeepalive.library.KeepAliveUtils;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void start(View view) {
        KeepAliveUtils.keepAlive(this, MyService.class, MyService.KEEP_ALIVE_ACTION);
    }

}
