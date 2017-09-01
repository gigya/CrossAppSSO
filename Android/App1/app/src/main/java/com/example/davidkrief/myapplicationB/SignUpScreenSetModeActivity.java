package com.example.davidkrief.myapplicationB;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.gigya.socialize.GSObject;
import com.gigya.socialize.android.GSPluginFragment;

public class SignUpScreenSetModeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_screen_set_mode);


        GSObject params = new GSObject();
        params.put("screenSet", "Default4-RegistrationLogin");
        //params.put("startScreen", "gigya-register-screen");
        GSPluginFragment pluginFragment = GSPluginFragment.newInstance("accounts.screenSet", params);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container, pluginFragment);
        fragmentTransaction.commit();


    }
}
