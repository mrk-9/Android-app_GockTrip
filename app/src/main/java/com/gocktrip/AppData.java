package com.gocktrip;

import com.gocktrip.models.User;

import java.util.Locale;

public class AppData {

    private static AppData instance;

    public User currentUser;
    public Locale currentLocale;

    public static AppData getInstance() {
        if (instance == null)
            instance = new AppData();
        return instance;
    }
}
