package com.snypir.callback.activity;

import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.snypir.callback.Action;
import com.snypir.callback.Extra;
import com.snypir.callback.R;
import com.snypir.callback.fragment.CallConfirmDialogFragment;
import com.snypir.callback.fragment.LoadingDialogFragment;
import com.snypir.callback.network.AuthInterceptor;
import com.snypir.callback.network.AuthStore;
import com.snypir.callback.network.RegistrationStatus;
import com.snypir.callback.network.RestClient;
import com.snypir.callback.network.UserAuthData;
import com.snypir.callback.network.UserMobileData;
import com.snypir.callback.preferences.Prefs_;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.InstanceState;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.rest.RestService;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;

/**
 * Created by stepangoncarov on 04/06/14.
 */
@EActivity(R.layout.ac_login)
public class LoginActivity extends Activity {

    @RestService
    RestClient client;

    @Bean
    AuthInterceptor authInterceptor;

    @SystemService
    TelephonyManager mTeleManager;

    @Bean(LoadingDialogFragment.class)
    Fragment mLoadingFragment;

    @Bean(CallConfirmDialogFragment.class)
    Fragment mConfirmationDialog;

    @ViewById(R.id.edit_phone)
    EditText mEditText;

    @InstanceState
    RegistrationStatus mStatus;

    @Pref
    Prefs_ mPreferences;

    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            final String confirmation = intent.getStringExtra(Extra.CONFIRMATION_CODE);
            registerCallback(confirmation);
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(mReceiver, new IntentFilter(Action.CONFIRM));
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this)
                .unregisterReceiver(mReceiver);
    }

//    @AfterInject
//    void initAuth() {
//        RestTemplate template = client.getRestTemplate();
//        List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
//        interceptors.add(authInterceptor);
//        template.setInterceptors(interceptors);
//    }

    @AfterViews
    void init() {
        String number = mTeleManager.getLine1Number();
        if (!TextUtils.isEmpty(number)) {
            mEditText.setText(number);
        }
    }

    @Background
    void register(View view) {
        showLoadingFragment();
        String operator = mTeleManager.getSimOperatorName();
        try {
            mStatus = client.registerMobileNumber(new UserMobileData(mEditText.getText().toString(), operator));
        } catch (HttpServerErrorException e) {
            showError(e);
            e.printStackTrace();
        }
        hideLoadingFragment();
        showConfirmationDialog();
    }

    @Background
    void registerCallback(final String confirmation){
        try {
            final AuthStore authStore = client.registerCallBackApp(
                    new UserAuthData(mStatus.getData().getRegistrationNumber(), confirmation));
            mPreferences.edit()
                    .login().put(authStore.getUsername())
                    .password().put(authStore.getPassword())
                    .apply();
            Log.d("AUTH", authStore.toString());
        } catch (RestClientException e) {
            e.printStackTrace();
        }
    }

    @UiThread
    void showLoadingFragment() {
        getFragmentManager().beginTransaction().add(mLoadingFragment, "loading").commit();
    }

    @UiThread
    void hideLoadingFragment() {
        getFragmentManager().beginTransaction().remove(mLoadingFragment).commit();
    }

    @UiThread
    void showConfirmationDialog() {
        getFragmentManager().beginTransaction().add(mConfirmationDialog, "confirm").commit();
    }

    @UiThread
    void removeConfirmationDialog() {
        getFragmentManager().beginTransaction().remove(mConfirmationDialog).commit();
    }

    @UiThread
    void showError(Exception e){
        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
    }

}
