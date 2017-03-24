package com.sidali.popularmovies;

import android.app.Application;

import com.facebook.stetho.Stetho;

/**
 * Created by shallak on 21/03/2017.
 */

public class MyApplication extends Application {
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
    }
}
