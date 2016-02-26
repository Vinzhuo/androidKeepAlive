package com.sml.zhuolin.smlkeepalive.library;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;

/**
 * Created by zhuolin on 15-8-14.
 */
public class KeepAlive {
    private static String TAG = "KeepAlive";
    private native static int startKeepAlive(Intent intent, String mil);
    static {
        try {
            System.loadLibrary("heartbeat");
        } catch (Throwable e) {
            Log.e(TAG, "loadLibrary : " + e.getMessage());
        }
    }

    private static boolean myShell(String strDir, String strCmd, StringBuffer paramStringBuffer)
    {
        try
        {
            Process localProcess = Runtime.getRuntime().exec("sh");
            DataInputStream localDataInputStream = new DataInputStream(localProcess.getInputStream());
            DataOutputStream localDataOutputStream = new DataOutputStream(localProcess.getOutputStream());
            localDataOutputStream.writeBytes("cd " + strDir + "\n");
            localDataOutputStream.writeBytes(strCmd + " &\n");
		      localDataOutputStream.writeBytes("exit\n");
            localDataOutputStream.flush();
		      localProcess.waitFor();
            return true;
        }
        catch (Exception e)
        {
            paramStringBuffer.append("Exception:" + e.getMessage());
        }
        return false;
    }

    private static void prepareExecuteFile(String pageName) {
        String strSoFilenamePath = "/data/data/" + pageName + "/lib/" + "libkeepalive.so";
        String strBinFilenamePath = "/data/data/" + pageName + "/" + "libkeepalive";
        String dir = "/data/data/" + pageName + "/";
        StringBuffer sBuf = new StringBuffer();
        myShell(dir, "dd if=" + strSoFilenamePath + " of=" + strBinFilenamePath, sBuf);
        myShell(dir, "chmod 500 " + strBinFilenamePath, sBuf);
    }


    public static int start(Context context, Intent intent, int sec) {
        if (context == null || intent == null) {
            return -1;
        }
        prepareExecuteFile(context.getPackageName());
        if (sec == 0) {
            sec = 8;
        }
        int pid = 0;
        try {
            pid = startKeepAlive(intent, String.valueOf(sec));
        } catch (Throwable throwable) {
            Log.e(TAG, throwable.getMessage());
        }
        return pid;
    }

}
