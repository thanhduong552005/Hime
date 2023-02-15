package com.example.khkt_2023.ui.main;

import static android.app.Activity.RESULT_OK;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.example.khkt_2023.R;
import com.example.khkt_2023.StoryContent;
import com.example.khkt_2023.StoryDetail;
import com.example.khkt_2023.models.Story;
import com.example.khkt_2023.models.StoryAdapter;
import com.example.khkt_2023.models.Suggestion;
import com.example.khkt_2023.models.SuggestionAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class StoryListActivity extends AppCompatActivity {

    private MainViewModel mViewModel;
    public String endpoint = "https://e946-2405-4803-c7a8-2ac0-ffff-ffff-ffff-ffe8.ap.ngrok.io/api/v1/upload";
    public StoryListActivity(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_list);
        //Bundle extras = getIntent().getExtras();

        myOnClickListener = new MyOnClickListener(this);

        Log.d("hihi", "hihihi");
        adapter = new StoryAdapter(data);
        recyclerView = (RecyclerView) findViewById(R.id.story_card_list);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

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

    String currentPhotoPath;
    File photoFileGlobal;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = this.getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */);

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    static final int REQUEST_IMAGE_CAPTURE = 1;

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(this.getApplicationContext().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this.getApplicationContext(), "com.example.android.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                photoFileGlobal = photoFile;

//                pb.setVisibility(View.VISIBLE);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            if (photoFileGlobal != null) {
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        uploadPicture(photoFileGlobal);
                    }
                });
                t.start();
            } else {
                Log.d("huyhuhuhu", "dont know why");
            }
        }
    }

    public void takePicture(View view) {
        dispatchTakePictureIntent();
    }

    public void startActivityFromMainThread() {

        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                Intent storyContent = new Intent(StoryListActivity.this, StoryContent.class);
                storyContent.putExtra("IMAGE_PATH", photoFileGlobal.getAbsolutePath());
                startActivity(storyContent);
            }
        });
    }

    private void uploadPicture(File photo) {
        Log.d("image path", photo.getAbsolutePath());
        URL url = null;
        try {
            url = new URL(endpoint);
            HttpURLConnection connection = null;
            DataOutputStream outputStream = null;
            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "*****";
            Log.d("debug 1", "fuck");
            int bytesRead, bytesAvailable, bufferSize;
            byte[] buffer;
            int maxBufferSize = 1 * 1024 * 1024;

            FileInputStream fileInputStream = null;
            fileInputStream = new FileInputStream(photo);
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            Log.d("debug 2", "fuck");

            outputStream = new DataOutputStream(connection.getOutputStream());
            outputStream.writeBytes(twoHyphens + boundary + lineEnd);
            outputStream.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"" + photo.getAbsolutePath() + "\"" + lineEnd);
            outputStream.writeBytes(lineEnd);
            Log.d("debug 3", "fuck");

            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            Log.d("debug 4", "fuck");

            while (bytesRead > 0) {
                outputStream.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }

            outputStream.writeBytes(lineEnd);
            outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            Log.d("debug 5.-1", "fuck");
            int serverResponseCode = connection.getResponseCode();
            Log.d("debug 5.0", "fuck");
            outputStream.flush();
            outputStream.close();

            Log.d("debug 5.1", "fuck");

            String serverResponseMessage = connection.getResponseMessage();

            Log.d("response", String.valueOf(serverResponseCode));

            Log.d("debug 6", "fuck");

            fileInputStream.close();
            Log.d("debug 7", "fuck");

            switch (serverResponseCode) {
                case 200:
                    Log.d("debug 8", "fuck");

                    //all went ok - read response
//                    pb.setVisibility(View.INVISIBLE);
                    startActivityFromMainThread();
                    Log.d("code", String.valueOf(serverResponseCode));
                    Log.d("message", serverResponseMessage);
                    break;

                default:
                    Log.d("debug 9", "fuck");

                    Log.d("status", serverResponseMessage);

                    //do something sensible
            }
        } catch (FileNotFoundException e) {
            Log.d("debug 10", "fuck");
            e.printStackTrace();
        } catch (ProtocolException e) {
            Log.d("debug 11", "fuck");
            e.printStackTrace();
        } catch (MalformedURLException e) {
            Log.d("debug 12", "fuck");
            e.printStackTrace();
        } catch (IOException e) {
            Log.d("debug 13", "fuck");
            e.printStackTrace();
        }
    }

    public class MyOnClickListener implements View.OnClickListener {

        private Context context;

        private MyOnClickListener(Context context) {
            this.context = context;
        }

        @Override
        public void onClick(View v) {
            // ????????????????
            // Bấm 1 nút vô 2 màn hình?
            // Em có xài app nào như vậy chưa?
            // em vào nó ở màn hình bài học
            // vậy giờ phải làm bên kai trc mới qua đc ha anh
            //goToStoryDetail(v);
            gotoMainFragment(v);
        }

//        private void goToStoryDetail(View v) {
//            int selectedItemPosition = recyclerView.getChildPosition(v);
//            RecyclerView.ViewHolder viewHolder
//                    = recyclerView.findViewHolderForPosition(selectedItemPosition);
//            TextView textViewTitle
//                    = (TextView) viewHolder.itemView.findViewById(R.id.story_title);
//            String selectedTitle = (String) textViewTitle.getText();
//
//            Intent detailStoryIntent = new Intent(this.context, StoryDetail.class);
//            detailStoryIntent.putExtra("TITLE", selectedTitle);
//            this.context.startActivity(detailStoryIntent);
//        }

        private void gotoMainFragment(View v) {
            Intent detailStoryIntent = new Intent(this.context, MainFragment.class);
            this.context.startActivity(detailStoryIntent);
        }

    }
}