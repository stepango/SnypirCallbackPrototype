package com.snypir.callback.network;

import com.snypir.callback.preferences.Prefs_;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.sharedpreferences.Pref;

/**
 * Created by stepangoncarov on 04/06/14.
 */
@EBean(scope = EBean.Scope.Singleton)
public class AuthStore {

    @Pref
    Prefs_ mPreferences;

    public String getUsername() {
        return mPreferences.login().get();
    }

    public String getPassword() {
        return mPreferences.password().get();
    }

}
