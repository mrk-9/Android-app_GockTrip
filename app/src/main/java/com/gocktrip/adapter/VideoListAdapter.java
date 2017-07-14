package com.gocktrip.adapter;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.gocktrip.R;
import com.gocktrip.activity.VideoListActivity;
import com.gocktrip.manager.ApiManager;
import com.gocktrip.models.Video;
import com.gocktrip.utils.MyHttpClient;
import com.koushikdutta.urlimageviewhelper.UrlImageViewCallback;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import cz.msebera.android.httpclient.Header;

public class VideoListAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private ArrayList<Video> videoArrayList;
    private Context context;
    private int currentlyPlayingIndex = 0;
    private MediaPlayer currentPlayingPlayer;

    private Handler addViewHandler = new Handler();
    private Handler disAppearThumbHandler = new Handler();
    private boolean isShowThumb = true;

    public void setCurrentlyPlayingIndex(int currentlyPlayingIndex) {
        this.currentlyPlayingIndex = currentlyPlayingIndex;
    }

    public int getCurrentlyPlayingIndex() {
        return currentlyPlayingIndex;
    }

    public void stopPlayingView() {
        if (currentPlayingPlayer != null)
            currentPlayingPlayer.stop();
    }

    private class ViewHolder {
        ImageView img_avatar;
        TextView lbl_username;
        TextView lbl_date;
        TextView lbl_viewcount;
        VideoView videoView;
        ImageView img_videothumb;
        TextView lbl_hotelname;
        TextView lbl_cityname;
        TextView lbl_videotitle;
        TextView lbl_description;
        TextView lbl_likenumber;
        ImageView btn_thanks;
        ImageView btn_report;
        ImageView btn_facebook;
        ProgressBar progressBar;
    }

    public VideoListAdapter(Context context, ArrayList<Video> objects) {
        this.context = context;
        this.videoArrayList = objects;
        mInflater = LayoutInflater.from(context);
    }

    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return videoArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return videoArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.listitem_video, null);
            holder.img_avatar = (ImageView) convertView.findViewById(R.id.img_avatar);
            holder.lbl_username = (TextView) convertView.findViewById(R.id.lbl_username);
            holder.lbl_date = (TextView) convertView.findViewById(R.id.lbl_date);
            holder.lbl_viewcount = (TextView) convertView.findViewById(R.id.lbl_viewcount);
            holder.videoView = (VideoView) convertView.findViewById(R.id.videoview);
            holder.progressBar = (ProgressBar) convertView.findViewById(R.id.progressbar);
            holder.img_videothumb = (ImageView) convertView.findViewById(R.id.video_thumb);
            holder.lbl_hotelname = (TextView) convertView.findViewById(R.id.lbl_hotelname);
            holder.lbl_cityname = (TextView) convertView.findViewById(R.id.lbl_cityname);
            holder.lbl_videotitle = (TextView) convertView.findViewById(R.id.lbl_videotitle);
            holder.lbl_description = (TextView) convertView.findViewById(R.id.lbl_description);
            holder.lbl_likenumber = (TextView) convertView.findViewById(R.id.videolikenumber);
            holder.btn_thanks = (ImageView) convertView.findViewById(R.id.videolikebtn);
            holder.btn_report = (ImageView) convertView.findViewById(R.id.videoreportteam);
            holder.btn_facebook = (ImageView) convertView.findViewById(R.id.videofacebookshare);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.lbl_username.setText(videoArrayList.get(position).getFirstname() + videoArrayList.get(position).getLastname());
        holder.lbl_date.setText(getDiffereceDate(videoArrayList.get(position).getCreation_date()));
        holder.lbl_viewcount.setText(videoArrayList.get(position).getViews());
        UrlImageViewHelper.setUrlDrawable(holder.img_videothumb, "http://travel.gockell.com/" + videoArrayList.get(position).getThumbnail_url(), R.drawable.noimage);
        if (isShowThumb) holder.img_videothumb.setVisibility(View.VISIBLE);
        else holder.img_videothumb.setVisibility(View.INVISIBLE);
        UrlImageViewHelper.setUrlDrawable(holder.img_avatar, "http://travel.gockell.com/" + videoArrayList.get(position).getAvatar(), R.drawable.noimage);
        holder.lbl_description.setText(videoArrayList.get(position).getDescription());
        holder.lbl_cityname.setText(videoArrayList.get(position).getCity());
        holder.lbl_hotelname.setText(videoArrayList.get(position).getEstablishment_name());
        holder.lbl_videotitle.setText(videoArrayList.get(position).getTitle());
        holder.lbl_likenumber.setText(String.valueOf(videoArrayList.get(position).getThanksCount()));
        holder.btn_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                alertDialog.setTitle("Report a Problem");
