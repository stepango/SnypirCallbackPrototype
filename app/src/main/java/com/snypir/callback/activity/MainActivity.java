package com.snypir.callback.activity;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.snypir.callback.R;
import com.snypir.callback.fragment.ContactsAllFragment_;
import com.snypir.callback.fragment.ContactsSnypirFragment_;
import com.snypir.callback.fragment.DummyFragment_;
import com.snypir.callback.model.CallbackNumberInfo;
import com.snypir.callback.model.MyNumber;
import com.snypir.callback.network.AccountNumbersList;
import com.snypir.callback.network.AuthRestClient;
import com.snypir.callback.network.CallbackNumbersList;
import com.snypir.callback.network.model.PstnAccountInfo;
import com.snypir.callback.preferences.Prefs_;
import com.snypir.callback.service.ContactDataUploaderService_;
import com.snypir.callback.utils.ContactUtils;
import com.snypir.callback.utils.ContentProviderUtils;
import com.snypir.callback.utils.ErrorHandler;
import com.snypir.callback.view.SlidingTabLayout;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.jetbrains.annotations.Nullable;
import org.springframework.web.client.RestClientException;

import java.util.List;
import java.util.Locale;

@EActivity(R.layout.activity_main)
public class MainActivity extends BaseActivity implements ActionBar.TabListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter pagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    @ViewById(R.id.viewpager)
    ViewPager viewPager;

    @Pref
    Prefs_ preferences;

    @ViewById(R.id.lay_root)
    LinearLayout mLinearLayout;

    @Bean
    AuthRestClient rest;

    @Bean
    ErrorHandler errorHandler;

    SlidingTabLayout tabLayout;
    private MenuItem settings;
    private MenuItem register;

    private Runnable initialUploadContacts = new Runnable() {
        @Override
        public void run() {
            uploadContactsInitial();
        }
    };

    private void uploadContactsInitial() {
        if (isUserSignedIn()) {
            ContactDataUploaderService_.intent(this).addRangeInitial(ContactUtils.getAllNumbers(this)).start();
        }
    }

    @Override
    protected void actionBarInit() {
    }

    @AfterViews
    void init() {
        final ActionBar actionBar = getActionBar();
        tabLayout = new SlidingTabLayout(this);
        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(false);
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayUseLogoEnabled(false);
            actionBar.setCustomView(tabLayout);
        }
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        pagerAdapter = new SectionsPagerAdapter(getFragmentManager());

        // Set up the ViewPager with the sections adapter.
        viewPager.setAdapter(pagerAdapter);

        tabLayout.setViewPager(viewPager);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#343434")));
    }

    private void checkLogin() {
        initMenuItems();
    }

    private void initMenuItems() {
        if (settings != null) {
            settings.setVisible(isUserSignedIn());
        }
        if (register != null) {
            register.setVisible(!isUserSignedIn());
        }
    }

    void uploadContacts(){
        if (!preferences.isInitialContactsUploaded().get()){
            AsyncTask.SERIAL_EXECUTOR.execute(initialUploadContacts);
        }
    }

    private boolean isUserSignedIn() {
        return !TextUtils.isEmpty(preferences.login().get()) &&
                !TextUtils.isDigitsOnly(preferences.password().get());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        settings = menu.findItem(R.id.action_settings);
        register = menu.findItem(R.id.action_register);
        initMenuItems();
        return true;
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    public void startSearchActivity(MenuItem item) {
        SearchActivity_.intent(this).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        resumeAction();
        uploadContacts();
    }

    @UiThread
    public void resumeAction() {
        checkLogin();
        populateFriendsPhones();
        populateMyPhones();
    }

    @Background
    void populateMyPhones() {
        if (!isUserSignedIn()) {
            return;
        }
        try {
            final AccountNumbersList data = rest.client.getAllAccountNumbers().getData();
            fillMyNumbers(data.getInfos());
        } catch (RestClientException e) {
            errorHandler.showInfo(e);
        }
    }

    private void fillMyNumbers(final List<PstnAccountInfo> infos) {
        for (PstnAccountInfo info : infos) {
            if (!info.isRegistration()) {
                MyNumber.instantiateSecondary(
                        ContactUtils.modifyPhoneNumber(info.getPhoneNumber())).save();
            }
        }
    }

    @Background
    void populateFriendsPhones() {
        if (!isUserSignedIn()) {
            return;
        }
        try {
            final CallbackNumbersList all = rest.client.CallbackNumbers().getData();
            fillContacts(all.getCallbackNumbers());
        } catch (RestClientException e) {
            errorHandler.showInfo(e);
        }
    }

    void fillContacts(List<CallbackNumberInfo> infos) {
        for (CallbackNumberInfo info : infos) {
            if (!info.isFavorite()) {
                ContentProviderUtils.removePhone(this, info.getСallbackNumber());
            } else {
                final String number = info.getPhoneNumber();
                if (!TextUtils.isEmpty(number)) {
                    fillContact(info);
                    info.setCallbackNumber(ContactUtils.modifyPhoneNumber(info.getСallbackNumber()));
                    info.save();
                }
            }
        }
    }

    void fillContact(CallbackNumberInfo info) {
        Cursor c = ContentProviderUtils.findPhoneNumber(this, info.getPhoneNumber());
        if (c == null) {
            return;
        }
        try {
            if (c.moveToFirst()) {
                long rawContactId = ContactUtils.getRawContactId(c);
                ContentProviderUtils.addPhone(this, rawContactId, info.getСallbackNumber());
            }
        } finally {
            c.close();
        }
    }

    public void startSettingsActivity(MenuItem item) {
        SettingsActivity_.intent(this).start();
    }

    public void register(MenuItem item) {
        LoginActivity_.intent(this).start();
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        String[] mFragments = new String[]{
                ContactsAllFragment_.class.getName(),
                ContactsSnypirFragment_.class.getName(),
                DummyFragment_.class.getName(),
                DummyFragment_.class.getName()
        };

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return Fragment.instantiate(MainActivity.this, mFragments[position]);
        }

        @Override
        public int getCount() {
            return mFragments.length;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.all).toUpperCase(l);
                case 1:
                    return getString(R.string.snypir).toUpperCase(l);
            }
            return null;
        }
    }
}
