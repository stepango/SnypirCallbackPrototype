package com.snypir.callback.fragment;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.NonNull;

import com.snypir.callback.R;
import com.snypir.callback.activity.PurchaseActivity_;
import com.snypir.callback.preferences.Prefs_;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.sharedpreferences.Pref;

/**
 * Created by stepangoncarov on 14/06/14.
 */
@EFragment
public class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {

    public static final String LOGOUT = "logout";
    public static final String BALANCE = "balance";
    @Pref
    Prefs_ preferences;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
        getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initLogout();
        initBalance();
    }

    private void initBalance() {
        Preference balance = findPreference(BALANCE);
        balance.setOnPreferenceClickListener(this);
    }

    private void initLogout() {
        findPreference(LOGOUT).setOnPreferenceClickListener(this);
    }

    @Override
    public boolean onPreferenceClick(@NonNull final Preference preference) {
        final String key = preference.getKey();
        if (LOGOUT.equals(key)) {
            preferences.edit().clear().apply();
            if (getActivity() != null) {
                getActivity().finish();
            }
        } else if (BALANCE.equals(key)){
            PurchaseActivity_.intent(this).start();
        }
        return false;
    }
}
