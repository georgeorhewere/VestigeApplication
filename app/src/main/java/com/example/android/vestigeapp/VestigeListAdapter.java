package com.example.android.vestigeapp;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;

import android.support.v7.view.menu.MenuView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.example.android.vestigeapp.database.VestigeEntry;

import java.util.List;


public class VestigeListAdapter extends RecyclerView.Adapter<VestigeListAdapter.VestigeItemViewHolder> {

private Context context;
private List<VestigeEntry> dbEntries;
final private ItemClickListener vItemClickListener;


    public VestigeListAdapter(Context _context, ItemClickListener listener) {
        this.context = context;
        vItemClickListener = listener;
    }

    @NonNull
    @Override
    public VestigeItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        int layoutIdForListItem = R.layout.vestige_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);

        boolean attachToParentImmediately = false;
        View view = inflater.inflate(layoutIdForListItem,parent,attachToParentImmediately);

        VestigeItemViewHolder viewHolder = new VestigeItemViewHolder(view);

        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull VestigeItemViewHolder holder, int position) {
        VestigeEntry _entry = dbEntries.get(position);
        holder.bind( _entry);
    }

    @Override
    public int getItemCount() {
        if(dbEntries == null)
            return 0;

        return dbEntries.size();
    }

    public List<VestigeEntry> getDbEntries() {
        return dbEntries;
    }

    public interface ItemClickListener {
        void onItemClickListener(int itemId);
    }

    class VestigeItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
         TextView descriptionTextView;
         TextView statusTextView;

        public VestigeItemViewHolder(View itemView) {
            super(itemView);
            descriptionTextView = (TextView) itemView.findViewById(R.id.rv_vestigeItem);
            statusTextView = (TextView) itemView.findViewById(R.id.rv_vestigePriority);
            itemView.setOnClickListener(this);
        }

        void bind( VestigeEntry _entry){
            descriptionTextView.setText(_entry.getDescription());
            statusTextView.setText(getStatusText(_entry.getStatus()));

        }

        @Override
        public void onClick(View v) {
            int elementId = dbEntries.get(getAdapterPosition()).getId();
            Log.d("On Click ", "Clicked"+ elementId);
            vItemClickListener.onItemClickListener(elementId);

        }
    }

public String getStatusText(int statusCode){
        String statusText =  "Urgent";
        switch(statusCode){
            case 1:
                statusText = context.getString(R.string.rdb_urgent);
                break;
            case 2:
                statusText = context.getString(R.string.rdb_important);
                break;
            case 3:
                statusText = context.getString(R.string.rdb_normal);
                break;
        }
        return statusText;
}

    public void setDbEntries(List<VestigeEntry> entries){
        dbEntries = entries;
        notifyDataSetChanged();
    }
}
