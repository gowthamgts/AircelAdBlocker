package com.gts.airceladblocker;

import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.provider.CallLog;
import android.util.Log;

/**
 * Created by GTS on 04-02-2015.
 */
public class BCastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (action.equals("com.gts.airceladblocker.ACTION_BCAST")) {
            Log.i("Debug", "Received a calling bcast...");
            // Read the prefs and clear the status.
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("isCallMade", false);
            editor.commit();
            String ph = sharedPreferences.getString("userphonenumber", null);
            if (ph !=null ) {
                PowerManager pm = (PowerManager) context
                        .getSystemService(Context.POWER_SERVICE);
                PowerManager.WakeLock wakeLock = pm.newWakeLock((PowerManager.SCREEN_BRIGHT_WAKE_LOCK
                        | PowerManager.FULL_WAKE_LOCK
                        | PowerManager.ACQUIRE_CAUSES_WAKEUP), "TAG");
                wakeLock.acquire();
                KeyguardManager keyguardManager = (KeyguardManager) context
                        .getSystemService(Context.KEYGUARD_SERVICE);
                KeyguardManager.KeyguardLock keyguardLock =  keyguardManager.newKeyguardLock("TAG");
                keyguardLock.disableKeyguard();
                Intent i = new Intent(Intent.ACTION_CALL);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.setData(Uri.parse("tel:" + ph));
                i.putExtra("com.android.phone.extra.slot", Integer.parseInt(sharedPreferences.getString("simslot", null)));
                i.putExtra("simSlot", Integer.parseInt(sharedPreferences.getString("simslot", null)));
                context.startActivity(i);
                editor.putBoolean("isCallMade", true);
                editor.commit();
                //deleting the log
                String queryString = "NUMBER=" + ph;
                context.getContentResolver().delete(CallLog.Calls.CONTENT_URI,
                        queryString, null);
                Log.i("Debug", "Deleted the log");
                //TODO Locking code here. Test for conformance
                keyguardLock.reenableKeyguard();
                wakeLock.release();
            }
        } else if (action.equals("android.intent.action.BOOT_COMPLETED")) {
            boolean isEnabled = sharedPreferences.getBoolean("pref_enabled", true);
            if(isEnabled) {
                AlarmHandler.registerAlarm(context);
            }
        }
    }
}
//            Intent homeIn=new Intent(context, Home.class);
//            homeIn.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            homeIn.setAction("com.gts.airceladblocker.ACTION_CALLJERKS");
//            context.startActivity(homeIn);