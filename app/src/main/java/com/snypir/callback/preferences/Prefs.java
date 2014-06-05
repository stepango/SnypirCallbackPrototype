package com.snypir.callback.preferences;

import org.androidannotations.annotations.sharedpreferences.SharedPref;

/**
 * Created by stepangoncarov on 04/06/14.
 */
@SharedPref
public interface Prefs {

    String login();

    String password();

    long bubbles();

}
