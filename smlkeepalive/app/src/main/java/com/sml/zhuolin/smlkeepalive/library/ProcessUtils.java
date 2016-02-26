package com.sml.zhuolin.smlkeepalive.library;

import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhuolin on 15-7-30.
 */
public class ProcessUtils {


    private static final String TAG = "ProcessUtil";

    public static void killProcess(int pid) {
        android.os.Process.killProcess(pid);
    }

    private static boolean containString(String [] strings, String string) {
        if (TextUtils.isEmpty(string)) {
            return false;
        }
        for (String s : strings) {
            if (string.equals(s)) {
                return true;
            }
        }
        return false;
    }

    public static List<Integer> getChildPid(int pid) {
        String cmd = "ps | grep " + pid;
        Process localProcess;
        BufferedReader in = null;
        DataOutputStream localDataOutputStream = null;
        List<Integer> childPids = new ArrayList<>(1);
        try {
            localProcess = Runtime.getRuntime().exec("sh");
            in = new BufferedReader(new InputStreamReader(localProcess.getInputStream()));
            localDataOutputStream = new DataOutputStream(localProcess.getOutputStream());
            localDataOutputStream.writeBytes(cmd + " &\n");
            localDataOutputStream.flush();
            localDataOutputStream.writeBytes("exit\n");
            localProcess.waitFor();
            String line;
            String[]temp;
            while ((line = in.readLine()) != null) {
                line = line.replaceAll("\\s+", " ");//替换多个空格为单个空格
                temp = line.split(" ");
                if (temp.length >= 3 && !TextUtils.isEmpty(temp[2]) && temp[2].trim().equals(String.valueOf(pid))) {
                    if (!TextUtils.isEmpty(temp[1])) {
                        childPids.add(Integer.valueOf(temp[1].trim()));
                    }
                }
            }

        } catch (IOException | InterruptedException e) {
            Log.e(TAG, e.getMessage());
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (localDataOutputStream != null) {
                    localDataOutputStream.close();
                }
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            }
        }
        return childPids;
    }

}
