package com.gocktrip.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.gocktrip.R;
import com.gocktrip.adapter.VideoListAdapter;
import com.gocktrip.manager.ApiManager;
import com.gocktrip.models.Video;
import com.gocktrip.utils.MyHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class VideoListActivity extends Activity {

    private ProgressDialog pDialog;
    private ListView listview;
    private VideoListAdapter adapter;
    private Context context;

    private String str_category;
    private String str_url;
    private ArrayList<Video> aryVideo;

    private EditText edit_search;
    private Button btn_search;
    private TextView screenTitle;
    private ImageView screenIcon;
    private ImageView btn_back;
    private View header;

    RelativeLayout actionbar;
    RelativeLayout searchbar;

    ArrayList<String> recommendedKeywords = new ArrayList<String>();
    ArrayAdapter<String> listAdapter;

    private View inflatelayout;
    private ListView listview_keywords;
    private RelativeLayout parentlayout;
    private RelativeLayout actionbarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videolist);

        context = getBaseContext();

        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= android.os.Build.VERSION_CODES.LOLLIPOP){
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.statusbarcolor));
        }

        Intent intent = getIntent();
        aryVideo = (ArrayList<Video>) intent.getSerializableExtra("videoArray");
        str_category = (String) intent.getSerializableExtra("category");

        LayoutInflater inflater = getLayoutInflater();
        header = inflater.inflate(R.layout.listheader_video, listview, false);

        screenTitle = (TextView) findViewById(R.id.lbl_title_videolist);
        screenTitle.setText(str_category);
        screenIcon = (ImageView) findViewById(R.id.icon_videolist);
        setScreenIcon(str_category);
        btn_back = (ImageView) findViewById(R.id.btn_back_videolist);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(VideoListActivity.this, TabsActivity.class);
                startActivity(i);
                finish();
            }
        });

        initUI(aryVideo);
    }

    private void loadDataFromserver(String keyword) {

        pDialog = new ProgressDialog(VideoListActivity.this);
        pDialog.setMessage("Loading Data");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();


        RequestParams params = new RequestParams();
        params.add("keyword", keyword);
        params.add("category", str_category);
        params.add("user_id", "251");

        str_url = getResources().getString(R.string.url_getvideobycategorywithkeyword);

        MyHttpClient.post(str_url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                aryVideo.clear();
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
                pDialog.dismiss();
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                super.onSuccess(statusCode, headers, responseString);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });

    }

    private void initUI(ArrayList<Video> aryVideo) {
        listview = (ListView) findViewById(R.id.listview_video);
        adapter = new VideoListAdapter(this, aryVideo);
        listview.addHeaderView(header, null, false);
        listview.setAdapter(adapter);

        listview.setRecyclerListener(new AbsListView.RecyclerListener() {
            @Override
            public void onMovedToScrapHeap(View view) {
                VideoView videoView = (VideoView) view.findViewById(R.id.videoview);
                if(videoView == null) return;

                MediaPlayer mp = null;
                if(videoView.getTag() != null) {
                    mp = (MediaPlayer) videoView.getTag();
                }

                if(mp != null) {
                    mp.pause();
                } else {
                    videoView.stopPlayback();
                    videoView.setTag(null);
                }
            }
        });
        listview.setOnScrollListener(new AbsListView.OnScrollListener() {

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
                     int last = Math.max(0, listview.getChildCount() - 1);

                     if(currentFirstVisibleItem == 0) {
                         currentFirstVisibleItem = 0;
                         currentVisibleItemCount -= 1;
                         first = 1;
                         last = Math.max(1, listview.getChildCount() - 1);
                     } else {
                         currentFirstVisibleItem -= 1;
                     }

                     int indexToPlay;
                     if(currentVisibleItemCount > 2) {
                         indexToPlay = currentFirstVisibleItem + currentVisibleItemCount / 2;
                     } else if (currentVisibleItemCount == 2) {

                         int firstHeight, firstTop, secondHeight, secondTop, listViewHeight;
                         int firstVisibleHeight, secondVisibleHeight;

                         firstTop = listview.getChildAt(first).getTop();
                         secondTop = listview.getChildAt(last).getTop();

                         firstHeight = listview.getChildAt(first).getMeasuredHeight();
                         secondHeight = listview.getChildAt(last).getMeasuredHeight();

                         listViewHeight = listview.getMeasuredHeight();

                         firstVisibleHeight = Math.min(listViewHeight, firstTop + firstHeight) - Math.max(firstTop, 0);
                         secondVisibleHeight = Math.min(listViewHeight, secondTop + secondHeight) - Math.max(secondTop, 0);

                         Resources r = getResources();
                         //height of item's top layout
                         float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, r.getDisplayMetrics());

                         int textsHeight1, textsHeight2 = (int)px;

                         textsHeight1 = firstHeight - listview.getChildAt(first).getMeasuredWidth() - (int)px;
                         int firstVideoVisibleHeight = firstVisibleHeight - textsHeight1;
                         int secondVideoVisibleHeight = secondVisibleHeight - textsHeight2;
                         indexToPlay = firstVideoVisibleHeight > secondVideoVisibleHeight ? currentFirstVisibleItem : currentFirstVisibleItem + 1;

                         Log.d("VideoList", String.format("First(%d) visible height %d, Second(%d) visible height %d)", first, firstVideoVisibleHeight, last, secondVideoVisibleHeight));
                     } else {
                         indexToPlay = currentFirstVisibleItem;
                     }

                     if(indexToPlay == adapter.getCurrentlyPlayingIndex()) return;
                     adapter.setCurrentlyPlayingIndex(indexToPlay);
                     videoPlaybackHandler.removeCallbacks(controlPlaybackRunnable);
                     videoPlaybackHandler.postDelayed(controlPlaybackRunnable, 1);
                 }
             }
        });

        edit_search = (EditText) findViewById(R.id.edittext_search_videolist);
        edit_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                int i = 0;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int i = 0;
            }

            @Override
            public void afterTextChanged(Editable s) {
                String keyword = edit_search.getText().toString();
                if (keyword.equals("")) {
                    recommendedKeywords.clear();
                    listAdapter.notifyDataSetChanged();
                }else {
                    ApiManager.getInstance(VideoListActivity.this).getRecommendedKeywordsWithCategory(str_category, keyword, new ApiManager.OnApiManagerListener(){
                        @Override
                        public void onFail(JSONObject fail) {
                            int i = 0;
                        }

                        @Override
                        public void onSucces(JSONObject result) {
                            int i = 0;
                        }

                        @Override
                        public void onSuccess(JSONArray resultArray) {
                            recommendedKeywords.clear();
                            for (int i = 0; i < resultArray.length(); i ++) {
                                try {
                                    String keyword = resultArray.getString(i);
                                    recommendedKeywords.add(keyword);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            listAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        });
        btn_search = (Button) findViewById(R.id.btn_search_videolist);
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String keyword = edit_search.getText().toString();
                if (keyword.length() == 0) {
                    Toast.makeText(VideoListActivity.this, "Please insert keyword", Toast.LENGTH_SHORT).show();
                } else {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(edit_search.getWindowToken(), 0);
                    loadDataFromserver(keyword);
                }

            }
        });

        showKeywordList();
    }

    private void showKeywordList() {
        parentlayout = (RelativeLayout) findViewById(R.id.videolist_layout);
        actionbarLayout = (RelativeLayout) findViewById(R.id.actionbar_listview);

        inflatelayout = getLayoutInflater().inflate(R.layout.inflate_keywordslist, null);

        listview_keywords = (ListView) inflatelayout.findViewById(R.id.listview_keywords);
        listview_keywords.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String keyword = recommendedKeywords.get(position);

                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(edit_search.getWindowToken(), 0);
                loadDataFromserver(keyword);

                edit_search.setText("");
            }
        });

        listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, recommendedKeywords);
        listview_keywords.setAdapter(listAdapter);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.BELOW, actionbarLayout.getId());

        Resources r = getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 65, r.getDisplayMetrics());
        params.setMargins(0, (int)px, 0, 0);

        inflatelayout.setLayoutParams(params);

        parentlayout.addView(inflatelayout);
    }

    Runnable controlPlaybackRunnable = new Runnable() {
        @Override
        public void run() {
            adapter.notifyDataSetChanged();
        }
    };
    private Handler videoPlaybackHandler = new Handler();

    private void setScreenIcon(String category) {
        if (category.equals("hotel")){
            screenIcon.setImageResource(R.drawable.hotel);
        }else if (category.equals("restaurant")) {
            screenIcon.setImageResource(R.drawable.restaurant);
        }else if (category.equals("activity")) {
            screenIcon.setImageResource(R.drawable.havetodo);
        }else {
//            listview.removeHeaderView(header);
        }
    }

    @Override
    public void onBackPressed() {

    }
}