//                alertDialog.setMessage("Please Enter Comment");

                final EditText input = new EditText(context);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                input.setLayoutParams(lp);
                input.setHint("Enter you message here...");
                alertDialog.setView(input);

                alertDialog.setPositiveButton("YES",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                String comment = input.getText().toString();
                                if (comment.equals("")) {
                                    Toast.makeText(context, "Please insert the comment", Toast.LENGTH_SHORT).show();
                                } else {
                                    ApiManager.getInstance(context).sendReport(videoArrayList.get(position).getId(), comment, new ApiManager.OnApiManagerListener() {
                                        @Override
                                        public void onSucces(JSONObject result) {
                                            Toast.makeText(context, "Sent comment successfully!", Toast.LENGTH_SHORT).show();
                                        }

                                        @Override
                                        public void onSuccess(JSONArray resultArray) {

                                        }

                                        @Override
                                        public void onFail(JSONObject fail) {

                                        }
                                    });
                                }
                            }
                        });

                alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                alertDialog.show();
            }

        });

        if (videoArrayList.get(position).canThank == 1) holder.btn_thanks.setImageResource(R.drawable.non_like);
        else holder.btn_thanks.setImageResource(R.drawable.like);

        holder.btn_thanks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (videoArrayList.get(position).getCanThank() == 0) {
                    Toast.makeText(context, "You have already like this video", Toast.LENGTH_SHORT).show();
                } else {
                    ApiManager.getInstance(context).setThanks(videoArrayList.get(position).getId(), new ApiManager.OnApiManagerListener() {

                        @Override
                        public void onSucces(JSONObject result) {
                            Toast.makeText(context, "You had set this video like", Toast.LENGTH_SHORT).show();
                            videoArrayList.get(position).thanksCount += 1;
                            videoArrayList.get(position).canThank = 0;
                            notifyDataSetChanged();
                        }

                        @Override
                        public void onSuccess(JSONArray resultArray) {

                        }

                        @Override
                        public void onFail(JSONObject fail) {

                        }
                    });
                }
            }
        });

        if(currentlyPlayingIndex == position) {
            MediaPlayer mp = null;
            if(holder.videoView.getTag() != null) {
                mp = (MediaPlayer) holder.videoView.getTag();
            }
            if(mp != null) {
                holder.videoView.requestFocus();
                mp.start();
                holder.img_videothumb.setVisibility(View.INVISIBLE);
                isShowThumb = false;
                holder.progressBar.setVisibility(View.INVISIBLE);

                Log.d("VideoAdapter", "Resume playing " + position);

            }else {

                String url = "http://travel.gockell.com" + videoArrayList.get(position).getVideo_url();

                holder.videoView.stopPlayback();
                holder.videoView.setVideoURI(Uri.parse(url));
                holder.videoView.requestFocus();
                holder.videoView.start();
                holder.videoView.setTag(null);
                holder.img_videothumb.setVisibility(View.VISIBLE);
                isShowThumb = true;
                holder.videoView.setBackground(null);
                holder.progressBar.setVisibility(View.VISIBLE);
                Log.d("VideoAdapter", "Start playing " + position + " - " + url);
            }

            holder.videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    holder.videoView.setTag(mp);
                    mp.setLooping(true);
                    mp.start();
                    currentPlayingPlayer = mp;
//                    holder.img_videothumb.setVisibility(View.INVISIBLE);
                    isShowThumb = true;
                    holder.progressBar.setVisibility(View.INVISIBLE);
                    disAppearThumbHandler.postDelayed(disappearThumbRunnable, 300);
                    addViewHandler.postDelayed(addViewRunnable, 3000);
                    Log.d("VideoAdapter", "Video Prepared " + position);
                }
            });

        } else {
            String url = "http://travel.gockell.com/" + videoArrayList.get(position).getVideo_url();

            holder.videoView.stopPlayback();
            holder.videoView.setTag(null);
            holder.img_videothumb.setVisibility(View.VISIBLE);
            isShowThumb = true;
            holder.progressBar.setVisibility(View.VISIBLE);
            addViewHandler.removeCallbacks(addViewRunnable);
            Log.d("VideoAdapter", "Stop playing " + position + " - " + url);
        }

        return convertView;
    }

    Runnable disappearThumbRunnable = new Runnable() {
        @Override
        public void run() {
            isShowThumb = false;
            notifyDataSetChanged();
        }
    };

    Runnable addViewRunnable = new Runnable() {
        @Override
        public void run() {
            ApiManager.getInstance(context).addViewCount(videoArrayList.get(currentlyPlayingIndex).id, new ApiManager.OnApiManagerListener() {
                @Override
                public void onSucces(JSONObject result) {
                    Toast.makeText(context, "You viewed this video!", Toast.LENGTH_SHORT).show();
                    String viewCounts = null;
                    try {
                        viewCounts = result.getString("views");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    videoArrayList.get(currentlyPlayingIndex).views = viewCounts;
                    notifyDataSetChanged();
                }

                @Override
                public void onSuccess(JSONArray resultArray) {

                }

                @Override
                public void onFail(JSONObject fail) {
                    int i =  0;
                }
            });
        }
    };

    @Override
    public int getViewTypeCount() {
        return 5;
    }

    @Override
    public int getItemViewType(int position) {
        return position % 5;
    }

    private String getDiffereceDate(String strDate){

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date postedDate = null;

        try {
            postedDate = formatter.parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar thatDay = Calendar.getInstance();
        thatDay.setTime(postedDate);

        Calendar today = Calendar.getInstance();

        String differeceDate;

        long diff = today.getTimeInMillis() - thatDay.getTimeInMillis();
        if (diff / (60 * 1000) < 60) {
            differeceDate = String.valueOf(diff / (60 * 1000)) + " minutes ago";
        }else if (diff / (60 * 60 * 1000) <  24){
            differeceDate = String.valueOf(diff / (60 * 60 * 1000)) + " hours ago";
        }else if (diff / (24 * 60 * 60 * 1000) < 30) {
            differeceDate = String.valueOf(diff / (24 * 60 * 60 * 1000)) + " days ago";
        }else {
            differeceDate = String.valueOf(diff / (30 * 24 * 60 * 60 * 1000)) + " months ago";
        }
        return differeceDate;
    }
}







