package com.example.khkt_2023.ui.main;

import static android.app.Activity.RESULT_OK;

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.khkt_2023.R;
import com.example.khkt_2023.StoryContent;
import com.example.khkt_2023.StoryDetail;
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


public class MainFragment extends Fragment{

    private MainViewModel mViewModel;

    public static View.OnClickListener myOnClickListener;

    public String endpoint = "https://e946-2405-4803-c7a8-2ac0-ffff-ffff-ffff-ffe8.ap.ngrok.io/api/v1/upload";
    private Bundle savedInstanceState;

    public MainFragment() {
    }

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        myOnClickListener = new MyOnClickListener(getContext());


        // TODO: Use the ViewModel
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    public ProgressBar pb;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SuggestionAdapter adapter = new SuggestionAdapter(new Suggestion[]{new Suggestion("Suggestion 1", "This is the first suggestionThis is the first suggestionThis is the first suggestionThis is the first suggestionThis is the first suggestion", "Click me"), new Suggestion("Suggestion 1", "This is the first suggestionThis is the first suggestionThis is the first suggestionThis is the first suggestionThis is the first suggestion", "Click me"), new Suggestion("Suggestion 1", "This is the first suggestionThis is the first suggestionThis is the first suggestionThis is the first suggestionThis is the first suggestion", "Click me"), new Suggestion("Suggestion 1", "This is the first suggestionThis is the first suggestionThis is the first suggestionThis is the first suggestionThis is the first suggestion", "Click me"), new Suggestion("Suggestion 1", "This is the first suggestionThis is the first suggestionThis is the first suggestionThis is the first suggestionThis is the first suggestion", "Click me"), new Suggestion("Suggestion 1", "This is the first suggestionThis is the first suggestionThis is the first suggestionThis is the first suggestionThis is the first suggestion", "Click me"),});
        RecyclerView recyclerView = (RecyclerView) getView().findViewById(R.id.suggestion_list);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        FloatingActionButton btnCamera = (FloatingActionButton) getView().findViewById(R.id.btnCamera);
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePicture(view);
            }
        });

        pb = (ProgressBar) getView().findViewById(R.id.progressBar);
        pb.setVisibility(View.GONE);
    }

    String currentPhotoPath;
    File photoFileGlobal;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
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
        if (takePictureIntent.resolveActivity(getContext().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getContext(), "com.example.android.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                photoFileGlobal = photoFile;

                pb.setVisibility(View.VISIBLE);
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
                Intent storyContent = new Intent(getActivity(), StoryContent.class);
                storyContent.putExtra("IMAGE_PATH", photoFileGlobal.getAbsolutePath());
                getContext().startActivity(storyContent);
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
//serverResponseCode = 200
            switch (serverResponseCode) {
                case 200:
                    Log.d("debug 8", "fuck");

                    //all went ok - read response
                    pb.setVisibility(View.INVISIBLE);
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

    public static class MyOnClickListener implements View.OnClickListener {

        private final Context context;

        private MyOnClickListener(Context context) {
            this.context = context;
        }

        @Override
        public void onClick(View v) {
            goToStoryListActivity(v);
        }

        private void goToStoryListActivity(View v) {

            Intent detailStoryIntent = new Intent(this.context, StoryDetail.class);
            this.context.startActivity(detailStoryIntent);
        }
    }

}
