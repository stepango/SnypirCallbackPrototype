package com.snypir.callback.fragment;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.support.annotation.NonNull;

import com.snypir.callback.R;
import com.snypir.callback.activity.LoginActivity_;
import com.snypir.callback.activity.PurchaseActivity_;
import com.snypir.callback.model.MyNumber;
import com.snypir.callback.preferences.Prefs_;
import com.snypir.callback.utils.PhoneFormatter;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by stepangoncarov on 14/06/14.
 */
@EFragment
public class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {

    public static final String LOGOUT = "logout";
    public static final String BALANCE = "balance";
    public static final String CATEGORY_NUMBERS = "category_numbers";
    public static final String ADD_NUMBER = "add_number";

    @Pref
    Prefs_ preferences;

    PhoneFormatter formatter = new PhoneFormatter();

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
        final PreferenceCategory numbers = (PreferenceCategory) findPreference(CATEGORY_NUMBERS);
        final Preference preference = makeMyNumberPreference();
        if (preference != null) {
            numbers.addPreference(preference);
        }
        for (Preference sp : makeSecondaryNumbersPreferences()) {
            numbers.addPreference(sp);
        }
        initLogout();
        initBalance();
        initAddNumber();
    }

    private void initAddNumber() {
        final Preference addNumber = findPreference(ADD_NUMBER);
        addNumber.setOnPreferenceClickListener(this);
    }

    @Nullable
    private Preference makeMyNumberPreference() {
        MyNumber number = MyNumber.getPrimary();
        if (number == null || getActivity() == null) {
            return null;
        }
        Preference p = new Preference(getActivity());
        p.setTitle(formatter.format(number.getNumber()));
        p.setSummary(R.string.your_main_number);
        return p;
    }


    private List<Preference> makeSecondaryNumbersPreferences() {
        List<Preference> list = new ArrayList<>();
        final List<MyNumber> numbers = MyNumber.getSecondary();
        if (getActivity() == null || numbers == null) {
            return list;
        }
        for (MyNumber number : numbers) {
            Preference p = new Preference(getActivity());
            p.setTitle(formatter.format(number.getNumber()));
            p.setSummary(R.string.secondary_number);
            list.add(p);
        }
        return list;
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
            MyNumber.deleteAll();
            if (getActivity() != null) {
                getActivity().finish();
            }
        } else if (BALANCE.equals(key)) {
            PurchaseActivity_.intent(this).start();
        } else if (ADD_NUMBER.equals(key)) {
            LoginActivity_.intent(this).primary(false).start();
        }
        return false;
    }
}
