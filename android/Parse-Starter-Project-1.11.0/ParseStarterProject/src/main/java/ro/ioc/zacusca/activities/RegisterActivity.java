package ro.ioc.zacusca.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;
import com.parse.starter.R;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        final EditText nume = (EditText) findViewById(R.id.edit_text_nume);
        final EditText prenume = (EditText) findViewById(R.id.edit_text_prenume);
        final EditText email = (EditText) findViewById(R.id.edit_text_email);
        final EditText phone = (EditText) findViewById(R.id.edit_text_telephone);
        final EditText password = (EditText) findViewById(R.id.edit_text_password);

        Button button = (Button) findViewById(R.id.button_register);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ParseUser user = new ParseUser();
                user.setEmail(email.getText().toString());
                user.setUsername(email.getText().toString());
                user.setPassword(password.getText().toString());

                user.put("telephone", phone.getText().toString());
                user.put("firstname", nume.getText().toString());
                user.put("lastname", prenume.getText().toString());

                user.signUpInBackground(new SignUpCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            // mergem mai departe
                            Toast.makeText(RegisterActivity.this, "Register success!", Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(RegisterActivity.this, MainActivity.class);
                            startActivity(i);
                        }
                        else {
                            Log.d("DEBUG", e.getMessage());
                            Toast.makeText(RegisterActivity.this, "Naspa!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}
