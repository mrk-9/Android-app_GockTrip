package com.gocktrip.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.gocktrip.AppData;
import com.gocktrip.R;
import com.gocktrip.adapter.ProfileVideoListAdapter;
import com.gocktrip.adapter.VideoListAdapter;
import com.gocktrip.manager.ApiManager;
import com.gocktrip.models.Video;
import com.gocktrip.utils.BitmapHelper;
import com.gocktrip.utils.MyHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageActivity;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;

import cz.msebera.android.httpclient.Header;
import de.ecotastic.android.camerautil.lib.CameraIntentHelper;
import de.ecotastic.android.camerautil.lib.CameraIntentHelperCallback;
import de.hdodenhof.circleimageview.CircleImageView;

public class TabProfileActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;

    private static final int CAMERA_REQUEST = 1888;
    final int CAMERA_CAPTURE = 100;
    final int CROP_IMAGE = 1;

    private CircleImageView img_avatar;
    private TextView txt_username;
    private TextView txt_email;
    private Button btn_edit;
    private ListView videoList;
    private ProfileVideoListAdapter listAdapter;
    private View header;
    private ProgressDialog progressDialog;

    private AppData appData;

    private CameraIntentHelper mCameraIntentHelper;

    ArrayList<Video> aryVideo = new ArrayList<Video>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_tab);

        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= android.os.Build.VERSION_CODES.LOLLIPOP){
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.statusbarcolor));
        }

        appData = AppData.getInstance();

        initUI();
        setupCameraIntentHelper();
        loadDataFromserver();
    }

    private void initUI() {

        sharedPreferences = getSharedPreferences("profile", Context.MODE_PRIVATE);
        videoList = (ListView) findViewById(R.id.videolist_profile);

        LayoutInflater inflater = getLayoutInflater();
        header = inflater.inflate(R.layout.listheader_profile, videoList, false);
        videoList.addHeaderView(header);

        setVideoList(aryVideo);

        img_avatar = (CircleImageView) findViewById(R.id.img_avatar_profile);
        if (sharedPreferences.contains("avatarPath") && sharedPreferences.getString("avatarPath", "").length() != 0){
            Bitmap avatar = BitmapHelper.readBitmap(TabProfileActivity.this, Uri.parse("file://" + sharedPreferences.getString("avatarPath", "")));
            avatar = BitmapHelper.shrinkBitmap(avatar, 300, 0);
            img_avatar.setImageBitmap(avatar);
        }else {
            img_avatar.setImageResource(R.drawable.noimage);
        }

        img_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCameraIntentHelper != null) {
                    mCameraIntentHelper.startCameraIntent();
//                    listAdapter.stopPlayingView();
                }
            }
        });

        txt_email = (TextView) findViewById(R.id.txt_email_profile);
        txt_email.setText(appData.currentUser.getEmail());
        txt_username = (TextView) findViewById(R.id.txt_username_profile);
        txt_username.setText(appData.currentUser.getFirstName() + " " + appData.currentUser.getLastName());
        btn_edit = (Button) findViewById(R.id.btn_edit_profile);
        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appData.currentUser.deleteFromDisk(TabProfileActivity.this);

                Intent intent = new Intent(TabProfileActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }


    private void setupCameraIntentHelper() {
        mCameraIntentHelper = new CameraIntentHelper(this, new CameraIntentHelperCallback() {
            @Override
            public void onPhotoUriFound(Date dateCameraIntentStarted, Uri photoUri, int rotateXDegrees) {
//                messageView.setText(getString(R.string.activity_camera_intent_photo_uri_found) + photoUri.toString());
//                CropImage.activity(photoUri)
//                        .setGuidelines(CropImageView.Guidelines.ON)
//                        .setCropShape(CropImageView.CropShape.OVAL)
//                        .start(TabProfileActivity.this);

                Intent intent = new Intent(TabProfileActivity.this, ImageCropActivity.class);
                intent.putExtra("photoUri", photoUri.toString());
                startActivityForResult(intent, CROP_IMAGE);
            }

            @Override
            public void deletePhotoWithUri(Uri photoUri) {
                BitmapHelper.deleteImageWithUriIfExists(photoUri, TabProfileActivity.this);
            }

            @Override
            public void onSdCardNotMounted() {
                Toast.makeText(getApplicationContext(), getString(R.string.error_sd_card_not_mounted), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCanceled() {
                Toast.makeText(getApplicationContext(), getString(R.string.warning_camera_intent_canceled), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCouldNotTakePhoto() {
                Toast.makeText(getApplicationContext(), getString(R.string.error_could_not_take_photo), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onPhotoUriNotFound() {
                Toast.makeText(getApplicationContext(), getString(R.string.activity_camera_intent_photo_uri_not_found), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void logException(Exception e) {
                Toast.makeText(getApplicationContext(), getString(R.string.error_sth_went_wrong), Toast.LENGTH_LONG).show();
                Log.d(getClass().getName(), e.getMessage());
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        mCameraIntentHelper.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mCameraIntentHelper.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (requestCode == CAMERA_CAPTURE) {
            mCameraIntentHelper.onActivityResult(requestCode, resultCode, intent);
        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(intent);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();

                Bitmap photo = BitmapHelper.readBitmap(TabProfileActivity.this, resultUri);
                if (photo != null) {
                    photo = BitmapHelper.shrinkBitmap(photo, 300, 0);
                    img_avatar.setImageBitmap(photo);

                    if (resultUri != null) {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("avatarPath", resultUri.getPath());
                        editor.commit();
                    }
                }

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        } else if (requestCode == CROP_IMAGE) {
            if (resultCode == RESULT_OK){

                Uri resultUri = Uri.parse(intent.getExtras().getString("croppedImageUri"));

                Bitmap photo = BitmapHelper.readBitmap(TabProfileActivity.this, resultUri);
                if (photo != null) {
                    photo = BitmapHelper.shrinkBitmap(photo, 300, 0);
                    img_avatar.setImageBitmap(photo);

                    if (resultUri != null) {
                        sendAvatarToServer(resultUri);

                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.clear();
                        editor.putString("avatarPath", resultUri.getPath());
                        editor.commit();


                    }
                }
            }else if (resultCode == RESULT_CANCELED){

            }
        }
    }

    private void sendAvatarToServer(Uri uri) {

        ApiManager.getInstance(TabProfileActivity.this).sendAvatar(251, uri, new ApiManager.OnApiManagerListener() {
                    @Override
                    public void onSucces(JSONObject result) {
                        int i = 0;
                    }

                    @Override
                    public void onSuccess(JSONArray resultArray) {
                        int i = 0;
                    }

                    @Override
                    public void onFail(JSONObject fail) {
                        int i = 0;
                    }
                }
        );

    }

    private void loadDataFromserver() {

        progressDialog = new ProgressDialog(TabProfileActivity.this);
        progressDialog.setMessage("Loading....");
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(true);
        progressDialog.show();

        RequestParams params = new RequestParams();

        String str_url = getResources().getString(R.string.url_getVideobyuser);

        params.add("user_id", "177");

        ApiManager.getInstance(this).getPostsByUser(177, new ApiManager.OnApiManagerListener() {
            @Override
            public void onSucces(JSONObject response) {

                try {
                    JSONArray result = response.getJSONArray("result");
                    for (int i = 0; i < result.length(); i++) {
                        JSONObject videoObjet = result.getJSONObject(i);
                        Video video = new Video(videoObjet);
                        aryVideo.add(video);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                progressDialog.dismiss();
                listAdapter.notifyDataSetChanged();
            }

            @Override
            public void onSuccess(JSONArray resultArray) {

            }

            @Override
            public void onFail(JSONObject fail) {
                String alertTitle;
                String alertMessage;

                alertTitle = "cannot connect server";
                alertMessage = "Please retry later";
                new AlertDialog.Builder(TabProfileActivity.this)
                        .setTitle(alertTitle)
                        .setMessage(alertMessage)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                progressDialog.dismiss();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });
    }

    private void setVideoList(ArrayList<Video> aryVideo) {
        listAdapter = new ProfileVideoListAdapter(this, aryVideo);
        videoList.setAdapter(listAdapter);

//        videoList.setRecyclerListener(new AbsListView.RecyclerListener() {
//            @Override
//            public void onMovedToScrapHeap(View view) {
//                VideoView videoView = (VideoView) view.findViewById(R.id.videoview);
//                if(videoView == null) return;
//
//                MediaPlayer mp = null;
//                if(videoView.getTag() != null) {
//                    mp = (MediaPlayer) videoView.getTag();
//                }
//
//                if(mp != null) {
//                    mp.pause();
//                } else {
//                    videoView.stopPlayback();
//                    videoView.setTag(null);
//                }
//            }
//        });

        videoList.setOnScrollListener(new AbsListView.OnScrollListener() {

            private int currentVisibleItemCount;
            private int currentScrollState;
            private int currentFirstVisibleItem;
            private int totalItem;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // TODO Auto-generated method stub
                this.currentScrollState = scrollState;
                try {
                    isScrollCompleted();
                } catch (Exception ex) {

                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                // TODO Auto-generated method stub
                this.currentFirstVisibleItem = firstVisibleItem;
                this.currentVisibleItemCount = visibleItemCount;
                this.totalItem = totalItemCount;

                try {
                    isScrollCompleted();
                } catch (Exception ex) {

                }
            }

            private void isScrollCompleted() throws NullPointerException {
                if (/*totalItem - currentFirstVisibleItem == currentVisibleItemCount
                         &&*/ this.currentScrollState == SCROLL_STATE_IDLE) {
                    /** To do code here*/

                    Log.d("VideoList", String.format("currentFirstVisibleItem - (%d), currentVisibleItemCount - (%d)", currentFirstVisibleItem, currentVisibleItemCount));
                    int first = 0;
                    int last = Math.max(0, videoList.getChildCount() - 1);

                    if(currentFirstVisibleItem == 0) {
                        currentFirstVisibleItem = 0;
                        currentVisibleItemCount -= 1;
                        first = 1;
                        last = Math.max(1, videoList.getChildCount() - 1);
                    } else {
                        currentFirstVisibleItem -= 1;
                    }

                    int indexToPlay;
                    if(currentVisibleItemCount > 2) {
                        indexToPlay = currentFirstVisibleItem + currentVisibleItemCount / 2;
                    } else if (currentVisibleItemCount == 2) {

                        int firstHeight, firstTop, secondHeight, secondTop, listViewHeight;
                        int firstVisibleHeight, secondVisibleHeight;

                        firstTop = videoList.getChildAt(first).getTop();
                        secondTop = videoList.getChildAt(last).getTop();

                        firstHeight = videoList.getChildAt(first).getMeasuredHeight();
                        secondHeight = videoList.getChildAt(last).getMeasuredHeight();

                        listViewHeight = videoList.getMeasuredHeight();

                        firstVisibleHeight = Math.min(listViewHeight, firstTop + firstHeight) - Math.max(firstTop, 0);
                        secondVisibleHeight = Math.min(listViewHeight, secondTop + secondHeight) - Math.max(secondTop, 0);

                        Resources r = getResources();
                        //height of item's top layout
                        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, r.getDisplayMetrics());

                        int textsHeight1, textsHeight2 = (int)px;

                        textsHeight1 = firstHeight - videoList.getChildAt(first).getMeasuredWidth() - (int)px;
                        int firstVideoVisibleHeight = firstVisibleHeight - textsHeight1;
                        int secondVideoVisibleHeight = secondVisibleHeight - textsHeight2;
                        indexToPlay = firstVideoVisibleHeight > secondVideoVisibleHeight ? currentFirstVisibleItem : currentFirstVisibleItem + 1;

                        Log.d("VideoList", String.format("First(%d) visible height %d, Second(%d) visible height %d)", first, firstVideoVisibleHeight, last, secondVideoVisibleHeight));
                    } else {
                        indexToPlay = currentFirstVisibleItem;
                    }

                    if(indexToPlay == listAdapter.getCurrentlyPlayingIndex()) return;
                    listAdapter.setCurrentlyPlayingIndex(indexToPlay);
                    videoPlaybackHandler.removeCallbacks(controlPlaybackRunnable);
                    videoPlaybackHandler.postDelayed(controlPlaybackRunnable, 1);
                }
            }
        });
    }

    Runnable controlPlaybackRunnable = new Runnable() {
        @Override
        public void run() {
            listAdapter.notifyDataSetChanged();
        }
    };
    private Handler videoPlaybackHandler = new Handler();
}
