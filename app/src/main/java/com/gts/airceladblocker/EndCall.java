package com.gts.airceladblocker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by GTS on 14-01-2015.
 */
public class EndCall extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("Debug", "Received a broadcast...");
        String phonenumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
        Log.i("Debug", "Phone number: " + phonenumber);
    }
}
