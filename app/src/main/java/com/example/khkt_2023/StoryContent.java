package com.example.khkt_2023;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.khkt_2023.models.Story;
import com.example.khkt_2023.models.StoryAdapter;
import com.example.khkt_2023.ui.main.MainFragment;
import com.google.android.material.card.MaterialCardView;

public class StoryContent extends AppCompatActivity {

    public String image_path;
    public static RecyclerView recyclerView;

    public static View.OnClickListener myOnClickListener;

    public static StoryAdapter adapter;
    public static Story[] data = new Story[]{
            new Story("Story 1", "Title", "Click me daddy"),
            new Story("Story 1", "Title", "Click me daddy"),
            new Story("Story 1", "Title", "Click me daddy"),
            new Story("Story 1", "Title", "Click me daddy"),
            new Story("Story 1", "Title", "Click me daddy"),
            new Story("Story 1", "Title", "Click me daddy"),
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_content);
        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            image_path = null;
        } else {
            image_path = extras.getString("IMAGE_PATH");
        }


        myOnClickListener = new MyOnClickListener(this);

        Log.d("hihi", "hihihi");
        ImageView imageView = (ImageView) findViewById(R.id.searchImage);
        imageView.setImageURI(Uri.parse(image_path));
        adapter = new StoryAdapter(data);
        recyclerView = (RecyclerView) findViewById(R.id.story_list);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }


    public static class MyOnClickListener implements View.OnClickListener {

        private final Context context;

        private MyOnClickListener(Context context) {
            this.context = context;
        }

        @Override
        public void onClick(View v) {
            goToStoryDetail(v);
        }

        private void goToStoryDetail(View v) {
            int selectedItemPosition = recyclerView.getChildPosition(v);
            RecyclerView.ViewHolder viewHolder
                    = recyclerView.findViewHolderForPosition(selectedItemPosition);
            TextView textViewTitle
                    = (TextView) viewHolder.itemView.findViewById(R.id.story_title);
            String selectedTitle = (String) textViewTitle.getText();

            Intent detailStoryIntent = new Intent(this.context, StoryDetail.class);
            detailStoryIntent.putExtra("TITLE", selectedTitle);
            this.context.startActivity(detailStoryIntent);
        }
    }
}
