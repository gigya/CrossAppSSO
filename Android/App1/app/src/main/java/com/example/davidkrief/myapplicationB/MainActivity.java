package com.example.davidkrief.myapplicationB;

import android.Manifest;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.security.KeyPairGeneratorSpec;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import android.text.Editable;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.gigya.socialize.GSArray;
import com.gigya.socialize.GSKeyNotFoundException;
import com.gigya.socialize.GSObject;
import com.gigya.socialize.GSResponse;
import com.gigya.socialize.GSResponseListener;
import com.gigya.socialize.android.GSAPI;
import com.gigya.socialize.android.GSSession;
import com.gigya.socialize.android.event.GSLoginUIListener;
import com.gigya.socialize.android.event.GSSocializeEventListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.math.BigInteger;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import java.security.SecureRandom;
import java.security.interfaces.RSAPublicKey;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.security.auth.x500.X500Principal;



public class MainActivity extends AppCompatActivity  {


    private static final String TAG = MainActivity.class.getCanonicalName();
    public final static String EXTRA_MESSAGE = "com.example.davidkrief.myapplication.MESSAGE";
    public static MainActivity la;

    private String globalRegToken;

    private Button fbButton;
    private Button googleButton;
    private Button twitterButton;
    private Button otherButton;
    private Button emailButton;
    private String conflictingLoginID;

    private boolean needToFinalize;

    KeyStore ks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        la = this;

        needToFinalize = false;
        globalRegToken = null;
        conflictingLoginID = null;
        // Gigya
        fbButton = (Button) findViewById(R.id.facebookLoginButton);
        googleButton = (Button) findViewById(R.id.googleLoginButton);
        twitterButton = (Button) findViewById(R.id.twitterLoginButton);
        otherButton = (Button) findViewById(R.id.otherLoginButton);
        emailButton = (Button) findViewById(R.id.emailLoginButton);

        //  friendsSpinner = (Spinner) findViewById(R.id.friendsSpinner);

        initGigya();
        isThereASession();

        fbButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    Log.d(TAG, "Fb");

                    login("facebook");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        twitterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Log.d(TAG, "Twitter");

