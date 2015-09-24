package com.snypir.callback;

import android.database.ContentObserver;
import android.os.AsyncTask;
import android.provider.ContactsContract;

import com.activeandroid.app.Application;
import com.snypir.callback.observers.ContactsObserver;
import com.snypir.callback.preferences.Prefs_;
import com.snypir.callback.utils.ContactUtils;

import org.acra.ACRA;
import org.acra.ReportField;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;
import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EApplication;
import org.androidannotations.annotations.sharedpreferences.Pref;

/**
 * Created by stepangoncarov on 12/06/14.
 */
@EApplication
@ReportsCrashes(
        mailTo = "your@mail.here",
        mode = ReportingInteractionMode.TOAST,
        resToastText = R.string.crash_msg,
        customReportContent = {
                ReportField.APP_VERSION_CODE,
                ReportField.APP_VERSION_NAME,
                ReportField.ANDROID_VERSION,
                ReportField.PHONE_MODEL,
                ReportField.CUSTOM_DATA,
                ReportField.STACK_TRACE,
                ReportField.LOGCAT})
public class App extends Application {

    @Bean(ContactsObserver.class)
    ContentObserver observer;

    @Pref
    Prefs_ preferences;

    private Runnable dumpContactStates = new Runnable() {
        @Override
        public void run() {
            preferences.isContactsDumped().put(ContactUtils.dumpContactStates(App.this));
        }
    };

    @AfterInject
    void init() {
        if (!preferences.isContactsDumped().get()) {
            AsyncTask.SERIAL_EXECUTOR.execute(dumpContactStates);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ACRA.init(this);
        registerContactsChangeObserver();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        unregisterContactChangeReceiver();
    }

    @Background
    void registerContactsChangeObserver() {
        getContentResolver()
                .registerContentObserver(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        false, observer);
    }

    void unregisterContactChangeReceiver() {
        getContentResolver().unregisterContentObserver(observer);
    }
}
