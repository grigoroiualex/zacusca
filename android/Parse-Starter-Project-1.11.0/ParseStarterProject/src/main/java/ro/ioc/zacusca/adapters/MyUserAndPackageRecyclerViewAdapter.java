package ro.ioc.zacusca.adapters;

import android.app.Activity;
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

import ro.ioc.zacusca.entities.UserAndPackage;

public class MyUserAndPackageRecyclerViewAdapter extends RecyclerView
        .Adapter<MyUserAndPackageRecyclerViewAdapter
        .DataObjectHolder> {
    private static String LOG_TAG = "MyUserAndTransportsRecyclerViewAdapter";
    private ArrayList<UserAndPackage> mDataSet;
    private String transportId;

    public static class DataObjectHolder extends RecyclerView.ViewHolder {
        TextView packageName;
        TextView userInfo;
        Button buttonAccept;
        Button buttonDecline;

        public DataObjectHolder(View itemView) {
            super(itemView);
            packageName = (TextView) itemView.findViewById(R.id.text_view_package_name);
            userInfo = (TextView) itemView.findViewById(R.id.text_view_user_info);
            buttonAccept = (Button) itemView.findViewById(R.id.button_accept_package);
            buttonDecline = (Button) itemView.findViewById(R.id.button_decline_package);
        }
    }

    public MyUserAndPackageRecyclerViewAdapter(ArrayList<UserAndPackage> myDataSet, String transportId) {
        mDataSet = myDataSet;
        this.transportId = transportId;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_view_row_transport_packages, parent, false);

        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(DataObjectHolder holder, final int position) {
        final DataObjectHolder h = holder;
        holder.packageName.setText(mDataSet.get(position).getName());
        holder.userInfo.setText(mDataSet.get(position).getUserFullName() + " " + mDataSet.get(position).getUserEmail());
        if (mDataSet.get(position).getState().equals("pending")) {
            holder.buttonAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final View v = view;
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("pkgId", mDataSet.get(position).getObjectId());
                    params.put("transportId", transportId);
                    ParseCloud.callFunctionInBackground("acceptJoin", params, new FunctionCallback<Object>() {

                        @Override
                        public void done(Object object, ParseException e) {
                            if (e == null) {
                                Toast.makeText(v.getContext(), "Package accepted!", Toast.LENGTH_SHORT).show();
                                h.buttonAccept.setVisibility(View.GONE);
                                h.buttonDecline.setVisibility(View.GONE);
                                notifyDataSetChanged();
                            } else {
                                Log.d(LOG_TAG, e.getMessage());
                            }
                        }
                    });
                }
            });
        }
        else if (mDataSet.get(position).getState().equals("accepted")) {
            holder.buttonAccept.setVisibility(View.GONE);
            holder.buttonDecline.setVisibility(View.GONE);
        }
    }

    public void addItem(UserAndPackage dataObj, int index) {
        mDataSet.add(index, dataObj);
        notifyItemInserted(index);
    }

    public void deleteItem(int index) {
        mDataSet.remove(index);
        notifyItemRemoved(index);
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }
}
