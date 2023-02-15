package com.example.khkt_2023.ui.main;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.khkt_2023.R;
import com.example.khkt_2023.models.Suggestion;

public class StoryCardActivity extends RecyclerView.Adapter<com.example.khkt_2023.models.SuggestionAdapter.ViewHolder> {

    private Suggestion[] localDataSet;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView getTitle() {
            return title;
        }

        public TextView getContent() {
            return content;
        }

        private final TextView title;
        private final TextView content;
        private final Button button;

        public ViewHolder(View view) {
            super(view);
            //Define click listener for the ViewHolder's View

            title = (TextView) view.findViewById(R.id.story_title);
            content = (TextView) view.findViewById(R.id.story_content);
            button = (Button) view.findViewById(R.id.story_button);
        }
    }

    public StoryCardActivity(Suggestion[] dataSet) {
        localDataSet = dataSet;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public com.example.khkt_2023.models.SuggestionAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.story_card, viewGroup, false);

        return new com.example.khkt_2023.models.SuggestionAdapter.ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(com.example.khkt_2023.models.SuggestionAdapter.ViewHolder viewHolder, final int position) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.getTitle().setText(localDataSet[position].getTitle());
        viewHolder.getContent().setText(localDataSet[position].getContent());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return localDataSet.length;
    }
}