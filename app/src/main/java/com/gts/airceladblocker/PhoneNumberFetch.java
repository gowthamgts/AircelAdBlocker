package com.gts.airceladblocker;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class PhoneNumberFetch extends Activity {
    Button btnDone;
    EditText etPhone;
    Context context;
    boolean simSelected;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_phone_number_fetch);

        simSelected = false;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        btnDone = (Button) findViewById(R.id.btnDone);
        etPhone = ((EditText)findViewById(R.id.etPhoneNumber));
        context = getApplicationContext();

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.pref_sim_slot_entries_added, R.layout.spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int selection = (int)parent.getItemIdAtPosition(position);
                switch (selection) {
                    case 0: simSelected = false;
                        break;
                    case 1:
                        sharedPreferences.edit().putString("simslot", "0").commit();
                        simSelected = true;
                        break;
                    case 2:
                        sharedPreferences.edit().putString("simslot", "1").commit();
                        simSelected = true;
                        break;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int len = etPhone.getText().length();
                if( len == 10 && simSelected) {
                    String phoneNumber = etPhone.getText().toString();
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("userphonenumber", phoneNumber);
                    editor.commit();
                    AlarmHandler.registerAlarm(context);
                    setResult(RESULT_OK);
                    finish();
                } else if ( len!=10 ) {
                    Toast.makeText(PhoneNumberFetch.this, "Please enter a valid phone number",
                            Toast.LENGTH_SHORT).show();
                } else if (!simSelected) {
                    Toast.makeText(PhoneNumberFetch.this,"Please select the SIM slot correctly",
                                Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
