package com.snypir.callback.observers;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;

import com.snypir.callback.model.ContactState;
import com.snypir.callback.network.AuthRestClient;
import com.snypir.callback.preferences.Prefs_;
import com.snypir.callback.service.ContactDataUploaderService_;
import com.snypir.callback.utils.ContactUtils;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by stepangoncarov on 22/06/14.
 */
@EBean
public class ContactsObserver extends ContentObserver {

    @Bean
    AuthRestClient rest;

    @RootContext
    Context context;

    private HashMap<Long, Integer> contactVersions = new HashMap<>();

    @Pref
    Prefs_ preferences;
    private Runnable updateProcessor = new Runnable() {
        @Override
        public void run() {
            Cursor c = ContactUtils.getRawContactsVersions(context);
            if (c == null) {
                return;
            }
            if (c.moveToFirst()){
                while (!c.isAfterLast()){
                    final long contactId =
                            c.getLong(ContactUtils.RAW_CONTACTS_VERSIONS_CONTACT_ID_COLUMN_INDEX);
                    final int contactVersion =
                            c.getInt(ContactUtils.RAW_CONTACTS_VERSIONS_VERSION_COLUMN_INDEX);
                    final Integer lastContactVersion = contactVersions.get(contactId);
                    if (lastContactVersion == null){
                        sendUpdatedContactData(contactId);
                    } else {
                        if (lastContactVersion > contactVersion) {
                            sendUpdatedContactData(contactId);
                        }
                    }
                }
                c.moveToNext();
            }
            c.close();
        }
    };

    private void sendUpdatedContactData(long contactId) {
        ArrayList<String> numbers = ContactUtils.getNumbers(context, contactId);
        ContactDataUploaderService_.intent(context).addRange(numbers).start();
    }

    private Runnable saveContactsInfo = new Runnable() {
        @Override
        public void run() {
            List<ContactState> all = ContactState.getAll();
            for (ContactState contactState : all) {
                contactVersions.put(contactState.getContactId(), contactState.getDataVersion());
            }
        }
    };

    public ContactsObserver() {
        super(new Handler(Looper.myLooper()));
        AsyncTask.SERIAL_EXECUTOR.execute(saveContactsInfo);
    }

    @Override
    public void onChange(final boolean selfChange) {
        super.onChange(selfChange);
        if (preferences.isContactsDumped().get()) {
            AsyncTask.SERIAL_EXECUTOR.execute(updateProcessor);
        }
    }
}
