package com.snypir.callback;

import com.activeandroid.app.Application;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import org.androidannotations.annotations.EApplication;

/**
 * Created by stepangoncarov on 12/06/14.
 */
@EApplication
public class App extends Application {

    private Tracker tracker;

    public synchronized Tracker getTracker(){
        if (tracker != null) return tracker;
        GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
        tracker = analytics.newTracker(R.xml.app_tracker);
        return tracker;
    }
}
