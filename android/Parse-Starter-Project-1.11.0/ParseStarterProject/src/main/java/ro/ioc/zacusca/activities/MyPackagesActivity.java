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

import ro.ioc.zacusca.adapters.MyPackagesRecyclerViewAdapter;
import ro.ioc.zacusca.entities.Package;

public class MyPackagesActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private static String LOG_TAG = "CardViewActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_packages);

        getSupportActionBar().setTitle("Pachetele mele");

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        //mAdapter = new MyPackagesRecyclerViewAdapter(new ArrayList<Package>());
        //mRecyclerView.setAdapter(mAdapter);
        //getDataSet();
    }

    @Override
    protected void onResume() {
        super.onResume();

        mAdapter = new MyPackagesRecyclerViewAdapter(new ArrayList<Package>());
        mRecyclerView.setAdapter(mAdapter);
        getDataSet();

        ((MyPackagesRecyclerViewAdapter) mAdapter).setOnItemClickListener(new MyPackagesRecyclerViewAdapter
                .MyClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                Intent i = new Intent(MyPackagesActivity.this, ShowPackageActivity.class);
                i.putExtra("objectId", ((MyPackagesRecyclerViewAdapter) mAdapter).getItem(position).getObjectId());
                i.putExtra("state", ((MyPackagesRecyclerViewAdapter) mAdapter).getItem(position).getState());
                i.putExtra("date", ((MyPackagesRecyclerViewAdapter) mAdapter).getItem(position).getDate());
                startActivity(i);
                Log.i(LOG_TAG, " Clicked on Item " + position);
            }
        });
    }

    private void getDataSet() {
        ParseUser user = ParseUser.getCurrentUser();
        if (user != null) {
            ParseCloud.callFunctionInBackground("getPackages", new HashMap<String, Object>(), new FunctionCallback<Object>() {

                @Override
                public void done(Object object, ParseException e) {
                    if (e == null) {
                        try {
                            JSONArray jArray = new JSONArray(object.toString());
                            Log.d("DEBUG", "PC: " + jArray.toString());

                            for (int i = 0; i < jArray.length(); ++i) {
                                JSONObject jObj = jArray.getJSONObject(i);
                                String name = jObj.getString("name");
                                String source = jObj.getString("source");
                                String destination = jObj.getString("destination");
                                String objectId = jObj.getString("objectId");
                                String date = jObj.getString("date");
                                String state = jObj.getString("state");
                                date = date.substring(0, date.indexOf("T"));

                                ((MyPackagesRecyclerViewAdapter) mAdapter).addItem(
                                        new Package(source, destination, name, date, state, objectId), 0
                                );
                            }
                            Log.d("DEBUG", "Count " + mAdapter.getItemCount());
                            mAdapter.notifyDataSetChanged();

                        }
                        catch (JSONException ex) {
                            Log.d("DEBUG", "Exceptieee " + ex.getMessage());
                        }
                        Log.d("DEBUG", "PC: " + object.toString());
                    } else {
                        Log.d("DEBUG", "PC: Exceptie " + e.getMessage());
                    }
                }
            });
            /*
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Package");
            query.whereEqualTo("user", user);
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if (e == null) {
                        Log.d("DEBUG", objects.toString());
                        for (ParseObject obj : objects) {
                            ((MyPackagesRecyclerViewAdapter) mAdapter).addItem(new Package(obj.getString("source"),
                                    obj.getString("destination"),
                                    obj.getString("name"),
                                    obj.getDate("date")), 0);
                        }
                        mAdapter.notifyDataSetChanged();
                    } else {
                        Log.d("DEBUG", e.getMessage());
                        //return null;
                    }
                }
            });
            */
        }
    }
}
