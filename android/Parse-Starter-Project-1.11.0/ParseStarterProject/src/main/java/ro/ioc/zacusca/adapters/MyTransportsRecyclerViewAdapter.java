package ro.ioc.zacusca.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.parse.starter.R;

import java.util.ArrayList;

import ro.ioc.zacusca.entities.Transport;

public class MyTransportsRecyclerViewAdapter extends RecyclerView
        .Adapter<MyTransportsRecyclerViewAdapter
        .DataObjectHolder> {
    private static String LOG_TAG = "MyTransportsRecyclerViewAdapter";
    private ArrayList<Transport> mDataset;
    private static MyClickListener myClickListener;

    public static class DataObjectHolder extends RecyclerView.ViewHolder
            implements View
            .OnClickListener {
        TextView source;
        TextView destination;
        TextView data;
        TextView slotsAvailable;

        public DataObjectHolder(View itemView) {
            super(itemView);
            source = (TextView) itemView.findViewById(R.id.text_view_source);
            destination = (TextView) itemView.findViewById(R.id.text_view_destination);
            data = (TextView) itemView.findViewById(R.id.text_view_date);
            slotsAvailable = (TextView) itemView.findViewById(R.id.text_view_slots_avail);
            Log.i(LOG_TAG, "Adding Listener");
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            myClickListener.onItemClick(getAdapterPosition(), v);
        }
    }

    public void setOnItemClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    public MyTransportsRecyclerViewAdapter(ArrayList<Transport> myDataset) {
        mDataset = myDataset;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_view_row_transport, parent, false);

        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(DataObjectHolder holder, int position) {
        holder.source.setText(mDataset.get(position).getSource());
        holder.destination.setText(mDataset.get(position).getDestination());
        holder.data.setText(mDataset.get(position).getDate().toString());
        holder.slotsAvailable.setText(mDataset.get(position).getSlotsAvailable().toString());
    }

    public void addItem(Transport dataObj, int index) {
        mDataset.add(index, dataObj);
        notifyItemInserted(index);
    }

    public Transport getItem(int index) {
        return mDataset.get(index);
    }

    public void deleteItem(int index) {
        mDataset.remove(index);
        notifyItemRemoved(index);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public interface MyClickListener {
        public void onItemClick(int position, View v);
    }
}
