package com.gocktrip.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.gocktrip.R;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageCropActivity extends Activity{
    private TextView btn_crop;
    private TextView btn_cancel;
    private CropImageView cropImageView;
    private Uri photoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_imagecrop);

        Intent intent = getIntent();
        photoUri = Uri.parse(intent.getStringExtra("photoUri"));

        cropImageView = (CropImageView) findViewById(R.id.cropImageView);
        cropImageView.setImageUriAsync(photoUri);
        cropImageView.setAutoZoomEnabled(true);
        cropImageView.setFixedAspectRatio(true);
        cropImageView.setAspectRatio(1, 1);
        cropImageView.setOnSaveCroppedImageCompleteListener(new CropImageView.OnSaveCroppedImageCompleteListener() {
            @Override
            public void onSaveCroppedImageComplete(CropImageView view, Uri uri, Exception error) {


            }
        });

        cropImageView.setOnGetCroppedImageCompleteListener(new CropImageView.OnGetCroppedImageCompleteListener() {
            @Override
            public void onGetCroppedImageComplete(CropImageView cropImageView, Bitmap bitmap, Exception e) {

            }
        });

        btn_crop = (TextView) findViewById(R.id.btn_crop);
        btn_crop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                cropImageView.saveCroppedImageAsync(photoUri);
//                cropImageView.getCroppedImageAsync();

                Bitmap bitmap = cropImageView.getCroppedImage();

                File file = new File(photoUri.getPath());
                file.delete();
                FileOutputStream fOut = null;
                try {
                    fOut = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 85, fOut);
                    fOut.flush();
                    fOut.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Intent i = new Intent();
                i.putExtra("croppedImageUri", photoUri.toString());
                setResult(RESULT_OK, i);
                finish();
            }
        });
        btn_cancel = (TextView) findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageCropActivity.this.finish();
            }
        });
    }

    public void onBackPressed() {
        super.onBackPressed();
    }
}
