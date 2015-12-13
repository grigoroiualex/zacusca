package ro.ioc.zacusca.activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.starter.R;

import java.util.Calendar;
import java.util.Date;

public class AddPackageActivity extends AppCompatActivity {

    private static TextView tv_date;
    private static Date package_date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_package);

        final EditText name = (EditText) findViewById(R.id.edit_text_package_name);
        final EditText source = (EditText) findViewById(R.id.edit_text_source);
        final EditText destination = (EditText) findViewById(R.id.edit_text_destination);

        tv_date = (TextView) findViewById(R.id.text_view_info_date);

        Button chooseDate = (Button) findViewById(R.id.button_choose_date);
        Button submit = (Button) findViewById(R.id.button_submit);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ParseObject pack = new ParseObject("Package");
                pack.put("name", name.getText().toString());
                pack.put("source", source.getText().toString());
                pack.put("destination", destination.getText().toString());
                pack.put("date", package_date);

                ParseUser user = ParseUser.getCurrentUser();
                if (user != null) {
                    pack.put("user", user);
                }
                else {
                    Log.d("DEBUG", "Error: user not found");
                }

                ParseACL parseACL = new ParseACL(user);
                parseACL.setPublicReadAccess(true);
                parseACL.setPublicWriteAccess(true);

                pack.setACL(parseACL);

                pack.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Toast.makeText(getApplicationContext(), "Pachetul a fost adaugat cu succes!", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                        else {
                            Toast.makeText(getApplicationContext(), "Eroare la adaugarea pachetului!", Toast.LENGTH_SHORT).show();
                            Log.d("DEBUG", e.getMessage());
                        }
                    }
                });
            }
        });
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
            tv_date.setText(day + "/" + month + "/" + year);

            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, day);

            package_date = calendar.getTime();
        }
    }
}
