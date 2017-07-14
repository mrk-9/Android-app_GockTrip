package com.gocktrip.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import com.gocktrip.R;

public class TabsActivity extends android.app.TabActivity {

    private static TabsActivity instance = null;
    ImageView topTabIcon;
    TextView topTabText;
    TabHost tabHost;

    public static TabsActivity getInstance () {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabs);

        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= android.os.Build.VERSION_CODES.LOLLIPOP){
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.statusbarcolor));
        }

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[] {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }


        topTabIcon = (ImageView)findViewById(R.id.topTabIcon);
        topTabText = (TextView)findViewById(R.id.topTabText);

        final Resources resources = getResources();
        tabHost = getTabHost();

        //home tab
        Intent intentHome = new Intent().setClass(this, TabHomeActivity.class);
        TabSpec tabSpecHome = tabHost
                .newTabSpec("Home")
                .setContent(intentHome);
        setIndicator(this, tabSpecHome, R.id.tabIcon, R.drawable.homeicon);

        //upload tab
        Intent intentUpload = new Intent().setClass(this, UploadTabActivity.class);
        TabSpec tabSpecUpload = tabHost
                .newTabSpec("Upload")
                .setContent(intentUpload);
        setIndicator(this, tabSpecUpload, R.id.tabIcon, R.drawable.videoicon);

        //profile tab
        Intent intentProfile = new Intent().setClass(this, TabProfileActivity.class);
        TabSpec tabSpecProfile = tabHost
                .newTabSpec("Profile")
                .setContent(intentProfile);

        setIndicator(this, tabSpecProfile, R.id.tabIcon, R.drawable.usericon);

        Intent intentVideo = new Intent().setClass(this, TabVideoActivity.class);
        TabSpec tabSpecVideo = tabHost
                .newTabSpec("Video")
                .setContent(intentVideo);

        setIndicator(this, tabSpecVideo, R.id.tabIcon, R.drawable.videoicon);

        //add all tabs
        tabHost.addTab(tabSpecHome);
        //tabHost.addTab(tabSpecUpload);
        tabHost.addTab(tabSpecVideo);
        tabHost.addTab(tabSpecProfile);
        //set Windows tab as default (zero based)
        tabHost.setCurrentTab(0);

        for (int i = 0; i<3; i++) {
            tabHost.getTabWidget().getChildAt(i).setBackgroundResource(R.drawable.tab_selector);
        }

        topTabIcon.setImageResource(R.drawable.homeicon);
        topTabText.setText(resources.getText(R.string.str_tab_home));
        instance = this;

        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                if (tabId.equals("Home")){
                    topTabIcon.setImageResource(R.drawable.homeicon);
                    topTabText.setText(resources.getText(R.string.str_tab_home));
                }else if (tabId.equals("Video")){
                    topTabIcon.setImageResource(R.drawable.videoicon);
                    topTabText.setText(resources.getText(R.string.str_tab_video));
                }else if (tabId.equals("Profile")){
                    topTabIcon.setImageResource(R.drawable.usericon);
                    topTabText.setText(resources.getText(R.string.str_tab_profile));
                }
            }
        });
    }

    public void setIndicator (Context ctx, TabSpec spec, int viewId, int resId){
        View view = LayoutInflater.from(ctx).inflate(R.layout.tab_indicator, null);
        ImageView imgTab = (ImageView) view.findViewById(viewId);
        imgTab.setImageDrawable(getResources().getDrawable(resId));
        spec.setIndicator(view);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
