package com.example.davidkrief.myapplicationB;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.gigya.socialize.GSArray;

public class LinkingActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static final String TAG = LinkingActivity.class.getCanonicalName();

    private Spinner spinner;
    private Button okBtn;
    private String[] conflictingLoginProvidersArray;
    private String selectedProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_linking);

        selectedProvider = null;

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            GSArray conflictingLoginProviders = (GSArray) extras.get("conflictingLoginProviders");
            Log.d(TAG,conflictingLoginProviders.toString());
            Log.d(TAG,conflictingLoginProviders.toJsonString());



            conflictingLoginProvidersArray = new String[conflictingLoginProviders.length()];
            for (int i = 0; i < conflictingLoginProviders.length(); i++)
                conflictingLoginProvidersArray[i] = conflictingLoginProviders.getString(i);




            okBtn = (Button)findViewById(R.id.okBtn);
            okBtn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    MainActivity.la.processLinking(selectedProvider);
                }
            });


            spinner = (Spinner)findViewById(R.id.spinner);
            ArrayAdapter<String>adapter = new ArrayAdapter<String>(LinkingActivity.this,
                    android.R.layout.simple_spinner_item,conflictingLoginProvidersArray);

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
            spinner.setOnItemSelectedListener(this);

        }




    }

    public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
        Log.d(TAG,conflictingLoginProvidersArray[position]);
        selectedProvider = conflictingLoginProvidersArray[position];

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
