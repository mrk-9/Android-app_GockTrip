package com.gocktrip.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;

import com.gocktrip.AppData;
import com.gocktrip.R;
import com.gocktrip.fragment.SliderFirstFragment;
import com.gocktrip.fragment.SliderSecondFragment;
import com.gocktrip.fragment.SliderThirdFragment;
import com.gocktrip.models.User;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LogoActivity extends FragmentActivity {

    private ImageView btn_next;
    private ImageView btn_preview;

    private MyPagerAdapter myPagerAdapter;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logo);

        checkLanguage();

        checkLoggedin();
    }

    private void checkLanguage() {
        Locale currentLocale = Locale.getDefault();
        String languageCode = currentLocale.getLanguage();
        AppData appData = AppData.getInstance();
        appData.currentLocale = currentLocale;
    }

    @Override
    protected void onResume() {
        super.onResume();

        setComponent();
    }

    private void checkLoggedin() {

        User currentUser = User.loadFromDisk(LogoActivity.this);

        if (currentUser == null){
            setComponent();
        }else {
            AppData appData = AppData.getInstance();
            appData.currentUser = currentUser;

            Intent intent = new Intent(LogoActivity.this, TabsActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void setComponent() {

        btn_next = (ImageView) findViewById(R.id.btn_next_logo);
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int i = viewPager.getCurrentItem();
                if(i != 2)
                    viewPager.setCurrentItem(i + 1);
                else {
                    Intent intent = new Intent(LogoActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }

            }
        });
        btn_preview = (ImageView) findViewById(R.id.btn_preview_logo);
        btn_preview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int i = viewPager.getCurrentItem();
                if (i != 0)
                    viewPager.setCurrentItem(i - 1);
            }
        });

        List<Fragment> fragments = getFragments();

        myPagerAdapter = new MyPagerAdapter(getSupportFragmentManager(), fragments);
        viewPager = (ViewPager)findViewById(R.id.viewPager);
        viewPager.setAdapter(myPagerAdapter);

        CirclePageIndicator indicator = (CirclePageIndicator)findViewById(R.id.indicator);
        indicator.setViewPager(viewPager);

        final float density = getResources().getDisplayMetrics().density;
        indicator.setRadius(5 * density);
        indicator.setPageColor(0x00000000);
        indicator.setFillColor(0xFFFFFFFF);
        indicator.setStrokeColor(0xFFFFFFFF);
        indicator.setStrokeWidth(1);

    }

    private List<Fragment> getFragments() {

        List<Fragment> fList = new ArrayList<Fragment>();
        return fList;
    }

    private class MyPagerAdapter extends FragmentPagerAdapter {

        private List<Fragment> fragments;

        public MyPagerAdapter(FragmentManager fm, List<Fragment> fragments) {

            super(fm);
            this.fragments = fragments;
        }

        @Override
        public Fragment getItem(int position) {

            switch(position) {
                case 0:
                    return SliderFirstFragment.newInstance("FirstFragment");
                case 1:
                    return SliderSecondFragment.newInstance("SecondFragment");
                case 2:
                    return SliderThirdFragment.newInstance("ThirdFragment");
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {

            return 3;
        }
    }



}
