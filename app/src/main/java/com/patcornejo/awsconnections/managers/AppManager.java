package com.patcornejo.awsconnections.managers;

import android.app.Application;
import android.content.Context;

public class AppManager extends Application {

    private static AppManager instance;
    private Context context;

    public static AppManager getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        instance = this;
    }

    public Context getContext() {
        return context;
    }
}