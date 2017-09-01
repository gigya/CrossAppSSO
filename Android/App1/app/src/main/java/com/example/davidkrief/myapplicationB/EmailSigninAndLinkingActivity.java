package com.example.davidkrief.myapplicationB;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.gigya.socialize.GSObject;
import com.gigya.socialize.GSResponse;
import com.gigya.socialize.GSResponseListener;
import com.gigya.socialize.android.GSAPI;


public class EmailSigninAndLinkingActivity extends AppCompatActivity {

    private static final String TAG = EmailSigninAndLinkingActivity.class.getCanonicalName();

    private EditText emailInput;
    private EditText pwInput;

    private TextView forgotPwDirect;
    private TextView forgotPwEmail;

    private TextView accountLinkingText;
    private TextView createAccountTitle;
    private TextView title;

    private Button okButton;
    private Button createAccountButtonScreenSetMode;
    private Button createAccountButtonWebbridgeMode;
    private Button createAccountButtonNativeMode;

    private boolean forLinkingAccounts;
    private String globalRegToken;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_signin_and_linking);


        emailInput = (EditText) findViewById(R.id.emailInput);
        pwInput = (EditText) findViewById(R.id.passwordInput);
        forgotPwDirect = (TextView) findViewById(R.id.forgotPasswordDirect);
        forgotPwEmail = (TextView) findViewById(R.id.forgotPasswordEmail);
        accountLinkingText = (TextView) findViewById(R.id.accountLinkingText);
        okButton = (Button) findViewById(R.id.okButton);
        createAccountTitle = (TextView) findViewById(R.id.createAccountTitle);

        title = (TextView) findViewById(R.id.title);
        title.setText("Log in (native implementation)");

        createAccountButtonScreenSetMode = (Button) findViewById(R.id.createAccountButtonScreenSetMode);
        createAccountButtonWebbridgeMode = (Button) findViewById(R.id.createAccountWebbridgeMode);
        createAccountButtonNativeMode = (Button) findViewById(R.id.createAccountNativeMode);



        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            forLinkingAccounts = extras.getBoolean("forLinkingAccounts");
            if(forLinkingAccounts)
            {
                globalRegToken = extras.getString("globalRegToken");


                Log.d(TAG, "to link accounts with token : "+globalRegToken);

                // Adapting the email sign-in view for linking accouts

                title.setText("Account linking");

                createAccountButtonWebbridgeMode.setVisibility(View.GONE);
                createAccountButtonScreenSetMode.setVisibility(View.GONE);
                createAccountButtonNativeMode.setVisibility(View.GONE);
                emailInput.setText(extras.getString("conflictingLoginID"));
                emailInput.setFocusable(false);
                createAccountTitle.setVisibility(View.GONE);
                accountLinkingText.setVisibility(View.VISIBLE);

            }


        }




        okButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                emailLogin();
            }
        });

        createAccountButtonWebbridgeMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRegistrationScreen("webbridge");
            }
        });
        createAccountButtonNativeMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRegistrationScreen("native");
            }
        });
        createAccountButtonScreenSetMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRegistrationScreen("screenset");
            }
        });

        forgotPwEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassword(true);
            }
        });

        forgotPwDirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassword(false);
            }
        });

    }


    public void showRegistrationScreen(String mode){
        Intent intent = null;
        switch(mode) {
            case "webbridge":
                intent = new Intent(this, SignUpWebbridgeActivity.class);
                startActivity(intent);
                break;
            case "native":
                intent = new Intent(this, SignUpNativeModeActivity.class);
                startActivity(intent);
                break;
            case "screenset":
                intent = new Intent(EmailSigninAndLinkingActivity.this, SignUpScreenSetModeActivity.class);
                startActivity(intent);
                break;
        }

    }

    public void resetPassword(boolean byEmail){

        String loginID = (String) emailInput.getText().toString();
        if(loginID.length() == 0)
        {
            Toast.makeText(getApplicationContext(), "Please enter your email first",
                    Toast.LENGTH_LONG).show();
        }
        else {



            // Step 1 - Defining request parameters
            GSObject params = new GSObject();

            params.put("loginID", loginID);


            GSResponseListener resListener;
            if(byEmail)
            {
                params.put("sendEmail", true);

                resListener = new GSResponseListener() {
                    @Override
                    public void onGSResponse(String method, GSResponse response, Object context) {
                        try {
                            Log.d(TAG, response.toString());
                            if (response.getErrorCode()==0) { // SUCCESS! response status = OK
                                System.out.println("Success in setStatus operation.");
                                Toast.makeText(getApplicationContext(), "An email regarding your password change has been sent to your email address.",
                                        Toast.LENGTH_LONG).show();

                            } else {  // Error
                                System.out.println("Got error on setStatus: " + response.getLog());
                                Toast.makeText(getApplicationContext(), (String) response.getErrorMessage(),
                                        Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception ex) {  ex.printStackTrace();  }
                    }
                };
            }
            else
            {
                params.put("sendEmail", false);

                resListener = new GSResponseListener() {
                    @Override
                    public void onGSResponse(String method, GSResponse response, Object context) {
                        try {
                            Log.d(TAG, response.toString());
                            if (response.getErrorCode()==0) { // SUCCESS! response status = OK
                                System.out.println("Success in setStatus operation.");
                                Toast.makeText(getApplicationContext(), "An email regarding your password change has been sent to your email address.",
                                        Toast.LENGTH_LONG).show();

                            } else {  // Error
                                System.out.println("Got error on setStatus: " + response.getLog());
                                Toast.makeText(getApplicationContext(), (String) response.getErrorMessage(),
                                        Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception ex) {  ex.printStackTrace();  }
                    }
                };
            }


            String methodName = "accounts.resetPassword";
            GSAPI.getInstance().sendRequest(methodName, params, resListener, null);
        }

    }



    public void emailLogin(){


        // Step 1 - Defining request parameters
        GSObject params = new GSObject();
        params.put("loginID", (String) emailInput.getText().toString());
        params.put("password",(String) pwInput.getText().toString());

        if(forLinkingAccounts)
        {
            params.put("loginMode", "link");
            params.put("regToken", globalRegToken);
        }
        else
        {
            params.put("loginMode", "standard");
        }


        // Step 2 - Defining response listener. The response listener will handle the request's response.
        GSResponseListener resListener = new GSResponseListener() {
            @Override
            public void onGSResponse(String method, GSResponse response, Object context) {
                try {
                    Log.d(TAG, response.toString());
                    if (response.getErrorCode()==0) { // SUCCESS! response status = OK
                        System.out.println("Successful login");
                        finish();

                    }
                    else if (response.getErrorCode()==200009) { // SUCCESS! response status = OK
                        System.out.println("Successful linked");
                        Toast.makeText(getApplicationContext(), "Account successfully linked !", Toast.LENGTH_LONG).show();

                        // Now finalize the registration

                        GSObject params = new GSObject();
                        params.put("regToken", response.getData().getString("regToken"));

                        params.put("allowAccountsLinking", true);

                        GSResponseListener resListener = new GSResponseListener() {
                            @Override
                            public void onGSResponse(String method, GSResponse response, Object context) {
                                try {
                                    Log.d(TAG, response.toString());
                                    if (response.getErrorCode()==0) { // SUCCESS! response status = OK
                                        System.out.println("Successful linking and login");
                                        finish();

                                    }
                                    else {  // Error
                                        System.out.println("Got error : " + response.getLog());
                                        Toast.makeText(getApplicationContext(), response.getErrorMessage(), Toast.LENGTH_LONG).show();
                                    }
                                } catch (Exception ex) {  ex.printStackTrace();  }
                            }
                        };

                        // Step 3 - Sending the request
                        String methodName = "accounts.finalizeRegistration";
                        GSAPI.getInstance().sendRequest(methodName, params, resListener, null);


                    }
                    else {  // Error
                        System.out.println("Got error : " + response.getLog());
                        Toast.makeText(getApplicationContext(), (String) response.getErrorMessage(), Toast.LENGTH_LONG).show();
                    }
                } catch (Exception ex) {  ex.printStackTrace();  }
            }
        };

        // Step 3 - Sending the request
        String methodName = "accounts.login";
        GSAPI.getInstance().sendRequest(methodName, params, resListener, null);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_my, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
