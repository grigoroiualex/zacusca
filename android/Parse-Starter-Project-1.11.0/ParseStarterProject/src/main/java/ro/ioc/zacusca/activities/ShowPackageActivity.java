package ro.ioc.zacusca.activities;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.parse.FunctionCallback;
import com.parse.GetCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.starter.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ro.ioc.zacusca.entities.UserAndTransport;
import ro.ioc.zacusca.adapters.MyUserAndTransportsRecyclerViewAdapter;

public class ShowPackageActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private String state;
    private String date;
    private String packageId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_package);

        packageId = getIntent().getStringExtra("objectId");
        state = getIntent().getStringExtra("state");
        date = getIntent().getStringExtra("date");

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new MyUserAndTransportsRecyclerViewAdapter(new ArrayList<UserAndTransport>());
        mRecyclerView.setAdapter(mAdapter);
        getDataSet();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ((MyUserAndTransportsRecyclerViewAdapter) mAdapter).setOnItemClickListener(new MyUserAndTransportsRecyclerViewAdapter
                .MyClickListener() {
            @Override
            public void onItemClick(int position, View v) {

            }
        });
    }

    private void getDataSet() {
        ParseUser user = ParseUser.getCurrentUser();
        if (user != null) {
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("objectId", packageId);
            HashMap<String, Map<String, String>> hashMap = new HashMap<>();
            hashMap.put("pkg", params);
            ParseCloud.callFunctionInBackground("getAvailableTransportsForPackage", hashMap, new FunctionCallback<Object>() {

                @Override
                public void done(Object object, ParseException e) {
                    if (e == null) {
                        try {
                            Log.d("DEBUG", "PC: " + object.toString());

                            JSONArray jArray = new JSONArray(object.toString());
                            Log.d("DEBUG", "PC: " + jArray.toString());

                            for (int i = 0; i < jArray.length(); ++i) {
                                JSONObject jObj = jArray.getJSONObject(i);
                                String source = jObj.getString("source");
                                String destination = jObj.getString("destination");
                                //Integer slotsAvailable = Integer.parseInt(jObj.getString("slotsAvailable"));
                                String username = jObj.getString("userFullname");
                                String email = jObj.getString("userEmail");
                                String telephone = jObj.getString("userTelephone");
                                String transportId = jObj.getString("objectId");
                                // check the state of the transport
                                String transportState = "";
                                if (jObj.has("state")) {
                                    transportState = jObj.getString("state");
                                }


                                ((MyUserAndTransportsRecyclerViewAdapter) mAdapter).addItem(
                                        new UserAndTransport(packageId, transportId, username, email, telephone, source, destination, date), 0
                                );
                                if (state.equals(transportState)) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            mAdapter.notifyDataSetChanged();
                                        }
                                    });
                                    //((Button)mRecyclerView.findViewById(R.id.button_join)).setVisibility(Button.INVISIBLE);
                                    ParseQuery<ParseObject> query = ParseQuery.getQuery("Transport");
                                    query.getInBackground(transportId, new GetCallback<ParseObject>() {
                                        @Override
                                        public void done(ParseObject object, ParseException e) {
                                            if (e == null) {
                                                if (object.has("lat") && object.has("long")) {
                                                    final MyUserAndTransportsRecyclerViewAdapter.DataObjectHolder holder = (MyUserAndTransportsRecyclerViewAdapter.DataObjectHolder)mRecyclerView.findViewHolderForAdapterPosition(0);
                                                    if (holder != null) {
                                                        holder.makeJoinInvisible();
                                                        holder.makeShowLocationVisible();
                                                    }
                                                    //((Button)mRecyclerView.findViewById(R.id.button_show_location)).setVisibility(Button.VISIBLE);
                                                    ((MyUserAndTransportsRecyclerViewAdapter) mAdapter).setLatitude(Float.parseFloat(object.getString("lat")));
                                                    ((MyUserAndTransportsRecyclerViewAdapter) mAdapter).setLongitude(Float.parseFloat(object.getString("long")));
                                                }
                                            }
                                            else {
                                                Log.d("DEBUG", e.getMessage());
                                            }
                                        }
                                    });
                                }
                            }
                            mAdapter.notifyDataSetChanged();
                            final MyUserAndTransportsRecyclerViewAdapter.DataObjectHolder holder = (MyUserAndTransportsRecyclerViewAdapter.DataObjectHolder)mRecyclerView.findViewHolderForAdapterPosition(0);
                            Log.d("DEBUG", (holder != null) + "");
                        } catch (JSONException ex) {
                            Log.d("DEBUG", ex.getMessage());
                        }
//                        Log.d("DEBUG", "PC: " + object.toString());
                    } else {
                        Log.d("DEBUG", "PC: " + e.getMessage());
                    }
                }
            });
        }
    }
}
