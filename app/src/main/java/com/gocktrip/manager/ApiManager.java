package com.gocktrip.manager;

import android.content.Context;
import android.graphics.Bitmap;
import android.location.GpsStatus;
import android.net.Uri;
import android.widget.Toast;

import com.gocktrip.R;
import com.gocktrip.utils.BitmapHelper;
import com.gocktrip.utils.MyHttpClient;
import com.loopj.android.http.Base64;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;

import cz.msebera.android.httpclient.Header;

public class ApiManager {

    public final static int REQUEST_SETTHANKS = 1;
    public final static int REQUEST_REPORT = 2;
    public final static int REQUEST_ADDVIEWCOUNT = 3;

    private static ApiManager instance;
    private Context mContext;

    public static ApiManager getInstance(Context context) {
        if (instance == null)
            instance = new ApiManager(context);

        return instance;
    }

    public ApiManager(Context context) {
        mContext = context;
    }

    public void login(String email, String pwd, final OnApiManagerListener listener) {
        RequestParams params = new RequestParams();

        params.add("mail", email);
        params.add("password", pwd);

        String str_url = mContext.getResources().getString(R.string.url_login);

        MyHttpClient.post(str_url, params, new JsonHttpResponseHandler(){

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                if (listener != null);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

                if (listener != null)
                    listener.onFail(errorResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

                if (listener != null);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                if (listener != null)
                    listener.onSucces(response);
            }
        });
    }

    public void signup(String prenom, String nom, String mail, String pwd, final OnApiManagerListener listener) {
        RequestParams params = new RequestParams();

        params.put("prenom", prenom);
        params.put("nom", nom);
        params.add("mail", mail);
        params.add("password", pwd);

        String str_url = mContext.getResources().getString(R.string.url_signup);

        MyHttpClient.post(str_url, params, new JsonHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                if (listener != null);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

                if (listener != null)
                    listener.onFail(errorResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

                if (listener != null);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                if (listener != null)
                    listener.onSucces(response);
            }
        });
    }

    public void sendAvatar(int userId, Uri uriAvatar, final OnApiManagerListener listener) {
        Bitmap photo = BitmapHelper.readBitmap(mContext, uriAvatar);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        if (photo != null){
            photo.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream .toByteArray();

            String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);

            RequestParams params = new RequestParams();
            params.put("user_id", String.valueOf(userId));
            params.put("avatar", encoded);

            String str_url = mContext.getResources().getString(R.string.url_update);

            MyHttpClient.post(str_url, params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);

                    if (listener != null)
                        listener.onSucces(response);
                }
            });
        }

    }

    public void sendReport(int videoId, String comment, final OnApiManagerListener listener) {

        RequestParams params = new RequestParams();

        params.add("post_id", String.valueOf(videoId));
        params.add("user_id", "251");
        params.add("comment_text", comment);

        String str_url = mContext.getResources().getString(R.string.url_reportAbuse);

        MyHttpClient.post(str_url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                if (listener != null)
                    listener.onSucces(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

                if (listener != null);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);

                if (listener != null)
                    listener.onFail(null);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

                if(listener != null) {
                    listener.onFail(errorResponse);
                }
            }
        });
    }

    public void setThanks(int videoId, final OnApiManagerListener listener){

        RequestParams params = new RequestParams();
        params.add("post_id", String.valueOf(videoId));
        params.add("user_id", "251");

        String str_url = mContext.getResources().getString(R.string.url_setthanks);

        MyHttpClient.post(str_url, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                try {
                    String str_response = response.getString("result");
                    if (str_response.equals("Thank added successfuly")) {
                        if (listener != null)
                            listener.onSucces(null);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

                if (listener != null)
                    listener.onFail(errorResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

                if (listener != null);
            }
        });
    }


    public void addViewCount(int videoId, final OnApiManagerListener listener){

        RequestParams params = new RequestParams();
        params.add("post_id", String.valueOf(videoId));

        String str_url = mContext.getResources().getString(R.string.url_addview);

        MyHttpClient.post(str_url, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                if (listener != null) {
                    listener.onSucces(response);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

                if (listener != null)
                    listener.onFail(errorResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

                if (listener != null);
            }
        });
    }

    public void getPostsByUser(int userId, final OnApiManagerListener listener) {
        RequestParams params = new RequestParams();
        params.add("user_id", "177");

        String str_url = mContext.getResources().getString(R.string.url_getVideobyuser);

        MyHttpClient.post(str_url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                if (listener != null)
                    listener.onSucces(response);
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

                if (listener != null)
                    listener.onFail(errorResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }

    public void getRecommendedKeywords(String keyword, final OnApiManagerListener listener){
        RequestParams params = new RequestParams();
        params.add("keyword", keyword);

        String str_url = mContext.getResources().getString(R.string.url_getrecommendedKeywords);
        MyHttpClient.post(str_url, params, new JsonHttpResponseHandler(){
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

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                if (listener != null)
                    listener.onSuccess(response);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                super.onSuccess(statusCode, headers, responseString);
            }
        });
    }

    public void getRecommendedKeywordsWithCategory(String category, String keyword, final OnApiManagerListener listener){
        RequestParams params = new RequestParams();
        params.add("keyword", keyword);
        params.add("category", category);

        String str_url = mContext.getResources().getString(R.string.url_getrecommendedKeywordsbycategory);
        MyHttpClient.post(str_url, params, new JsonHttpResponseHandler(){
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

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                if (listener != null)
                    listener.onSuccess(response);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                super.onSuccess(statusCode, headers, responseString);
            }
        });
    }

    public interface OnApiManagerListener {
        void onSucces(JSONObject result);
        void onSuccess(JSONArray resultArray);
        void onFail(JSONObject fail);
    }
}
