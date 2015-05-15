package com.gts.airceladblocker;

import android.app.AlarmManager;
import android.app.KeyguardManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.provider.CallLog;
import android.util.Log;

import java.util.Calendar;

/**
 * Created by GTS on 04-02-2015.
 */
public class BCastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals("com.gts.airceladblocker.ACTION_BCAST")) {
            Log.i("Debug", "Received a calling bcast...");
//            Toast.makeText(context, "Received a calling bcast...", Toast.LENGTH_SHORT).show();
            Log.i("Debug", "Clearing Status...");
//            Toast.makeText(context, "Received status clear bcast...", Toast.LENGTH_SHORT).show();
            // Read the prefs and clear the status.
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
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
                i.putExtra("com.android.phone.extra.slot", sharedPreferences.getInt("simslot", -1));
                i.putExtra("simSlot", sharedPreferences.getInt("simslot", -1));
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
            // This intent is to call
            Intent alrmIntent = new Intent(context, BCastReceiver.class);
            alrmIntent.setAction("com.gts.airceladblocker.ACTION_BCAST");
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alrmIntent, 0);
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(System.currentTimeMillis());
            cal.set(Calendar.HOUR_OF_DAY, 00);
            cal.set(Calendar.MINUTE, 10);
            AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY, pendingIntent);
            Log.i("Debug", "Registered call broadcast...");
        }
    }
}
//            Intent homeIn=new Intent(context, Home.class);
//            homeIn.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            homeIn.setAction("com.gts.airceladblocker.ACTION_CALLJERKS");
//            context.startActivity(homeIn);