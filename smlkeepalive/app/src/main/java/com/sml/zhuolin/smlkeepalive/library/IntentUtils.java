package com.sml.zhuolin.smlkeepalive.library;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

/**
 * Created by zhuolin on 2015/8/11.
 */
public class IntentUtils {

    private static final String TAG = "IntentUtils";
    public static final String WEBVIEW_URL = "WEBVIEW_URL";
    public static final String WEBVIEW_TITLE = "WEBVIEW_TITLE";

    /**
     * Is intent valid or invalid
     * @param intent
     * @return true valid, false invalid
     */
    public static final boolean isValid(Intent intent) {
        if (intent == null) {
            return false;
        }
        try {
            intent.hasExtra("");
        } catch (Exception ClassNotFoundException) {
            return false;
        }
        return true;
    }

    public static void goLauncher(Context context) {
        try {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Log.w(TAG, e.toString());
            if (context != null && context instanceof Activity) {
                ((Activity) context).finish();
            }
        }

    }

    public static void goApp(Context context, String name) {
        PackageManager packageManager = context.getPackageManager();
        try {
            Intent i = packageManager.getLaunchIntentForPackage(name);
            if (i != null) {
                context.startActivity(i);
            }
        } catch (Exception e1) {
            try {
                PackageInfo pi = packageManager.getPackageInfo(name, 0);

                Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
                resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                resolveIntent.setPackage(pi.packageName);

                List<ResolveInfo> apps = packageManager.queryIntentActivities(resolveIntent, 0);

                ResolveInfo ri = apps.iterator().next();
                if (ri != null ) {
                    String packageName = ri.activityInfo.packageName;
                    String className = ri.activityInfo.name;

                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_LAUNCHER);

                    ComponentName cn = new ComponentName(packageName, className);

                    intent.setComponent(cn);
                    context.startActivity(intent);
                }
            } catch (Exception e2) {
                Log.e(TAG, e2.toString());
            }
        }
    }

    public static void gotoSite(Activity context,  Class cls, String url, String title) {
        Intent intent = new Intent(context, cls);
        intent.putExtra(WEBVIEW_URL, url);
        intent.putExtra(WEBVIEW_TITLE, title);
        context.startActivity(intent);
    }

    public static void openSettingAppInfo(Activity activity, String pkgname) {
        Uri packageURI = Uri.parse("package:" + pkgname);
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,packageURI);
        activity.startActivity(intent);
    }

    public static void openGooglePlus(Context context, String profile, int textId) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setClassName("com.google.android.apps.plus",
                    "com.google.android.libraries.social.gateway.GatewayActivity");
            intent.putExtra("customAppUri", profile);
            context.startActivity(intent);
        } catch(Exception e) {
            try {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://plus.google.com/" + profile + "/posts")));
            } catch (Exception e1) {
                Toast.makeText(context, textId, Toast.LENGTH_SHORT).show();
                Log.e(TAG, e1.toString());
            }
        }
    }

    public static void openTwitter(Context context, String twtrName, int textId) {
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name=" + twtrName)));
        } catch (Exception e) {
            try {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/" + twtrName)));
            } catch (Exception e1) {
                Toast.makeText(context, textId, Toast.LENGTH_SHORT).show();
                Log.e(TAG, e1.toString());
            }
        }
    }

    public static void openFacebook(Context context, String facebookId, int textId) {
        try{
            String facebookScheme = "fb://page/" + facebookId;
            Intent facebookIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(facebookScheme));
            context.startActivity(facebookIntent);
        } catch (Exception e) {
            try {
                Intent facebookIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/" + facebookId));
                context.startActivity(facebookIntent);
            } catch (Exception e1) {
                Toast.makeText(context, textId, Toast.LENGTH_SHORT).show();
                Log.e(TAG, e1.toString());
            }
        }
    }

}
