package com.snypir.callback.network;

import android.os.Build;

import com.snypir.callback.BuildConfig;
import com.snypir.callback.Config;

import java.util.Locale;

/**
 * Created by stepangoncarov on 04/06/14.
 */
public class UserAuthData {

    final boolean IsProduction = Config.IS_PRODUCTION;
    final String OsType = Config.OS_TYPE;
    final String OsVersion = Build.VERSION.RELEASE;
    final String LanguageCode = Locale.getDefault().toString();
    final String AppVersion = BuildConfig.VERSION_NAME;

    String RegistrationNumber;
    String AuthCode;

    public UserAuthData(final String registrationNumber, final String authCode) {
        RegistrationNumber = registrationNumber;
        AuthCode = authCode;
    }


}