                    login("twitter");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        googleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    login("googleplus");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        otherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSocialLoginList();
            }
        });

        emailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    login("email");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });




    }

    void isThereASession() {
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("GIGYA-PREFS", MODE_PRIVATE);

        String token = preferences.getString("gigya-token", "");
        String secret = preferences.getString("gigya-secret", "");

        Long expirationTime = preferences.getLong("gigya-expirationTime", 0);

        GSSession oldSession = new GSSession(token, secret, expirationTime);
        if(oldSession.isValid()){

            GSAPI.getInstance().setSession(oldSession);

            //Call to getAccountInfo
            GSAPI.getInstance().sendRequest("accounts.getAccountInfo", null, new GSResponseListener() {
                @Override
                public void onGSResponse(String method, GSResponse response, Object context) {
                    if (response.getErrorCode() == 0) {
                        Log.d(TAG, "Already logged");
                        try {
                            displayMainView(response.getData().getString("firstName"), response.getData().getString("nickname"));
                        } catch (GSKeyNotFoundException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Log.d(TAG, "Not logged");
                    }
                }
            }, null);
        }


    }

    void storeSession(GSSession session){

        SharedPreferences preferences = getApplicationContext().getSharedPreferences("GIGYA-PREFS", MODE_PRIVATE);
        SharedPreferences.Editor PrefsEditor = preferences.edit();

        PrefsEditor.putString("gigya-token",session.getToken());
        PrefsEditor.putString("gigya-secret",session.getSecret());
        PrefsEditor.putLong("gigya-expirationTime",session.getExpirationTime());
        PrefsEditor.apply();


    }


    public void initGigya() {

        GSAPI.getInstance().initialize(this, getString(R.string.gigya_api_key), getString(R.string.api_domain) + ".gigya.com");


        GSAPI.getInstance().setSocializeEventListener(new GSSocializeEventListener() {
            @Override
            public void onLogout(Object context) {
                Log.d(TAG, "Gigya logged out");

                //CLEAN THE SHAREDPREFERENCES
                SharedPreferences preferences = getApplicationContext().getSharedPreferences("GIGYA-PREFS", MODE_PRIVATE);
                SharedPreferences.Editor PrefsEditor = preferences.edit();

                PrefsEditor.putString("gigya-token",null);
                PrefsEditor.putString("gigya-secret",null);
                PrefsEditor.putLong("gigya-expirationTime",0);
                PrefsEditor.apply();

            }

            @Override
            public void onLogin(String provider, GSObject user, Object context) {
                Log.d(TAG, "Gigya logged in with " + provider);
                try {

                    //addKeyToKeyStore(GSAPI.getInstance().getSession());



                    storeSession(GSAPI.getInstance().getSession());


                    Log.d(TAG, user.toString());
                    displayMainView(user.getString("firstName"), user.containsKey("nickname") ? user.getString("nickname") : null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onConnectionRemoved(String provider, Object context) {
                Log.d(TAG, provider + " connection was removed");
            }

            @Override
            public void onConnectionAdded(String provider, GSObject user, Object context) {
                Log.d(TAG, provider + " connection was added");
            }
        });

        GSAPI.getInstance().sendRequest("socialize.getUserInfo", null, new GSResponseListener() {
            @Override
            public void onGSResponse(String method, GSResponse response, Object context) {
                if (response.getErrorCode() == 0) {
                    Log.d(TAG, "Already logged");
                    try {
                        displayMainView(response.getData().getString("firstName"), response.getData().getString("nickname"));
                    } catch (GSKeyNotFoundException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.d(TAG, "Not logged");
                }
            }
        }, null);




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


    // Called on Login
    public void displayMainView(String firstname, String nickname) {
        Intent intent = new Intent(MainActivity.this, LoggedActivity.class);

        // Make profile name
        String profileName = firstname;
        if (nickname != null && "" != nickname) {
            profileName = nickname;
        }

        // An intent contains key values
        intent.putExtra(EXTRA_MESSAGE, profileName);
        startActivityForResult(intent, 0);
    }

    public void displayEmailSigninView(boolean forLinkingAccounts, String conflictingLoginID) {

        System.out.println("displayEmailSigninView");
        Intent intent = new Intent(this, EmailSigninAndLinkingActivity.class);
        intent.putExtra("forLinkingAccounts", forLinkingAccounts);
        intent.putExtra("globalRegToken", globalRegToken);
        intent.putExtra("conflictingLoginID", conflictingLoginID);
        startActivityForResult(intent, 0);

    }

    public void presentLinkingView(GSArray conflictingLoginProviders) {

        System.out.println("presentLinkingView");
        Intent intent = new Intent(this, LinkingActivity.class);
        intent.putExtra("conflictingLoginProviders", conflictingLoginProviders);
        startActivityForResult(intent, 0);

    }



    public void linkHandler(GSResponse response) {
        Log.d(TAG, "==> linkHandler");
        Log.d(TAG, response.toString());

        //Check response for success
        if (response.getErrorCode() == 0) {

            // ....
            //suppose the loginProviders=['facebook', 'twitter', 'site']

            //  "conflictingAccount":{"loginID":"davidkrief.ecl@gmail.com","loginProviders":["site"]}                                                                                                                        	data:{"callId":"c3ae8b2333644e35aa631ae93d8d2e1c","conflictingAccount":{"loginID":"davidkrief.ecl@gmail.com","loginProviders":["site"]},"errorCode":0,"statusCode":200,"statusReason":"OK","time":"2016-07-25T14:29:16.492Z"}
            try {
                GSObject conflictingAccountObj = response.getData().getObject("conflictingAccount");
                GSArray conflictingLoginProviders = conflictingAccountObj.getArray("loginProviders");

                if(conflictingAccountObj.containsKey("loginID"))
                {
                    conflictingLoginID = conflictingAccountObj.getString("loginID");
                }



                System.out.println(conflictingLoginProviders.toString());
                System.out.println(conflictingLoginProviders.length());
                System.out.println(conflictingLoginProviders.getString(0));


                if (conflictingLoginProviders.length() > 1) // multiple other conflicting login providers
                {
                    //Present a linking choice UI for the user to select a provider to authenticate with
                    presentLinkingView(conflictingLoginProviders);

                } else {
                    //Check the user's selection (social or site identity) and call the corresponding method
                    processLinking(conflictingLoginProviders.getString(0));
                }

            } catch (GSKeyNotFoundException e) {
                e.printStackTrace();
            }


        } else {  // Error
            System.out.println("Got error on linkAccounts: " + response.getLog());
            Toast.makeText(getApplicationContext(), response.getErrorMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void processLinking(String formerProviderToLink) {
        if (formerProviderToLink.equals("site")) { // social to site linking
            displayEmailSigninView(true, conflictingLoginID);

        } else {  // social to social linking
            //link the social accounts. Make sure to call the login method with loginMode='link'
                      /*  accounts.socialLogin({
                                provider: provider,
                                regToken : globalRegToken,
                                loginMode: 'link'
                        });*/

            GSObject params = new GSObject();

            params.put("loginMode", "link");
            params.put("regToken", globalRegToken);

            switch (formerProviderToLink) {
                case "facebook":
                    params.put("provider", "facebook");

                    try {
                        GSAPI.getInstance().login(this, params, new GSResponseListener() {
                            @Override
                            public void onGSResponse(String method, GSResponse response, Object context) {
                                try {
                                    loginHandler(response);
                                } catch (GSKeyNotFoundException e) {
                                    e.printStackTrace();
                                }

                            }
                        }, false, null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case "google":
                    params.put("provider", "googleplus");
                    try {
                        GSAPI.getInstance().login(this, params, new GSResponseListener() {
                            @Override
                            public void onGSResponse(String method, GSResponse response, Object context) {
                                try {
                                    loginHandler(response);
                                } catch (GSKeyNotFoundException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, false, null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case "googleplus":
                    params.put("provider", "googleplus");
                    try {
                        GSAPI.getInstance().login(this, params, new GSResponseListener() {
                            @Override
                            public void onGSResponse(String method, GSResponse response, Object context) {
                                try {
                                    loginHandler(response);
                                } catch (GSKeyNotFoundException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, false, null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case "twitter":
                    params.put("provider", "twitter");
                    try {
                        GSAPI.getInstance().login(this, params, new GSResponseListener() {
                            @Override
                            public void onGSResponse(String method, GSResponse response, Object context) {
                                try {
                                    loginHandler(response);
                                } catch (GSKeyNotFoundException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, false, null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case "email":
                    displayEmailSigninView(false, "");
                    break;
            }
        }

    }


    public void linkAccounts() {
        Log.d(TAG, "linkAccounts");

        GSObject params = new GSObject();
        params.put("regToken", globalRegToken);

        GSResponseListener resListener = new GSResponseListener() {
            @Override
            public void onGSResponse(String method, GSResponse response, Object context) {
                Log.d(TAG, response.toString());

                try {
                    Log.d(TAG, response.toString());
                    linkHandler(response);

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };

        String methodName = "accounts.getConflictingAccount";
        GSAPI.getInstance().sendRequest(methodName, params, resListener, null);

    }

    public void loginHandler(GSResponse response) throws GSKeyNotFoundException {

        Log.d(TAG, "loginHandler");
        Log.d(TAG, response.toString());

        if (response.getErrorCode() != 0) // something is not OK
        {
            needToFinalize = false;
            switch (response.getErrorCode()) {
                case 200010: // Login identifier already in use
                    linkAccounts();
                    break;
                case 403043: // login identifier already exists
                    //Call a method that handles account linking and pass it the regToken returned from the initial login

                    if(response.getData().containsKey("regToken"))
                    {
                        globalRegToken = response.getData().getString("regToken");
                        System.out.println("globalRegToken");
                        System.out.println(globalRegToken);
                        linkAccounts();
                    }
                    else
                    {
                        Log.d(TAG, "No regToken");
                    }

                    break;
                case 206001: // required information is missing : account pending registration
                    globalRegToken = response.getData().getString("regToken");
                    System.out.println("globalRegToken");
                    System.out.println(globalRegToken);
                    if (globalRegToken != null && globalRegToken != "") {
                        showEmailInputAlertAndContinue();
                    } else {
                        Log.d(TAG, "No regToken");
                    }
                    break;
                case 200009:
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
                    break;
                default:
                    Toast.makeText(getApplicationContext(), response.getErrorMessage(), Toast.LENGTH_LONG).show();
                    break;
            }



        }
        else if(needToFinalize)
        {
            needToFinalize = false;

            GSObject paramsFinalize = new GSObject();
            paramsFinalize.put("regToken", globalRegToken);

            GSResponseListener resListener = new GSResponseListener() {
                @Override
                public void onGSResponse(String method, GSResponse response, Object context) {
                    try {
                        Log.d(TAG, response.toString());

                    } catch (Exception ex) {  ex.printStackTrace();  }
                }
            };

            // Step 3 - Sending the request
            String methodName = "accounts.finalizeRegistration";
            GSAPI.getInstance().sendRequest(methodName, paramsFinalize, resListener, null);
        }

    }



    private void showSocialLoginList() {
// Present the Login user interface.
        // GSAPI.getInstance().setLoginBehavior(GSAPI.LoginBehavior.WEBVIEW_DIALOG);
        GSLoginUIListener gsLoginUIListener = new GSLoginUIListener() {
            @Override
            public void onLogin(String s, GSObject gsObject, Object o) {
                Log.d(TAG, "showLoginUI : onClose");
                System.out.println(s);
                System.out.println(gsObject);
                System.out.println(o);

            }

            @Override
            public void onLoad(Object o) {
                Log.d(TAG, "showLoginUI : onLoad");
                System.out.println(o);
            }

            @Override
            public void onClose(boolean b, Object o) {
                Log.d(TAG, "showLoginUI : onClose");
                System.out.println(b);
                System.out.println(o);

            }

            @Override
            public void onError(GSResponse gsResponse, Object o) {
                Log.d(TAG, "showLoginUI : onError");
                System.out.println(gsResponse);
                System.out.println(o);

                // Toast.makeText(getApplicationContext(), gsResponse.getErrorMessage(), Toast.LENGTH_LONG).show();
                try {
                    loginHandler(gsResponse);
                } catch (GSKeyNotFoundException e) {
                    e.printStackTrace();
                }

            }
        };

        GSObject params = new GSObject();
        params.put("enabledProviders", "Amazon,AOL,Blogger,Foursquare,Instagram,Kaixin,LINE,LinkedIn,Livedoor,liveJournal,mixi,Netlog,Odnoklassniki,openID,OrangeFr,PayPal,QQ,Renren,Signon,Sina,Skyrock,Spiceworks,Typepad,VeriSign,VKontakte,VZnet,WeChat,Wordpress,Xing,Yahoo,YahooJapan");
        GSAPI.getInstance().showLoginUI(params, gsLoginUIListener, null);
    }



    public void login(String provider) throws Exception {




        GSObject params = new GSObject();
        switch (provider) {
            case "facebook":
                params.put("provider", "facebook");

                GSAPI.getInstance().login(this, params, new GSResponseListener() {
                    @Override
                    public void onGSResponse(String method, GSResponse response, Object context) {
                        try {
                            loginHandler(response);
                        } catch (GSKeyNotFoundException e) {
                            e.printStackTrace();
                        }

                    }
                }, false, null);
                break;
            case "googleplus":
                params.put("provider", "googleplus");
                GSAPI.getInstance().login(this, params, new GSResponseListener() {
                    @Override
                    public void onGSResponse(String method, GSResponse response, Object context) {
                        try {
                            loginHandler(response);
                        } catch (GSKeyNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }, false, null);

/*

                GSObject paramss = new GSObject();
                params.put("screenSet", "Default-RegistrationLogin");
                GSAPI.getInstance().showPluginDialog("accounts.screenSet", paramss, null, null);
*/

                break;
            case "twitter":
                params.put("provider", "twitter");
                GSAPI.getInstance().login(this, params, new GSResponseListener() {
                    @Override
                    public void onGSResponse(String method, GSResponse response, Object context) {
                        try {
                            loginHandler(response);
                        } catch (GSKeyNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }, false, null);
                break;
            case "email":
                displayEmailSigninView(false, "");
                break;
        }

    }


    public void logout() {
        GSAPI.getInstance().logout();
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_item, android.R.id.text1);
        // friendsSpinner.setAdapter(spinnerAdapter);
        spinnerAdapter.notifyDataSetChanged();
    }

    public void showEmailInputAlertAndContinue(){

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        final EditText edittext = new EditText(getApplicationContext());
        edittext.setTextColor(Color.BLACK);
        alert.setMessage("Missing required information");
        alert.setTitle("Please enter your email :");

        alert.setView(edittext);

        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //What ever you want to do with the value
                String emailInputText = edittext.getText().toString();

                GSObject params = new GSObject();
                params.put("regToken", globalRegToken);
                params.put("profile", "{ \"email\" : \""+emailInputText+"\" }");
                params.put("conflictHandling", "saveProfileAndFail");


                GSResponseListener resListener = new GSResponseListener() {
                    @Override
                    public void onGSResponse(String method, GSResponse response, Object context) {
                        try {
                            Log.d(TAG, response.toString());
                            System.out.println("Successful setAccountInfo");
                            needToFinalize = true;
                            loginHandler(response);



                        } catch (Exception ex) {  ex.printStackTrace();  }
                    }
                };
                String methodName = "accounts.setAccountInfo";
                GSAPI.getInstance().sendRequest(methodName, params, resListener, null);
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // what ever you want to do with No option.
            }
        });

        alert.show();
    }
}
