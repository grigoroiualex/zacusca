package ro.ioc.zacusca.adapters;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.starter.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ro.ioc.zacusca.activities.MapsActivity;
import ro.ioc.zacusca.activities.ShowPackageActivity;
import ro.ioc.zacusca.entities.UserAndTransport;

public class MyUserAndTransportsRecyclerViewAdapter extends RecyclerView
        .Adapter<MyUserAndTransportsRecyclerViewAdapter
        .DataObjectHolder> {
    private static String LOG_TAG = "MyUserAndTransportsRecyclerViewAdapter";
    private ArrayList<UserAndTransport> mDataset;
    private static MyClickListener myClickListener;
    private Float latitude = null;
    private Float longitude = null;

    public static class DataObjectHolder extends RecyclerView.ViewHolder
            implements View
            .OnClickListener {
        TextView username;
        TextView email;
        TextView telephone;
        TextView source;
        TextView destination;
        TextView data;
        Button button;
        Button showLocation;
        //TextView slotsAvailable;

        public DataObjectHolder(View itemView) {
            super(itemView);
            source = (TextView) itemView.findViewById(R.id.text_view_source);
            destination = (TextView) itemView.findViewById(R.id.text_view_destination);
            data = (TextView) itemView.findViewById(R.id.text_view_date);
            username = (TextView) itemView.findViewById(R.id.text_view_username);
            email = (TextView) itemView.findViewById(R.id.text_view_email);
            telephone = (TextView) itemView.findViewById(R.id.text_view_telephone);
            button = (Button) itemView.findViewById(R.id.button_join);
            showLocation = (Button) itemView.findViewById(R.id.button_show_location);
            //slotsAvailable = (TextView) itemView.findViewById(R.id.text_view_slots_avail);
            Log.i(LOG_TAG, "Adding Listener");
            itemView.setOnClickListener(this);
        }

        public void makeJoinInvisible() {
            button.setVisibility(Button.INVISIBLE);
        }

        public void makeShowLocationVisible() {
            showLocation.setVisibility(Button.VISIBLE);
        }

        @Override
        public void onClick(View v) {
            myClickListener.onItemClick(getAdapterPosition(), v);
        }
    }

    public void setOnItemClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    public MyUserAndTransportsRecyclerViewAdapter(ArrayList<UserAndTransport> myDataset) {
        mDataset = myDataset;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_view_row_available_transport, parent, false);

        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(DataObjectHolder holder, final int position) {
        holder.source.setText(mDataset.get(position).getSource());
        holder.destination.setText(mDataset.get(position).getDestination());
        holder.data.setText(mDataset.get(position).getDate().toString());
        holder.username.setText(mDataset.get(position).getUsername());
        holder.email.setText(mDataset.get(position).getEmail());
        holder.telephone.setText(mDataset.get(position).getPhone());
        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final View v = view;
                Map<String, String> params = new HashMap<String, String>();
                Log.d(LOG_TAG, mDataset.get(position).getPackageId() + " " + mDataset.get(position).getTransportId());
                params.put("pkgId", mDataset.get(position).getPackageId());
                params.put("transportId", mDataset.get(position).getTransportId());
                ParseCloud.callFunctionInBackground("requestJoin", params, new FunctionCallback<Object>() {

                    @Override
                    public void done(Object object, ParseException e) {
                        if (e == null) {
                            Toast.makeText(v.getContext(), "Requested to join registered!", Toast.LENGTH_SHORT).show();
                            ((Activity) v.getContext()).finish();
                        } else {
                            Log.d(LOG_TAG, e.getMessage());
                        }
                    }
                });
            }
        });
        //holder.slotsAvailable.setText(mDataset.get(position).getSlotsAvailable().toString());
        holder.showLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), MapsActivity.class);
                i.putExtra("lat", latitude);
                i.putExtra("long", longitude);
                v.getContext().startActivity(i);
            }
        });
    }

    public void addItem(UserAndTransport dataObj, int index) {
        mDataset.add(index, dataObj);
        notifyItemInserted(index);
    }

    public void deleteItem(int index) {
        mDataset.remove(index);
        notifyItemRemoved(index);
    }

    public void setLatitude(Float latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(Float longitude) {
        this.longitude = longitude;
    }



    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public interface MyClickListener {
        public void onItemClick(int position, View v);
    }
}
