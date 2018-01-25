package com.example.anders.wellactually;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Anders on 24/01/2018.
 */

public class RecordingsViewAdapter extends RecyclerView.Adapter<RecordingsViewAdapter.ViewHolder> {
    private ArrayList<RecordingListItem> mDataset;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView cardName;
        public TextView cardTimestamp;
        public CardView cardView;
        public ViewHolder(View itemView) {
            super(itemView);
            cardName = itemView.findViewById(R.id.cardview_text);
            cardView = itemView.findViewById(R.id.files_cardview);
            cardTimestamp = itemView.findViewById(R.id.filestab_duration);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public RecordingsViewAdapter(ArrayList<RecordingListItem> myDataset) {
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RecordingsViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recording_item, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.cardName.setText(mDataset.get(position).getName());
        holder.cardTimestamp.setText(mDataset.get(position).getTimestamp());

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}

