package com.snypir.callback.activity;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.snypir.callback.Action;
import com.snypir.callback.Extra;
import com.snypir.callback.R;
import com.snypir.callback.fragment.CallConfirmDialogFragment_;
import com.snypir.callback.fragment.LoadingDialogFragment_;
import com.snypir.callback.model.MyNumber;
import com.snypir.callback.network.AuthRestClient;
import com.snypir.callback.network.Balance;
import com.snypir.callback.network.PhoneNumber;
import com.snypir.callback.network.ResponseTemplate;
import com.snypir.callback.network.SecondaryNumberInfo;
import com.snypir.callback.network.UserAuthData;
import com.snypir.callback.network.UserMobileData;
import com.snypir.callback.network.model.AuthData;
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
import org.jetbrains.annotations.Nullable;
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
    TelephonyManager teleManager;

    Fragment loadingFragment;

    Fragment confirmationDialog;

    @ViewById(R.id.edit_phone)
    EditText editText;

    @Nullable
    @InstanceState
    String registrationNumber;

    @Pref
    Prefs_ preferences;

    @org.androidannotations.annotations.Extra
    boolean primary = true;

    @Nullable
    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            final String action = intent.getAction();
            if (Action.CONFIRM.equals(action)) {
                final String confirmation = intent.getStringExtra(Extra.CONFIRMATION_CODE);
                if (primary) {
                    registerCallback(confirmation);
                } else {
                    addNumber(confirmation);
                }
            } else if (Action.RETRY.equals(action)) {
                register(null);
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(receiver, new IntentFilter(Action.CONFIRM));
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(receiver, new IntentFilter(Action.RETRY));
    }

    @Override
    public Fragment getBaseFragment() {
        return null;
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this)
                .unregisterReceiver(receiver);
    }

    @AfterViews
    void init() {
        editText.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        String number = teleManager.getLine1Number();
        if (!TextUtils.isEmpty(number)) {
            editText.setText(number);
        }
        editText.requestFocus();
    }

    @Background
    void register(View view) {
        String formattedNumber = getMobileNumber();
        if (!isNumberValid(formattedNumber)) {
            return;
        }
        showLoadingFragment();
        String operator = teleManager.getSimOperatorName();
        try {
            ResponseTemplate status;
            if (primary) {
                status = rest.client
                        .registerMobileNumber(new UserMobileData(formattedNumber, operator));
            } else {
                status = rest.client
                        .authenticate(new PhoneNumber(formattedNumber));
            }
            if (status.isError()) {
                errorHandler.showInfo(status);
            } else {
                registrationNumber = formattedNumber;
                saveNumber(registrationNumber);
            }
        } catch (RestClientException e) {
            errorHandler.showInfo(e);
        }
        hideLoadingFragment();
        showConfirmationDialog();
    }

    @UiThread
    void showEditTextError(int id) {
        editText.setError(getString(id));
    }

    private String getMobileNumber() {
        Editable e = editText.getText();
        if (e == null) {
            return "";
        }
        return editText.getText().toString().replaceAll("[^\\d^+]", "");
    }

    @Background
    void addNumber(final String confirmation) {
        try {
            String operator = teleManager.getSimOperatorName();
            final ResponseTemplate status =
                    rest.client.addNumber(new SecondaryNumberInfo(registrationNumber, operator, confirmation));
            if (status.isError()) {
                errorHandler.showInfo(status);
            } else {
                saveNumber(registrationNumber);
                finish();
            }
        } catch (RestClientException e) {
            errorHandler.showInfo(e);
        }
    }

    @Background
    void registerCallback(final String confirmation) {
        showLoadingFragment();
        try {
            ResponseTemplate<AuthData> authData = rest.client.registerCallBackApp(
                    new UserAuthData(registrationNumber, confirmation));
            if (authData.isError()) {
                errorHandler.showInfo(authData);
            } else {
                preferences.login().put(authData.getData().getLogin());
                preferences.password().put(authData.getData().getPassword());
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
                preferences.edit().balance().put(balanceResponse.getData().getBalance()).apply();
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
        if (loadingFragment == null) {
            loadingFragment = new LoadingDialogFragment_();
        }
        if (!loadingFragment.isVisible()) {
            getFragmentManager().beginTransaction().add(loadingFragment, "loading").commit();
        }
    }

    @UiThread
    void hideLoadingFragment() {
        getFragmentManager().beginTransaction().remove(loadingFragment).commit();
    }

    @UiThread
    void showConfirmationDialog() {
        if (confirmationDialog == null) {
            confirmationDialog = new CallConfirmDialogFragment_();
        }
        if (!confirmationDialog.isVisible()) {
            getFragmentManager().beginTransaction().add(confirmationDialog, "confirm").commit();
        }
    }

    @UiThread
    void removeConfirmationDialog() {
        if (confirmationDialog != null) {
            getFragmentManager().beginTransaction().remove(confirmationDialog).commit();
        }
    }

    private void saveNumber(final String formattedNumber) {
        if (primary) {
            MyNumber.instantiatePrimary(formattedNumber).save();
        } else {
            MyNumber.instantiateSecondary(formattedNumber).save();
        }
    }

    private boolean isNumberValid(@NonNull final String formattedNumber) {
        if (!formattedNumber.startsWith("+")) {
            showEditTextError(R.string.number_should_start_with_plus);
            return false;
        }
        if (formattedNumber.length() < 12) {
            showEditTextError(R.string.mobile_number_should_be_11_digits_minimum);
            return false;
        }
        return true;
    }

}
