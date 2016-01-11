package ro.ioc.zacusca.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.parse.starter.R;

import java.util.ArrayList;

import ro.ioc.zacusca.entities.Package;

public class MyPackagesRecyclerViewAdapter extends RecyclerView
        .Adapter<MyPackagesRecyclerViewAdapter
        .DataObjectHolder> {
    private static String LOG_TAG = "MyPackagesRecyclerViewAdapter";
    private ArrayList<Package> mDataset;
    private static MyClickListener myClickListener;

    public static class DataObjectHolder extends RecyclerView.ViewHolder
            implements View
            .OnClickListener {
        TextView name;
        TextView source;
        TextView destination;
        TextView data;

        public DataObjectHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.text_view_name);
            source = (TextView) itemView.findViewById(R.id.text_view_source);
            destination = (TextView) itemView.findViewById(R.id.text_view_destination);
            data = (TextView) itemView.findViewById(R.id.text_view_date);
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

    public MyPackagesRecyclerViewAdapter(ArrayList<Package> myDataset) {
        mDataset = myDataset;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_view_row_package, parent, false);

        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(DataObjectHolder holder, int position) {
        holder.name.setText(mDataset.get(position).getName());
        holder.source.setText(mDataset.get(position).getSource());
        holder.destination.setText(mDataset.get(position).getDestination());
        holder.data.setText(mDataset.get(position).getDate().toString());
    }

    public void addItem(Package dataObj, int index) {
        mDataset.add(index, dataObj);
        notifyItemInserted(index);
    }

    public void deleteItem(int index) {
        mDataset.remove(index);
        notifyItemRemoved(index);
    }

    public Package getItem(int index) {
        return mDataset.get(index);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public interface MyClickListener {
        public void onItemClick(int position, View v);
    }
}
