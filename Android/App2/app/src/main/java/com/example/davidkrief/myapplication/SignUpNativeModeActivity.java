package com.example.davidkrief.myapplication;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.gigya.socialize.GSObject;
import com.gigya.socialize.GSResponse;
import com.gigya.socialize.GSResponseListener;
import com.gigya.socialize.android.GSAPI;

import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpNativeModeActivity extends AppCompatActivity {

    private static final String TAG = SignUpNativeModeActivity.class.getCanonicalName();

    private boolean loginIDIsUnique;
    private boolean loginIDIsConfirmed;
    private boolean passwordIsValid;
    private int minPasswordLength;
    private int minPasswordCharGroups;
    private String regToken;


    private Drawable inputDefault;

    private Button fbLoginButton;
    private Button googleLoginButton;
    private Button twitterLoginButton;

    private EditText nameInput;
    private EditText emailInput;
    private EditText confirmEmailInput;
    private EditText pwInput;
    private TextView alreadyMemberLink;
    private Button continueButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_native_mode);



        loginIDIsUnique = false;
        loginIDIsConfirmed = false;
        passwordIsValid = false;
        minPasswordLength = 6; // by default
        minPasswordCharGroups = 0; // by default
        regToken = ""; // by default

        fbLoginButton = (Button) findViewById(R.id.fbLoginButton);
        googleLoginButton = (Button) findViewById(R.id.googleLoginButton);
        twitterLoginButton = (Button) findViewById(R.id.twitterLoginButton);

        nameInput = (EditText) findViewById(R.id.nameInput);
        emailInput = (EditText) findViewById(R.id.emailInput);
        confirmEmailInput = (EditText) findViewById(R.id.confirmEmailInput);
        pwInput = (EditText) findViewById(R.id.pwInput);
        alreadyMemberLink = (TextView) findViewById(R.id.alreadyMemberLink);
        continueButton = (Button) findViewById(R.id.continueButton);

        inputDefault = confirmEmailInput.getBackground();

        initRegistration();
        getPasswordComplexityRequirements();

        fbLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    MainActivity.la.login("facebook");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        googleLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    MainActivity.la.login("googleplus");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        twitterLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    MainActivity.la.login("twitter");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });





        emailInput.addTextChangedListener(
                new TextWatcher() {
                    @Override public void onTextChanged(CharSequence s, int start, int before, int count) { }
                    @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

                    private Timer timer=new Timer();
                    private final long DELAY = 1000; // milliseconds

                    @Override
                    public void afterTextChanged(final Editable s) {
                        timer.cancel();
                        timer = new Timer();
                        timer.schedule(
                                new TimerTask() {

                                    @Override
                                    public void run() {
                                        runOnUiThread(new Runnable() {

                                            @Override
                                            public void run() {
                                                if(isValidEmail(s)){
                                                    Log.d(TAG, "valid");
                                                    if(isValidEmail(s)){
                                                        testLoginIDAvailability(s.toString());
                                                    }
                                                    else{
                                                        emailInput.setBackgroundResource(R.drawable.backtext);
                                                    }
                                                }
                                            }
                                        });

                                    }



                                },
                                DELAY
                        );
                    }




                });

        confirmEmailInput.addTextChangedListener(
                new TextWatcher() {
                    @Override public void onTextChanged(CharSequence s, int start, int before, int count) { }
                    @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

                    private Timer timer=new Timer();
                    private final long DELAY = 1000; // milliseconds

                    @Override
                    public void afterTextChanged(final Editable s) {
                        timer.cancel();
                        timer = new Timer();
                        timer.schedule(
                                new TimerTask() {

                                    @Override
                                    public void run() {
                                        runOnUiThread(new Runnable() {

                                            @Override
                                            public void run() {
                                                loginIDIsConfirmed = false;


                                                if(s.toString().equals(emailInput.getText().toString()) && s.toString() != ""){
                                                    loginIDIsConfirmed = true;
                                                    confirmEmailInput.setBackground(inputDefault);

                                                }else if(s.toString() != "") {


                                                    confirmEmailInput.setBackgroundResource(R.drawable.backtext);

                                                    Toast.makeText(getApplicationContext(), "Emails don't match",
                                                            Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        });

                                    }



                                },
                                DELAY
                        );
                    }
                }
        );


        pwInput.addTextChangedListener(

                new TextWatcher() {
                    @Override public void onTextChanged(CharSequence s, int start, int before, int count) { }
                    @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

                    private Timer timer=new Timer();
                    private final long DELAY = 1000; // milliseconds


                    @Override
                    public void afterTextChanged(final Editable s) {
                        /*if(complexity == 0 ) // Null
                        {
                            passwordIsValid = false;
                        }
                        else if(complexity == -1) // Too weak
                        {
                            passwordIsValid = false;
                        }
                        else if()
                        {

                        }
*/



                        timer.cancel();
                        timer = new Timer();
                        timer.schedule(
                                new TimerTask() {

                                    @Override
                                    public void run() {
                                        runOnUiThread(new Runnable() {

                                            @Override
                                            public void run() {
                                                int complexity = computePasswordComplexity(s.toString());

                                                switch (complexity) {
                                                    case -1:
                                                        passwordIsValid = false;
                                                        pwInput.setBackgroundResource(R.drawable.backtext);
                                                        Toast.makeText(getApplicationContext(), "Too weak", Toast.LENGTH_LONG).show();
                                                        break;
                                                    case 0:
                                                        passwordIsValid = false;
                                                        pwInput.setBackground(inputDefault);
                                                        break;
                                                    case 1:
                                                        passwordIsValid = true;
                                                        pwInput.setBackground(inputDefault);
                                                        Toast.makeText(getApplicationContext(), "Fair", Toast.LENGTH_LONG).show();
                                                        break;
                                                    case 2:
                                                        passwordIsValid = true;
                                                        pwInput.setBackground(inputDefault);
                                                        Toast.makeText(getApplicationContext(), "Strong", Toast.LENGTH_LONG).show();
                                                        break;
                                                }
                                            }
                                        });
                                    }
                                }, DELAY);
                    }
                });

        alreadyMemberLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpNativeModeActivity.this, EmailSigninAndLinkingActivity.class);
                startActivity(intent);
                finish();
            }
        });

        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameInput.getText().toString();
                if(loginIDIsUnique && loginIDIsConfirmed && passwordIsValid && !regToken.equals("") && !name.equals("") ){
                    register( name, emailInput.getText().toString() , pwInput.getText().toString()  );
                }else{
                    Toast.makeText(getApplicationContext(), "Error in registration form",
                            Toast.LENGTH_LONG).show();
                }


            }
        });



        //
    }

    public void register(String name, String email, String password){

        Log.d(TAG, "Trying to register with: "+ name + " - "+ email + " - " + password);

        // Step 1 - Defining request parameters
        GSObject params = new GSObject();
        params.put("regToken", regToken);
        params.put("email", email);
        params.put("password", password);
        params.put("finalizeRegistration", true);

        GSObject profile = new GSObject();
        profile.put("firstName", name);
        params.put("profile", profile);



        // Step 2 - Defining response listener. The response listener will handle the request's response.
        GSResponseListener resListener = new GSResponseListener() {
            @Override
            public void onGSResponse(String method, GSResponse response, Object context) {
                try {
                    Log.d(TAG, response.toString());
                    if (response.getErrorCode()==0) {

                        // Successfully registered
                        finish();
                        Toast.makeText(getApplicationContext(), "Successfully registered",
                                Toast.LENGTH_LONG).show();
                    } else {  // Error
                        System.out.println("Got error on setStatus: " + response.getLog());
                        Toast.makeText(getApplicationContext(), (String) response.getErrorMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                }
                catch (Exception ex) {  ex.printStackTrace();  }
            }
        };

        // Step 3 - Sending the request
        String methodName = "accounts.register";
        GSAPI.getInstance().sendRequest(methodName, params, resListener, null);

    }

    public void initRegistration(){
        GSResponseListener resListener = new GSResponseListener() {
            @Override
            public void onGSResponse(String method, GSResponse response, Object context) {
                try {
                    Log.d(TAG, response.toString());
                    if (response.getErrorCode()==0) {

                        regToken = response.getData().getString("regToken");

                    } else {  // Error
                        System.out.println("Got error on setStatus: " + response.getLog());
                        Toast.makeText(getApplicationContext(), (String) response.getErrorMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                }
                catch (Exception ex) {  ex.printStackTrace();  }
            }
        };

        // Step 3 - Sending the request
        String methodName = "accounts.initRegistration";
        GSAPI.getInstance().sendRequest(methodName, null, resListener, null);
    }

    public int computePasswordComplexity(String q){
        if(q.length() > 0)
        {
            // your code goes here
            Pattern p = Pattern.compile("([^a-zA-Z0-9 ])|([a-z])|([A-Z])|([0-9])");
            Matcher matcher = p.matcher(q);
            int s = 0, l = 0, u = 0, n = 0, groups = 0;
            while (matcher.find()) {
                if(matcher.group(1)!=null)
                {
                    s++;
                    if(s==1)
                        groups++;
                }
                else if(matcher.group(2)!=null)
                {
                    l++;
                    if(l==1)
                        groups++;
                }
                else if(matcher.group(3)!=null)
                {
                    u++;
                    if(u==1)
                        groups++;
                }
                else if(matcher.group(4)!=null)
                {
                    n++;
                    if(n==1)
                        groups++;
                }
            }
            System.out.println(groups + " groups - " + s + " special - " + l + " lower - " + u + " upper - " + n + " number");
            if(s+l+u+n>=minPasswordLength)
            {
                if(groups>=minPasswordCharGroups)
                {
                    if(groups==1)
                    {
                        return 1;
                    }
                    else
                    {
                        return 2;
                    }
                }
                else
                {
                    return -1;
                }
            }
            else
            {
                return -1;
            }
        }
        return 0;
    }
    public void getPasswordComplexityRequirements(){

        // Step 1 - Defining request parameters
        GSObject params = new GSObject();
        params.put("sections", "passwordComplexity");

        // Step 2 - Defining response listener. The response listener will handle the request's response.
        GSResponseListener resListener = new GSResponseListener() {
            @Override
            public void onGSResponse(String method, GSResponse response, Object context) {
                try {
                    Log.d(TAG, response.toString());
                    if (response.getErrorCode()==0) {
                        GSObject pwComplexity = response.getData().getObject("passwordComplexity");
                        minPasswordLength = pwComplexity.getInt("minLength");
                        minPasswordCharGroups = pwComplexity.getInt("minCharGroups");

                    } else {  // Error
                        System.out.println("Got error on setStatus: " + response.getLog());
                        Toast.makeText(getApplicationContext(), (String) response.getErrorMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                }
                catch (Exception ex) {  ex.printStackTrace();  }
            }
        };

        // Step 3 - Sending the request
        String methodName = "accounts.getPolicies";
        GSAPI.getInstance().sendRequest(methodName, params, resListener, null);
    }
    public final static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public void testLoginIDAvailability(String s){

        loginIDIsUnique = false;
        // Step 1 - Defining request parameters
        GSObject params = new GSObject();
        params.put("loginID", s);

        // Step 2 - Defining response listener. The response listener will handle the request's response.
        GSResponseListener resListener = new GSResponseListener() {
            @Override
            public void onGSResponse(String method, GSResponse response, Object context) {
                try {
                    Log.d(TAG, response.toString());
                    if (response.getErrorCode()==0) { // SUCCESS! response status = OK
                        System.out.println("Success in setStatus operation.");

                        if(response.getData().getBool("isAvailable")){
                            loginIDIsUnique = true;
                            emailInput.setBackground(inputDefault);
                        }
                        else
                        {

                            emailInput.setBackgroundResource(R.drawable.backtext);
                            Toast.makeText(getApplicationContext(), "Email already exists",
                                    Toast.LENGTH_LONG).show();
                        }

                    } else {  // Error
                        System.out.println("Got error on isAvailableLoginID: " + response.getLog());
                        Toast.makeText(getApplicationContext(), (String) response.getErrorMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                }
                catch (Exception ex) {  ex.printStackTrace();  }
            }
        };

        // Step 3 - Sending the request
        String methodName = "accounts.isAvailableLoginID";
        GSAPI.getInstance().sendRequest(methodName, params, resListener, null);
    }


}
