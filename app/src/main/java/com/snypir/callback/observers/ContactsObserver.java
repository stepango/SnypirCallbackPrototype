package com.snypir.callback.observers;

import android.database.ContentObserver;
import android.os.Handler;
import android.os.Looper;

import com.snypir.callback.network.AuthRestClient;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;

/**
 * Created by stepangoncarov on 22/06/14.
 */
@EBean
public class ContactsObserver extends ContentObserver {

    @Bean
    AuthRestClient rest;

    public ContactsObserver() {
        super(new Handler(Looper.myLooper()));
    }

    @Override
    public void onChange(final boolean selfChange) {
        super.onChange(selfChange);
    }
}
