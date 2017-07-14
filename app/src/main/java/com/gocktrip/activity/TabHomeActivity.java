package com.gocktrip.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.gocktrip.R;
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

public class TabHomeActivity extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout btn_hotels;
    private LinearLayout btn_resturants;
    private LinearLayout btn_active;
    private LinearLayout btn_destination;
    private LinearLayout btn_search;
    private EditText edit_search;
    private ListView listview_keywords;
    private RelativeLayout parentlayout;
    private RelativeLayout searchbarlayout;
    private View inflatelayout;


    private ProgressDialog pDialog;
    private String str_url;

    ArrayList<String> recommendedKeywords = new ArrayList<String>();
    ArrayAdapter<String> listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_tab);

        initUI();
    }

    private void initUI() {

        pDialog = new ProgressDialog(TabHomeActivity.this);
        pDialog.setMessage("Loading Data");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);

        parentlayout = (RelativeLayout) findViewById(R.id.home_tab_layout);
        searchbarlayout = (RelativeLayout) findViewById(R.id.searchbar_back);

        btn_hotels = (LinearLayout) findViewById(R.id.btn_hotels);
        btn_hotels.setOnClickListener(this);
        btn_resturants = (LinearLayout) findViewById(R.id.btn_resturants);
        btn_resturants.setOnClickListener(this);
        btn_active = (LinearLayout) findViewById(R.id.btn_active);
        btn_active.setOnClickListener(this);
        btn_destination = (LinearLayout) findViewById(R.id.btn_destination);
        btn_destination.setOnClickListener(this);
        btn_search = (LinearLayout) findViewById(R.id.btn_search_home);
        btn_search.setOnClickListener(this);
        edit_search = (EditText) findViewById(R.id.edittext_search);
        edit_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String keyword = edit_search.getText().toString();
                if (keyword.equals("")) {
                    recommendedKeywords.clear();
                    listAdapter.notifyDataSetChanged();
                }else {
                    ApiManager.getInstance(TabHomeActivity.this).getRecommendedKeywords(keyword, new ApiManager.OnApiManagerListener(){
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

        showKeywordList();

    }

    private void showKeywordList() {

        inflatelayout = getLayoutInflater().inflate(R.layout.inflate_keywordslist, null);

        listview_keywords = (ListView) inflatelayout.findViewById(R.id.listview_keywords);
        listview_keywords.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String keyword = recommendedKeywords.get(position);

                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(edit_search.getWindowToken(), 0);
                pDialog.show();
                edit_search.setText(keyword);
                loadDataFromserver(keyword, true);
            }
        });

        listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, recommendedKeywords);
        listview_keywords.setAdapter(listAdapter);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.BELOW, searchbarlayout.getId());
        inflatelayout.setLayoutParams(params);

        parentlayout.addView(inflatelayout);
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.btn_hotels) {
            loadDataFromserver("hotel", false);
        }else if (v.getId() == R.id.btn_resturants) {
            loadDataFromserver("restaurant", false);
        }else if (v.getId() == R.id.btn_active) {
            loadDataFromserver("activity", false);
        }else if (v.getId() == R.id.btn_destination) {
//            this.str_category = "destination";
        }else if (v.getId() == R.id.btn_search_home) {
            String keyword = edit_search.getText().toString();
            if (keyword.equals("")){
                Toast.makeText(TabHomeActivity.this , "Please insert keyword", Toast.LENGTH_SHORT).show();
            }else {

                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(edit_search.getWindowToken(), 0);
                loadDataFromserver(keyword, true);
            }
        }

        pDialog.show();

    }

    private void showVideoList(ArrayList<Video> videoArray, String category) {

        Intent intent = new Intent(TabHomeActivity.this, VideoListActivity.class);
        intent.putExtra("videoArray", videoArray);
        intent.putExtra("category", category);
        startActivity(intent);
        finish();
    }

    private void loadDataFromserver(final String str_param, final boolean isSearch) {

        RequestParams params = new RequestParams();

        if (isSearch){
            str_url = getResources().getString(R.string.url_getvideobykeyword);
            params.add("keyword", str_param);
        }else {
            str_url = getResources().getString(R.string.url_getvideobycategory);
            params.add("category", str_param);
        }

        params.add("user_id", "251");

        MyHttpClient.post(str_url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                ArrayList<Video> aryVideo = new ArrayList<Video>();

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
                showVideoList(aryVideo, str_param);
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

                pDialog.dismiss();

                String alertTitle;
                String alertMessage;

                if (isSearch){
                    alertTitle = "cannot connect server";
                    alertMessage = "Please retry later";
                }else {
                    alertTitle = "failed to search";
                    alertMessage = "please reenter the keyword and retry";
                }
                new AlertDialog.Builder(TabHomeActivity.this)
                        .setTitle(alertTitle)
                        .setMessage(alertMessage)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                edit_search.setText("");
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
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
}
