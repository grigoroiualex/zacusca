package ro.ioc.zacusca.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.parse.starter.R;

import java.util.ArrayList;

import ro.ioc.zacusca.entities.FriendAndTransports;
import ro.ioc.zacusca.entities.Transport;

public class MyFriendsRecyclerViewAdapter extends RecyclerView
        .Adapter<MyFriendsRecyclerViewAdapter
        .DataObjectHolder> {

    private static String LOG_TAG = "MyFriendsRecyclerViewAdapter";
    private ArrayList<FriendAndTransports> mDataSet;

    public static class DataObjectHolder extends RecyclerView.ViewHolder {
        TextView userName;
        TextView transports;

        public DataObjectHolder(View itemView) {
            super(itemView);
            userName = (TextView) itemView.findViewById(R.id.text_view_username);
            transports = (TextView) itemView.findViewById(R.id.text_view_transports);
        }
    }

    public MyFriendsRecyclerViewAdapter(ArrayList<FriendAndTransports> list) {
        mDataSet = list;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_view_row_friend_transport, parent, false);

        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(DataObjectHolder holder, final int position) {
        holder.userName.setText(mDataSet.get(position).getUsername());
        StringBuffer transportsList = new StringBuffer();
        for (Transport transport : mDataSet.get(position).getTransports()) {
            transportsList.append(transport.getSource() + " - " + transport.getDestination() + " pe " +
                                        transport.getDate() + "\n");
        }
        holder.transports.setText(transportsList.toString());
    }

    public void addItem(FriendAndTransports dataObj, int index) {
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
