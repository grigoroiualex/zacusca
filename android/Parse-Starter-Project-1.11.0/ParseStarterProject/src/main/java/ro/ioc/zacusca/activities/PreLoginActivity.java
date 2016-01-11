package ro.ioc.zacusca.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;
import com.parse.starter.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class PreLoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_login);

        Button register = (Button) findViewById(R.id.button_register);
        Button login = (Button) findViewById(R.id.button_log_in);
        Button fbLogin = (Button) findViewById(R.id.button_fb_login);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(PreLoginActivity.this, RegisterActivity.class);
                startActivity(i);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(PreLoginActivity.this, LogInActivity.class);
                startActivity(i);
            }
        });

        fbLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ParseFacebookUtils.logInWithReadPermissionsInBackground(PreLoginActivity.this, Arrays.asList("user_friends"),
                        new LogInCallback() {
                            @Override
                            public void done(ParseUser user, ParseException e) {
                                if (user == null) {
                                    Log.d("DEBUG", "Uh oh. The user cancelled the Facebook login.");
                                } else if (user.isNew()) {
                                    Log.d("DEBUG", "User signed up and logged in through Facebook!");
                                } else {
                                    Log.d("DEBUG", "User logged in through Facebook!");
                                }

                                if (user != null) {
                                    final ParseUser finalUser = user;
                                    GraphRequest request = GraphRequest.newMeRequest(
                                            AccessToken.getCurrentAccessToken(),
                                            new GraphRequest.GraphJSONObjectCallback() {
                                                @Override
                                                public void onCompleted(
                                                        JSONObject object,
                                                        GraphResponse response) {
                                                    // Application code
                                                    try {
                                                        String id = object.getString("id");
                                                        finalUser.put("facebook_id", id);
                                                        finalUser.saveInBackground();
                                                    } catch (JSONException e1) {
                                                        e1.printStackTrace();
                                                    }

                                                    Intent i = new Intent(PreLoginActivity.this, MainActivity.class);
                                                    startActivity(i);
                                                }
                                            });
                                    Bundle parameters = new Bundle();
                                    parameters.putString("fields", "id,name,link");
                                    request.setParameters(parameters);
                                    request.executeAsync();


                                }
                            }
                        });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }
}
