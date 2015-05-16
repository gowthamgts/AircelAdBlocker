package com.gts.airceladblocker;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * Created by Gowtham Gopalakrishnan on 14/5/15.
 */
public class FragmenterActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager()
                .beginTransaction()
                .replace(android.R.id.content, new Fragmenter())
                .commit();
    }

    public static class Fragmenter extends PreferenceFragment
            implements SharedPreferences.OnSharedPreferenceChangeListener {
        private Context context;
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings);
        }

        /**
         * This method is overriden here to obtain the context.
         * @param activity
         */
        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            context = getActivity();
        }

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

        }

        @Override
        public void onPause() {
            getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
            super.onPause();
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            Log.i("Logging", key);
            if (key.equals("pref_enabled")) {
                boolean state = sharedPreferences.getBoolean(key, true);
                if (state) {
                    // register the alarm
                    AlarmHandler.registerAlarm(context);
                } else {
                    // remove the alarm
                    AlarmHandler.removeAlarm(context);
                }
            } else if (key.equals("simslot")) {
                Log.i("Debug", key);
                Log.i("Debug", String.valueOf(sharedPreferences.getString(key, null)));
            }
        }
    }
}