package com.example.h6launcher;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {
    private static final String TAG = "BootReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction()) ||
            "android.intent.action.QUICKBOOT_POWERON".equals(intent.getAction())) {
            
            Log.i(TAG, "Boot completed, starting launcher");
            
            new Handler().postDelayed(() -> {
                Intent launcherIntent = new Intent(context, MainActivity.class);
                launcherIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(launcherIntent);
            }, 2000);
        }
    }
}
