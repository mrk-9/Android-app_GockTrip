package com.gocktrip.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.gocktrip.AppData;
import com.gocktrip.R;

public class SliderThirdFragment extends Fragment {

    public static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";

    private ImageView background;

    public static final SliderThirdFragment newInstance(String message) {

        SliderThirdFragment thirdFragment = new SliderThirdFragment();

        Bundle bdl = new Bundle(1);

        bdl.putString(EXTRA_MESSAGE, message);

        thirdFragment.setArguments(bdl);

        return thirdFragment;

    }

    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        String message = getArguments().getString(EXTRA_MESSAGE);

        View v = inflater.inflate(R.layout.fragment_slider3, container, false);

        background = (ImageView) v.findViewById(R.id.background_slider3);
        if (AppData.getInstance().currentLocale.getLanguage().equals("en"))
            background.setBackgroundResource(R.drawable.sliderimg_en3);
        else if (AppData.getInstance().currentLocale.getLanguage().equals("fr"))
            background.setBackgroundResource(R.drawable.sliderimg_fr3);

        return v;

    }

}
