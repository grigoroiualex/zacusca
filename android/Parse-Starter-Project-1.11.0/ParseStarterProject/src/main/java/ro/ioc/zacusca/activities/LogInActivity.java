package ro.ioc.zacusca.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.starter.R;

public class LogInActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        Button login = (Button) findViewById(R.id.button_log_in);

        final EditText username = (EditText) findViewById(R.id.edit_text_username);
        final EditText password = (EditText) findViewById(R.id.edit_text_password);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ParseUser.logInInBackground(username.getText().toString(), password.getText().toString(), new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException e) {
                        if (user != null) {
                            Toast.makeText(LogInActivity.this, "Hooray", Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(LogInActivity.this, MainActivity.class);
                            startActivity(i);
                        }
                        else {
                            Toast.makeText(LogInActivity.this, "Buhu", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}
