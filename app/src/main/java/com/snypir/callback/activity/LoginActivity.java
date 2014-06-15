package com.snypir.callback.activity;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.snypir.callback.Action;
import com.snypir.callback.Extra;
import com.snypir.callback.R;
import com.snypir.callback.fragment.CallConfirmDialogFragment_;
import com.snypir.callback.fragment.LoadingDialogFragment_;
import com.snypir.callback.network.AuthData;
import com.snypir.callback.network.AuthRestClient;
import com.snypir.callback.network.Balance;
import com.snypir.callback.network.RegistrationData;
import com.snypir.callback.network.ResponseTemplate;
import com.snypir.callback.network.UserAuthData;
import com.snypir.callback.network.UserMobileData;
import com.snypir.callback.preferences.Prefs_;
import com.snypir.callback.utils.ErrorHandler;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.InstanceState;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.springframework.web.client.RestClientException;

/**
 * Created by stepangoncarov on 04/06/14.
 */
@EActivity(R.layout.ac_login)
public class LoginActivity extends BaseActivity {

    @Bean
    ErrorHandler errorHandler;

    @Bean
    AuthRestClient rest;

    @SystemService
    TelephonyManager mTeleManager;

    Fragment mLoadingFragment;

    Fragment mConfirmationDialog;

    @ViewById(R.id.edit_phone)
    EditText mEditText;

    @InstanceState
    ResponseTemplate<RegistrationData> mStatus;

    @Pref
    Prefs_ mPreferences;

    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            final String action = intent.getAction();
            if (Action.CONFIRM.equals(action)) {
                final String confirmation = intent.getStringExtra(Extra.CONFIRMATION_CODE);
                registerCallback(confirmation);
            } else if (Action.RETRY.equals(action)){
                register(null);
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(mReceiver, new IntentFilter(Action.CONFIRM));
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(mReceiver, new IntentFilter(Action.RETRY));
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this)
                .unregisterReceiver(mReceiver);
    }

    @AfterViews
    void init() {
        mEditText.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        String number = mTeleManager.getLine1Number();
        if (!TextUtils.isEmpty(number)) {
            mEditText.setText(number);
        }
        mEditText.requestFocus();
    }

    @Background
    void register(View view) {
        showLoadingFragment();
        String operator = mTeleManager.getSimOperatorName();
        try {
            mStatus = rest.client.registerMobileNumber(new UserMobileData(getMobileNumber(), operator));
            if (mStatus.isError()) {
                errorHandler.showInfo(mStatus);
            }
        } catch (RestClientException e) {
            errorHandler.showInfo(e);
        }
        hideLoadingFragment();
        showConfirmationDialog();
    }

    String getMobileNumber() {
        return "+" + mEditText.getText().toString();
    }

    @Background
    void registerCallback(final String confirmation) {
        showLoadingFragment();
        try {
            ResponseTemplate<AuthData> authData = rest.client.registerCallBackApp(
                    new UserAuthData(mStatus.getData().getRegistrationNumber(), confirmation));
            if (authData.isError()) {
                errorHandler.showInfo(authData);
            } else {
                mPreferences.login().put(authData.getData().getLogin());
                mPreferences.password().put(authData.getData().getPassword());
                Log.d("AUTH", authData.toString());
                getBalance();
            }
        } catch (RestClientException e) {
            errorHandler.showInfo(e);
            hideLoadingFragment();
        }
    }

    @Background
    void getBalance() {
        try {
            final ResponseTemplate<Balance> balanceResponse = rest.client.getBalance();
            if (balanceResponse.isError()) {
                errorHandler.showInfo(balanceResponse);
            } else {
                mPreferences.edit().balance().put(balanceResponse.getData().getBalance()).apply();
                Log.d("BALANCE", balanceResponse.toString());
                finish();
            }
        } catch (RestClientException e) {
            errorHandler.showInfo(e);
        }
        hideLoadingFragment();
    }

    @UiThread
    void showLoadingFragment() {
        if (mLoadingFragment == null) {
            mLoadingFragment = new LoadingDialogFragment_();
        }
        if (!mLoadingFragment.isVisible()) {
            getFragmentManager().beginTransaction().add(mLoadingFragment, "loading").commit();
        }
    }

    @UiThread
    void hideLoadingFragment() {
        getFragmentManager().beginTransaction().remove(mLoadingFragment).commit();
    }

    @UiThread
    void showConfirmationDialog() {
        if (mConfirmationDialog == null) {
            mConfirmationDialog = new CallConfirmDialogFragment_();
        }
        if (!mConfirmationDialog.isVisible()) {
            getFragmentManager().beginTransaction().add(mConfirmationDialog, "confirm").commit();
        }
    }

    @UiThread
    void removeConfirmationDialog() {
        if (mConfirmationDialog != null) {
            getFragmentManager().beginTransaction().remove(mConfirmationDialog).commit();
        }
    }

}
