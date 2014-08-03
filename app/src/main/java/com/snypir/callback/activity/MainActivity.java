package com.snypir.callback.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.snypir.callback.R;
import com.snypir.callback.fragment.BlueContactsFragment_;
import com.snypir.callback.fragment.ContactsAllFragment_;
import com.snypir.callback.fragment.DummyFragment_;
import com.snypir.callback.fragment.GoldContactsFragment_;
import com.snypir.callback.fragment.SettingsFragment_;
import com.snypir.callback.model.BlueNumber;
import com.snypir.callback.model.GoldNumber;
import com.snypir.callback.model.MyNumber;
import com.snypir.callback.model.NamePhone;
import com.snypir.callback.model.UnusedNumber;
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
import com.snypir.callback.widget.DepthTransformer;
import com.snypir.callback.widget.SnypirViewPager;

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

@EActivity(R.layout.activity_main)
public class MainActivity extends BaseActivity {

    public static final int INFO_PAGE = 4;
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
    SnypirViewPager viewPager;

    @ViewById(R.id.sliding_tab)
    SlidingTabLayout tabLayout;

    @Pref
    Prefs_ preferences;

    @ViewById(R.id.lay_root)
    LinearLayout mLinearLayout;

    @Bean
    AuthRestClient rest;

    @Bean
    ErrorHandler errorHandler;

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
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        getActionBar().hide();

        pagerAdapter = new SectionsPagerAdapter(getFragmentManager());


        // Set up the ViewPager with the sections adapter.
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOffscreenPageLimit(5);
        viewPager.setPagingEnabled(false);
        viewPager.setPageTransformer(true, new DepthTransformer());

        tabLayout.setViewPager(viewPager);

    }

    private void checkLogin() {
        initMenuItems();
    }

    private void initMenuItems() {
        if (!isUserSignedIn()) {
            finish();
            HowToActivity_.intent(this).start();
        }
        tabLayout.setVisibility(isUserSignedIn() ? View.VISIBLE : View.GONE);
    }

    void uploadContacts() {
        if (!preferences.isInitialContactsUploaded().get()) {
            AsyncTask.SERIAL_EXECUTOR.execute(initialUploadContacts);
        }
    }

    private boolean isUserSignedIn() {
        return !TextUtils.isEmpty(preferences.login().get()) &&
                !TextUtils.isDigitsOnly(preferences.password().get());
    }

    @Override
    protected void onResume() {
        super.onResume();
        resumeAction();
        uploadContacts();
    }

    @Override
    public Fragment getBaseFragment() {
        return null;
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
//            errorHandler.showInfo(e);
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
            final CallbackNumbersList all = rest.client.getAllNumbers().getData();
            fillContacts(all);
        } catch (RestClientException e) {
//            errorHandler.showInfo(e);
        }
    }

    void fillContacts(CallbackNumbersList infos) {
        BlueNumber.deleteAll();
        GoldNumber.deleteAll();
        saveNumbers(infos.getBlueNumbers());
        saveNumbers(infos.getGoldNumbers());
        removeUnusedNumbers(infos.getUnusedNumbers());
    }

    private <T extends NamePhone> void saveNumbers(List<T> numbers) {
        for (NamePhone info : numbers) {
            final String number = info.getPhoneNumber();
            if (!TextUtils.isEmpty(number)) {
                fillContact(info);
                info.setCallbackNumber(ContactUtils.modifyPhoneNumber(info.getCallbackNumber()));
                info.prepare(this);
                info.save();
            }
        }
    }

    void removeUnusedNumbers(List<UnusedNumber> numbers) {
        for (UnusedNumber number : numbers) {
            ContentProviderUtils.removePhone(this, number.getCallbackNumber());
        }
    }

    void fillContact(NamePhone info) {
        Cursor c = ContentProviderUtils.findPhoneNumber(this, info.getPhoneNumber());
        if (c == null) {
            return;
        }
        try {
            if (c.moveToFirst()) {
                long rawContactId = ContactUtils.getRawContactId(c);
                ContentProviderUtils.addPhone(this, rawContactId, info.getCallbackNumber());
            }
        } finally {
            c.close();
        }
    }

    public void startSettingsActivity(MenuItem item) {
        SettingsActivity_.intent(this).start();
    }

    public void register(MenuItem item) {
        LoginActivity_.intent(this).primary(true).start();
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        String[] mFragments = new String[]{
                ContactsAllFragment_.class.getName(),
                BlueContactsFragment_.class.getName(),
                GoldContactsFragment_.class.getName(),
                DummyFragment_.class.getName(),
                SettingsFragment_.class.getName()
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
            return null;
        }
    }
}
