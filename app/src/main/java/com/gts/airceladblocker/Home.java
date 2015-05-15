package com.gts.airceladblocker;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

public class Home extends AppCompatActivity {
    private static PowerManager.WakeLock wakeLock;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        checkRequisites();
        Button btnCall = (Button) findViewById(R.id.btnCall);
        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //calling some random jerk around.
                startActivityForResult(new Intent(Home.this, CallJerks.class), 2);
            }
        });
//        SharedPreferences sp = getApplicationContext()
//                .getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        boolean status = sp.getBoolean("isCallMade", false);
        if (!status) {
            TextView tv = (TextView) findViewById(R.id.tvStatus);
            tv.setText("Not Blocked Today");
            tv.setTextColor(Color.argb(255, 190, 65, 65));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sp = getApplicationContext()
                .getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE);
        boolean status = sp.getBoolean("isCallMade", false);
        if (!status) {
            TextView tv = (TextView) findViewById(R.id.tvStatus);
            tv.setText("Not Blocked Today");
            tv.setTextColor(Color.argb(255, 190, 65, 65));
        } else {
            TextView tv = (TextView) findViewById(R.id.tvStatus);
            tv.setText("Done for the day!");
            tv.setTextColor(Color.argb(255, 77, 190, 30));
        }
    }

    /**
     * This method will try to obtain phone number from the system. If not possible it will prompt
     * the user from a new intent and also sets the alarm for the first time.
     */
    private void checkRequisites() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String phoneNumber = sharedPreferences.getString("userphonenumber", null);
        Log.i("LOGGING", "phoneNumber = " + phoneNumber);
        if(phoneNumber == null) {
            // Start a new activity
            Log.i("Debug", "usernumber is null");
            Intent intent = new Intent(this, PhoneNumberFetch.class);
            startActivityForResult(intent, 1);
        }
    }

    /**
     * Checks for the activity results
     * @param requestCode   The code that is unique to the activity that is started.
     * @param resultCode    The result code set by the activities when finished.
     * @param data  The data returned from the called activity.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            finishActivity(1);
            if(resultCode == RESULT_OK) {
                // phone number stored.
                Log.i("Debug", "Phone number succesfully received");
            }
        } else if (requestCode == 2) {
            finishActivity(2);
            TextView tvStatus = (TextView) findViewById(R.id.tvStatus);
            SharedPreferences sp = getApplicationContext()
                    .getSharedPreferences("com.gts.airceladblocker.prefs",
                            MODE_PRIVATE);
            if (sp.getBoolean("isCallMade", false) == false) {
                SharedPreferences.Editor editor = sp.edit();
                if (resultCode == RESULT_OK) {
                    editor.putBoolean("isCallMade", true);
                    editor.commit();
                    tvStatus.setText("Done for the day!");
                    tvStatus.setTextColor(Color.argb(255, 77, 190, 30));
                } else if (resultCode == RESULT_CANCELED) {
                    editor.putBoolean("isCallMade", false);
                    editor.commit();
                    tvStatus.setText("Something went wrong!");
                    tvStatus.setTextColor(Color.rgb(190, 65, 65));
                }
            }
            //release lock
            if(wakeLock != null)
                wakeLock.release();
            wakeLock = null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, FragmenterActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_show_help) {
            showPopup();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void showPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        TextView title = new TextView(this);
        title.setText("About AircelAdBlocker");
        title.setGravity(Gravity.CENTER);
        title.setPadding(0, 15, 0, 15);
        builder.setCustomTitle(title);
        WebView wv = new WebView(this);
        wv.loadData(getString(R.string.webview_about), "text/html", null);
        wv.setBackgroundColor(Color.rgb(30, 30, 30));
        builder.setView(wv);
        builder.setNeutralButton(R.string.close, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
