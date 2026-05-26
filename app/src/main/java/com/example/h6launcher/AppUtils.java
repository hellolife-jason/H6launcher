package com.example.h6launcher;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class AppUtils {
    private static final String TAG = "AppUtils";

    public static List<AppInfo> getInstalledApps(Context context) {
        List<AppInfo> apps = new ArrayList<>();
        PackageManager pm = context.getPackageManager();
        
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        
        List<ResolveInfo> resolveInfos = pm.queryIntentActivities(mainIntent, 0);
        
        for (ResolveInfo info : resolveInfos) {
            try {
                String packageName = info.activityInfo.packageName;
                String className = info.activityInfo.name;
                String label = info.loadLabel(pm).toString();
                
                if (label == null || label.isEmpty()) {
                    label = packageName;
                }
                
                AppInfo appInfo = new AppInfo();
                appInfo.setPackageName(packageName);
                appInfo.setClassName(className);
                appInfo.setLabel(label);
                appInfo.setIcon(info.loadIcon(pm));
                
                apps.add(appInfo);
            } catch (Exception e) {
                Log.e(TAG, "Error loading app info", e);
            }
        }
        
        return apps;
    }

    public static void launchApp(Context context, String packageName, String className) {
        try {
            Intent intent = new Intent();
            intent.setClassName(packageName, className);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, "Failed to launch app: " + packageName, e);
        }
    }

    public static boolean isSystemApp(ResolveInfo info) {
        return (info.activityInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0;
    }
}
