package com.example.davidkrief.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.gigya.socialize.GSObject;
import com.gigya.socialize.GSResponse;
import com.gigya.socialize.GSResponseListener;
import com.gigya.socialize.android.GSAPI;
import com.gigya.socialize.android.GSPluginFragment;

public class LoggedActivity extends AppCompatActivity {

    private TextView userStatus;
    private Button logoutButton;

    public static LoggedActivity ma;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        ma = this;
        // Gigya
        logoutButton = (Button) findViewById(R.id.logoutButton);
        userStatus = (TextView) findViewById(R.id.userStatus);
        userStatus.setText("");

        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        userStatus.setText("Logged as "+message);


        // Comment plugin loaded within "layout"
        GSObject paramsComments = new GSObject();
        paramsComments.put("categoryID", "test");
        paramsComments.put("streamID", "AndroidApp");
        GSPluginFragment pluginFragmentComments = GSPluginFragment.newInstance("comments.commentsUI", paramsComments);
        getSupportFragmentManager().beginTransaction().add(R.id.commentLayout, pluginFragmentComments, "comments").commit();


        // Rating plugin loaded within "layout"
        GSObject paramsRating = new GSObject();
        paramsRating.put("categoryID", "rating");
        paramsRating.put("streamID", "AndroidApp");
        GSPluginFragment pluginFragmentRating = GSPluginFragment.newInstance("comments.commentsUI", paramsRating);
        getSupportFragmentManager().beginTransaction().add(R.id.shareLayout, pluginFragmentRating, "rating").commit();


        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GSAPI.getInstance().logout();
                finish();
            }
        });

    }


}
