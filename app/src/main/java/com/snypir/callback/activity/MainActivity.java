package com.snypir.callback.activity;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.database.Cursor;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.snypir.callback.R;
import com.snypir.callback.fragment.ContactsAllFragment_;
import com.snypir.callback.fragment.ContactsSnypirFragment_;
import com.snypir.callback.model.CallbackNumberInfo;
import com.snypir.callback.network.AuthRestClient;
import com.snypir.callback.network.CallbackNumbersList;
import com.snypir.callback.preferences.Prefs_;
import com.snypir.callback.utils.ContactUtils;
import com.snypir.callback.utils.ContentProviderUtils;
import com.snypir.callback.utils.ErrorHandler;
import com.snypir.callback.view.SlidingTabLayout;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;
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
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    @ViewById(R.id.viewpager)
    ViewPager mViewPager;

    @Pref
    Prefs_ mPreferences;

    @ViewById(R.id.lay_root)
    LinearLayout mLinearLayout;

    @Bean
    AuthRestClient rest;

    @Bean
    ErrorHandler errorHandler;

    SlidingTabLayout mSlidingTabLayout;

    @Override
    protected void actionBarInit() {
    }

    @AfterViews
    void init() {
        final ActionBar actionBar = getActionBar();
        mSlidingTabLayout = new SlidingTabLayout(this);
        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setCustomView(mSlidingTabLayout);
        }
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mSlidingTabLayout.setViewPager(mViewPager);
    }

    private void checkLogin() {
        final View view = findViewById(R.id.lay_register);
        if (!isUserSignedIn()) {
            if (view == null) {
                mLinearLayout.addView(View.inflate(this, R.layout.btn_register, null));
            }
        } else {
            if (view != null) {
                mLinearLayout.removeView(view);
            }
        }
    }

    private boolean isUserSignedIn() {
        return !TextUtils.isEmpty(mPreferences.login().get()) &&
                !TextUtils.isDigitsOnly(mPreferences.password().get());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
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

    public void register(View view) {
        LoginActivity_.intent(this).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkLogin();
        populatePhones();
    }

    @Background
    void populatePhones() {
        if (!isUserSignedIn()) {
            return;
        }
        try {
            final CallbackNumbersList all = rest.client.getAll().getData();
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

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        String[] mFragments = new String[]{
                ContactsAllFragment_.class.getName(),
                ContactsSnypirFragment_.class.getName()
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
