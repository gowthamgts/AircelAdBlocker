package com.gts.airceladblocker;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Calendar;

/**
 * This class handles all the alarm related capabilities including
 * registering and unregistering the alarms in the application.
 * @author Gowtham Gopalakrishnan
 */
public class AlarmHandler {
    /**
     * This method is used to register the alarm for the application.
     * @param context The context of the alarm to be registered.
     */
    public static void registerAlarm(Context context) {
        if (isEmpty(context)) {
            Intent intent = new Intent(context, BCastReceiver.class);
            intent.setAction("com.gts.airceladblocker.ACTION_BCAST");
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(System.currentTimeMillis());
            cal.set(Calendar.HOUR_OF_DAY, 00);
            cal.set(Calendar.MINUTE, 10);
            cal.add(Calendar.DATE, 1);
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY, pendingIntent);
            Log.i("Debug", "Registered call broadcast...");
        } else {
            Log.w("Debug", "Broadcast is already present...");
        }
    }

    /**
     * This method unregisters the alarm by the application
     * @param context The context for performing actions.
     */
    public static void removeAlarm(Context context) {
        if (!isEmpty(context)) {
            Intent intent = new Intent(context, BCastReceiver.class);
            intent.setAction("com.gts.airceladblocker.ACTION_BCAST");
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel();
            Log.i("Debug", "UnRegistered call broadcast...");
        } else {
            Log.w("Debug", "No Broadcast is present...");
        }
    }

    /**
     * This method is responsible for checking whether the alarm is registered or not.
     * @param context The context for the search.
     * @return  true if empty else false
     */
    private static boolean isEmpty(Context context) {
        Intent intent = new Intent(context, BCastReceiver.class);
        intent.setAction("com.gts.airceladblocker.ACTION_BCAST");
        boolean empty = (PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_NO_CREATE)
                == null);
        Log.i("Debug", String.valueOf(empty));
        return empty;
    }
}
