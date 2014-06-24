package com.snypir.callback.service;

import android.app.IntentService;
import android.content.Intent;
import android.database.ContentObserver;
import android.provider.ContactsContract;

import com.snypir.callback.observers.ContactsObserver;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EIntentService;

/**
 * Created by stepangoncarov on 22/06/14.
 */
@EIntentService
public class ContactsRangeService extends IntentService {

    @Bean(ContactsObserver.class)
    ContentObserver observer;

    public ContactsRangeService() {
        super(ContactsRangeService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(final Intent intent) {

    }

    void registerContactsChangeHandler() {
        getContentResolver()
                .registerContentObserver(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        false, observer);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getContentResolver()
                .unregisterContentObserver(observer);
    }
}
