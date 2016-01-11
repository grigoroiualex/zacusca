package ro.ioc.zacusca.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.starter.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import ro.ioc.zacusca.entities.*;
import ro.ioc.zacusca.adapters.MyTransportsRecyclerViewAdapter;

public class MyTransportsActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_transports);

        getSupportActionBar().setTitle("Transporturile mele");

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

    }

    @Override
    protected void onResume() {
        super.onResume();

        mAdapter = new MyTransportsRecyclerViewAdapter(new ArrayList<Transport>());
        mRecyclerView.setAdapter(mAdapter);
        getDataSet();
        ((MyTransportsRecyclerViewAdapter) mAdapter).setOnItemClickListener(new MyTransportsRecyclerViewAdapter
                .MyClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                Intent i = new Intent(MyTransportsActivity.this, ShowTransportActivity.class);
                i.putExtra("transportId", ((MyTransportsRecyclerViewAdapter) mAdapter).getItem(position).getObjectId());
                startActivity(i);
            }
        });
    }

    private void getDataSet() {
        ParseUser user = ParseUser.getCurrentUser();
        if (user != null) {
            ParseCloud.callFunctionInBackground("getTransportsByCurrentUser", new HashMap<String, Object>(), new FunctionCallback<Object>() {

                @Override
                public void done(Object object, ParseException e) {
                    if (e == null) {
                        try {
                            JSONArray jArray = new JSONArray(object.toString());
                            Log.d("DEBUG", "PC: " + jArray.toString());

                            for (int i = 0; i < jArray.length(); ++i) {
                                JSONObject jObj = jArray.getJSONObject(i);
                                String objectId = jObj.getString("objectId");
                                String source = jObj.getString("source");
                                String destination = jObj.getString("destination");
                                Integer slotsAvailable = Integer.parseInt(jObj.getString("slotsAvailable"));
                                String date = jObj.getString("date");
                                date = date.substring(0, date.indexOf("T"));

                                ((MyTransportsRecyclerViewAdapter) mAdapter).addItem(
                                        new Transport(source, destination, date, slotsAvailable, objectId), 0
                                );
                            }
                            mAdapter.notifyDataSetChanged();
                        } catch (JSONException ex) {
                            Log.d("DEBUG", ex.getMessage());
                        }
                        Log.d("DEBUG", "PC: " + object.toString());
                    } else {
                        Log.d("DEBUG", "PC: " + e.getMessage());
                    }
                }
            });
        }
    }

}
