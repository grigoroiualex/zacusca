package ro.ioc.zacusca.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;
import com.parse.starter.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import ro.ioc.zacusca.adapters.MyFriendsRecyclerViewAdapter;
import ro.ioc.zacusca.adapters.MyUserAndPackageRecyclerViewAdapter;
import ro.ioc.zacusca.entities.FriendAndTransports;
import ro.ioc.zacusca.entities.Transport;
import ro.ioc.zacusca.entities.UserAndPackage;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        //navigationView.setNavigationItemSelectedListener(this);

        View view = navigationView.inflateHeaderView(R.layout.nav_header_main);

        TextView navigationTitle = (TextView) view.findViewById(R.id.navigator_title);
        TextView navigationSubtitle = (TextView) view.findViewById(R.id.navigator_subtitle);

        ParseUser user = ParseUser.getCurrentUser();
        if (user != null) {
            navigationTitle.setText(user.getString("firstname") + " " + user.getString("lastname"));
            navigationSubtitle.setText(user.getEmail());
        }
        else {
            Log.d("DEBUG", "Error: no current user found");
        }

        navigationView.setNavigationItemSelectedListener(this);

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new MyFriendsRecyclerViewAdapter(new ArrayList<FriendAndTransports>());
        mRecyclerView.setAdapter(mAdapter);

        if (ParseFacebookUtils.isLinked(user)) {


            getDataForFbUsers();
        }
    }

    private void getDataForFbUsers() {
        GraphRequest friendsRequest = GraphRequest.newMyFriendsRequest(AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONArrayCallback() {
                    @Override
                    public void onCompleted(JSONArray objects, GraphResponse response) {
                        for (int i = 0; i < objects.length(); ++i) {
                            try {
                                String userName = objects.getJSONObject(i).getString("name");
                                String facebookId = objects.getJSONObject(i).getString("id");

                                final FriendAndTransports friendAndTransports = new FriendAndTransports(userName);

                                HashMap<String, String> params = new HashMap<String, String>();
                                params.put("facebookId", facebookId);
                                ParseCloud.callFunctionInBackground("getTransportsByFbId", params, new FunctionCallback<Object>() {
                                    @Override
                                    public void done(Object object, ParseException e) {
                                        if (e == null) {
                                            Log.d("DEBUG", "obj response: " + object.toString());
                                            try {
                                                JSONArray jArray = new JSONArray(object.toString());
                                                for (int i = 0; i < jArray.length(); ++i) {
                                                    JSONObject jObj = jArray.getJSONObject(i);
                                                    String objectId = jObj.getString("objectId");
                                                    String source = jObj.getString("source");
                                                    String destination = jObj.getString("destination");
                                                    Integer slotsAvailable = Integer.parseInt(jObj.getString("slotsAvailable"));
                                                    String date = jObj.getString("date");
                                                    date = date.substring(0, date.indexOf("T"));

                                                    friendAndTransports.addTransport(new Transport(source, destination, date, slotsAvailable, objectId));
                                                }
                                                if (jArray.length() > 0) {
                                                    ((MyFriendsRecyclerViewAdapter) mAdapter).addItem(friendAndTransports, 0);
                                                    mAdapter.notifyDataSetChanged();
                                                }
                                            } catch (JSONException e1) {
                                                e1.printStackTrace();
                                            }
                                        }
                                        else {
                                            Log.d("DEBUG", "getTransportsByFbId: " + e.getMessage());
                                        }
                                    }
                                });

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        Log.d("DEBUG", objects.length() + " " + objects.toString());
                    }
                });
        friendsRequest.executeAsync();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.my_packs) { // handle my packs
            Toast.makeText(this, "Pachetele mele", Toast.LENGTH_LONG).show();
            Intent i = new Intent(MainActivity.this, MyPackagesActivity.class);
            startActivity(i);
        } else if (id == R.id.my_transports) { // handle my transports
            Toast.makeText(this, "Transporturile mele", Toast.LENGTH_LONG).show();
            Intent i = new Intent(MainActivity.this, MyTransportsActivity.class);
            startActivity(i);
        } else if (id == R.id.send_pack) { // send a pack
            Toast.makeText(this, "Vreau să trimit pachet", Toast.LENGTH_LONG).show();
            Intent i = new Intent(MainActivity.this, AddPackageActivity.class);
            startActivity(i);
        } else if (id == R.id.transport_pack) { // transport a pack
            Toast.makeText(this, "Pot duce un pachet", Toast.LENGTH_LONG).show();
            Intent i = new Intent(MainActivity.this, RegisterTransportActivity.class);
            startActivity(i);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
