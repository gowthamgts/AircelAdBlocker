package com.gts.airceladblocker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.provider.CallLog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class CallJerks extends AppCompatActivity {
    private CountDownTimer timer;
    private int simSelection;
    private String phoneNumber;
    @Override
    protected void onStop() {
        super.onStop();
        navigateHome(false);
    }

    protected void onPause() {
        super.onPause();
        navigateHome(false);
    }
    /**
     * This function stops the timer to stop calling when cancel button or the activity goes out
     * scope.
     */
    private void stopTimer() {
        timer.cancel();
    }

    private void navigateHome(boolean result) {
        stopTimer();
        setResult((result ? RESULT_OK: RESULT_CANCELED));
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        readDetails();
        setContentView(R.layout.activity_call_jerks);
        final TextView tv = (TextView) findViewById(R.id.tvSecs);
        Button btnCancel = (Button) findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //cancel the call and go back...
                navigateHome(false);
            }
        });
        timer = new CountDownTimer(6000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                millisUntilFinished = Math.round(millisUntilFinished / 1000);
                tv.setText(String.valueOf(millisUntilFinished));
            }
            @Override
            public void onFinish() {
                if(phoneNumber != null && simSelection != -1) {
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse("tel:" + phoneNumber));
                    intent.putExtra("com.android.phone.extra.slot", simSelection);
                    intent.putExtra("simSlot", simSelection);
                    startActivity(intent);
                    //deleting the log
                    String queryString = "NUMBER=" + phoneNumber;
                    getApplicationContext().getContentResolver().delete(CallLog.Calls.CONTENT_URI,
                            queryString, null);
                    Log.i("Debug", "reached at the end");
                    navigateHome(true);
                } else {
                    //get phone number
                    Intent intent = new Intent(CallJerks.this, PhoneNumberFetch.class);
                    Log.i("Debug", "Starting phone number fetch from Calljerks");
                    startActivityForResult(intent, 1);
                }
            }
        }.start();
    }

    private void readDetails() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        phoneNumber  = sharedPreferences.getString("userphonenumber", null);
        simSelection = sharedPreferences.getInt("simslot", -1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            finishActivity(1);
            if (resultCode == RESULT_OK) {
                // phone number stored.
                Log.i("Debug", "Phone number succesfully received");
            }
        }
    }
}
