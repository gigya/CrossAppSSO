package com.example.davidkrief.myapplication;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.gigya.socialize.GSObject;
import com.gigya.socialize.GSResponse;
import com.gigya.socialize.android.GSLoginRequest;
import com.gigya.socialize.android.GSWebBridge;
import com.gigya.socialize.android.event.GSWebBridgeListener;

public class SignUpWebbridgeActivity extends AppCompatActivity {


    private static final String TAG = MainActivity.class.getCanonicalName();
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_webbridge_mode);

        webView = (WebView) findViewById(R.id.webView);



        GSWebBridge.attach(this, webView, new GSWebBridgeListener() {
            @Override
            public void beforeLogin(WebView webView, GSLoginRequest.LoginRequestType requestType, GSObject params) {
                super.beforeLogin(webView, requestType, params);

                Log.d(TAG, "beforeLogin");
                Log.d(TAG, params.toString());

            }

            @Override
            public void onLoginResponse(WebView webView, GSResponse response) {
                super.onLoginResponse(webView, response);

                Toast.makeText(getApplicationContext(), "logged in", Toast.LENGTH_LONG).show();

                Log.d(TAG, "onLoginResponse");
                Log.d(TAG, response.toString());

            }

            @Override
            public void onPluginEvent(WebView webView, GSObject gsObject, String s) {
                Log.d(TAG, "onPluginEvent : "+s);
                Log.d(TAG, gsObject.toString());
            }
        });

        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView webView, String url) {

                if (GSWebBridge.handleUrl(webView, url)) {
                    //  Log.d(TAG, "Bridge : " + url);
                    return true;
                }
                //  Log.d(TAG, "Webview : " + url);
                return false;

            }
        });


        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setJavaScriptEnabled(true);


        // Allow third party cookies for Android Lollipop
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CookieManager.getInstance().setAcceptThirdPartyCookies(webView,true);
        }

        webView.loadUrl("https://david.gigya-cs.com/webbridge.php?apiKey=" + getString(R.string.gigya_api_key));


    }


}
