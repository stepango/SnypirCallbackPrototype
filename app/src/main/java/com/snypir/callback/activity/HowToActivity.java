package com.snypir.callback.activity;

import android.app.Fragment;
import android.os.Bundle;
import android.text.TextUtils;

import com.snypir.callback.R;
import com.snypir.callback.preferences.Prefs_;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.sharedpreferences.Pref;

/**
 * Created by stepangoncarov on 03/08/14.
 */
@EActivity(R.layout.ac_root)
public class HowToActivity extends BaseActivity{

    @Pref
    Prefs_ preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().hide();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isUserSignedIn()){
            finish();
        }
    }

    private boolean isUserSignedIn() {
        return !TextUtils.isEmpty(preferences.login().get()) &&
                !TextUtils.isDigitsOnly(preferences.password().get());
    }

    @Override
    public Fragment getBaseFragment() {
        return HowToFragment_.builder().build();
    }
}
