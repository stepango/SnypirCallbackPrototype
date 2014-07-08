package com.snypir.callback.network;

import android.os.Build;

import com.snypir.callback.BuildConfig;
import com.snypir.callback.Config;
import com.snypir.callback.network.model.MobileContact;

import java.util.List;
import java.util.Locale;

/**
 * Created by stepangoncarov on 05/07/14.
 */
public class UpdateContactsData {

    final String OsType = Config.OS_TYPE;
    final String OsVersion = Build.VERSION.RELEASE;
    final String LanguageCode = Locale.getDefault().toString();
    final String AppVersion = BuildConfig.VERSION_NAME;

    List<MobileContact> Contacts;

}
