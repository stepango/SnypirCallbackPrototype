package com.snypir.callback.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.snypir.callback.network.AuthRestClient;
import com.snypir.callback.network.PhoneNumbers;
import com.snypir.callback.network.ResponseTemplate;
import com.snypir.callback.preferences.Prefs_;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EIntentService;
import org.androidannotations.annotations.ServiceAction;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.springframework.web.client.RestClientException;

import java.util.ArrayList;

/**
 * Created by stepangoncarov on 05/07/14.
 */
@EIntentService
public class ContactDataUploaderService extends IntentService{

    private boolean isInitial;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("SERVICE", "CREATED");
    }

    @Bean
    AuthRestClient rest;

    @Pref
    Prefs_ preferences;

    public ContactDataUploaderService() {
        super(ContactDataUploaderService.class.getName());
    }

    @ServiceAction
    void addRangeSingle(String number){
        addRange(new PhoneNumbers(number));
    }

    @ServiceAction
    void addRange(ArrayList<String> numbers){
        addRange(new PhoneNumbers(numbers));
    }

    @ServiceAction
    void addRangeInitial(ArrayList<String> initialNumbers){
        isInitial = true;
        addRange(initialNumbers);
    }

    void addRange(PhoneNumbers phoneNumbers){
        try {
            Log.d("ADD RANGE", "Enter");
            ResponseTemplate responseTemplate = rest.client.addRange(phoneNumbers);
            if (!responseTemplate.isError() && isInitial){
                preferences.isInitialContactsUploaded().put(true);
            }
        } catch (RestClientException e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onHandleIntent(Intent intent) {
    }
}
