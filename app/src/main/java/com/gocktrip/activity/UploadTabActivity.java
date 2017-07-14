package com.gocktrip.activity;

import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.VideoView;

import com.gocktrip.R;

import java.io.File;

public class UploadTabActivity extends AppCompatActivity {

    VideoView uploadVideoView;
    Button playBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_tab);

        uploadVideoView = (VideoView)findViewById(R.id.uploadvideoview);
        playBtn = (Button)findViewById(R.id.btn_play);

        String videoAddress = "http://www.ebookfrenzy.com/android_book/movie.mp4";
        uploadVideoView.setVideoURI(Uri.parse(videoAddress));
        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("Hu", "play btn is clicked");
                uploadVideoView.start();
            }
        });
    }
}
