package ro.ioc.zacusca.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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
import java.util.List;
import java.util.Map;

import ro.ioc.zacusca.adapters.MyUserAndPackageRecyclerViewAdapter;
import ro.ioc.zacusca.adapters.MyUserAndTransportsRecyclerViewAdapter;
import ro.ioc.zacusca.entities.UserAndPackage;
import ro.ioc.zacusca.entities.UserAndTransport;

public class ShowTransportActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private String transportId;
    private Location location;
    private Boolean locationSharingEnabled;
    private LocationListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_transport);

        locationSharingEnabled = false;

        transportId = getIntent().getStringExtra("transportId");
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new MyUserAndPackageRecyclerViewAdapter(new ArrayList<UserAndPackage>(), transportId);
        mRecyclerView.setAdapter(mAdapter);
        getDataSet();

        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location loc) {
                Log.d("DEBUG", "Location changed");
                if (locationSharingEnabled) {
                    final Location location = loc;
                    ParseQuery<ParseObject> query = ParseQuery.getQuery("Transport");
                    query.getInBackground(transportId, new GetCallback<ParseObject>() {
                        @Override
                        public void done(ParseObject object, ParseException e) {
                            if (e == null) {
                                if (location != null) {
                                    object.put("long", location.getLongitude() + "");
                                    object.put("lat", location.getLatitude() + "");
                                    object.saveInBackground();
                                    Toast.makeText(ShowTransportActivity.this, "Location Shared", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(ShowTransportActivity.this, "Location Not Shared", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Log.d("DEBUG", "GET TRANSPORT ERROR: " + e.getMessage());
                            }
                        }
                    });
                    locationSharingEnabled = false;
                }
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };

        Button buttonShareLocation = (Button) findViewById(R.id.button_share_location);
        buttonShareLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                Criteria criteria = new Criteria();
                criteria.setAccuracy(Criteria.ACCURACY_FINE);
                String bestProvider = locationManager.getBestProvider(criteria, false);
                List<String> providers = locationManager.getProviders(true);
                locationSharingEnabled = true;
                try {
                    locationManager.requestLocationUpdates(bestProvider, 0, 10, listener);
                    for (String provider : providers) {
                        final Location location = locationManager.getLastKnownLocation(provider);
                        if (location != null) {
                            locationSharingEnabled = false;
                            Log.d("DEBUG", "Location is not null!");
                            ParseQuery<ParseObject> query = ParseQuery.getQuery("Transport");
                            query.getInBackground(transportId, new GetCallback<ParseObject>() {
                                @Override
                                public void done(ParseObject object, ParseException e) {
                                    if (e == null) {
                                        if (location != null) {
                                            object.put("long", location.getLongitude() + "");
                                            object.put("lat", location.getLatitude() + "");
                                            object.saveInBackground();
                                            Toast.makeText(ShowTransportActivity.this, "Location Shared", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(ShowTransportActivity.this, "Location Not Shared", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        Log.d("DEBUG", "GET TRANSPORT ERROR: " + e.getMessage());
                                    }
                                }
                            });
                            break;
                        }
                    }

                }
                catch (SecurityException e) {
                    Log.d("DEBUG", "Security exception " + e.getMessage());
                }
            }
        });
    }


    private void getDataSet() {
        ParseUser user = ParseUser.getCurrentUser();
        if (user != null) {
            HashMap<String, String> params = new HashMap<String, String>();
            HashMap<String, Map<String, String>> hashMap = new HashMap<>();
            params.put("objectId", transportId);
            hashMap.put("transport", params);
            ParseCloud.callFunctionInBackground("getPackagesOnBoardForTransport", hashMap, new FunctionCallback<Object>() {
                @Override
                public void done(Object object, ParseException e) {
                    if (e == null) {
                        Log.d("DEBUG", "PC: " + object.toString());

                        try {
                            JSONArray jArray = new JSONArray(object.toString());
                            for (int i = 0; i < jArray.length(); ++i) {
                                JSONObject jObj = jArray.getJSONObject(i);
                                String objectId = jObj.getString("objectId");
                                String name = jObj.getString("name");
                                String source = jObj.getString("source");
                                String destination = jObj.getString("destination");
                                String date = jObj.getString("date");
                                String state = jObj.getString("state");
                                String userFullName = jObj.getString("userFullname");
                                String userEmail = jObj.getString("userEmail");
                                String userPhone = jObj.getString("userTelephone");

                                ((MyUserAndPackageRecyclerViewAdapter) mAdapter).addItem(
                                        new UserAndPackage(objectId, name, source, destination, date, state, userFullName, userEmail, userPhone), 0
                                );
                            }
                            mAdapter.notifyDataSetChanged();
                        } catch (JSONException ex) {
                            Log.d("DEBUG", ex.getMessage());
                        }

                    } else {
                        Log.d("DEBUG", "PC: " + e.getMessage());
                    }
                }
            });
        }
    }
}
