package com.example.khkt_2023.models;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.khkt_2023.R;
import com.example.khkt_2023.StoryContent;

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.ViewHolder> {

    private Story[] localDataSet;

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView getTitle() {
            return title;
        }
        public TextView getDescription() {
            return description;
        }

        private final TextView title;
        private final TextView description;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View

            title = (TextView) view.findViewById(R.id.story_title);
            description = (TextView) view.findViewById(R.id.story_description);
        }
    }

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param dataSet Story[] containing the data to populate views to be used
     *                by RecyclerView.
     */
    public StoryAdapter(Story[] dataSet) {
        localDataSet = dataSet;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.suggestion_final, viewGroup, false);
        view.setOnClickListener(StoryContent.myOnClickListener);
        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.getTitle().setText(localDataSet[position].getTitle());
        viewHolder.getDescription().setText(localDataSet[position].getDescription());


    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return localDataSet.length;
    }
}
