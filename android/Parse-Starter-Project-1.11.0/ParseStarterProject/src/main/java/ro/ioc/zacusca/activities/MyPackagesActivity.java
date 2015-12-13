package ro.ioc.zacusca.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.starter.R;

import java.util.ArrayList;
import java.util.List;

import ro.ioc.zacusca.ro.ioc.zacusca.adapters.MyPackagesRecyclerViewAdapter;
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

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new MyPackagesRecyclerViewAdapter(getDataSet());
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ((MyPackagesRecyclerViewAdapter) mAdapter).setOnItemClickListener(new MyPackagesRecyclerViewAdapter
                .MyClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                Log.i(LOG_TAG, " Clicked on Item " + position);
            }
        });
    }

    private ArrayList<Package> getDataSet() {
        ParseUser user = ParseUser.getCurrentUser();
        final ArrayList<Package> results = new ArrayList<Package>();
        if (user != null) {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Package");
            query.whereEqualTo("user", user.getObjectId());
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if (e == null) {
                        for (ParseObject obj : objects) {
                            results.add(new Package(obj.getString("source"),
                                                    obj.getString("destination"),
                                                    obj.getString("name"),
                                                    obj.getDate("date")));
                        }
                    }
                    else {
                        //return null;
                    }
                }
            });

            return results;
        }
        else {
            return null;
        }

    }
}
