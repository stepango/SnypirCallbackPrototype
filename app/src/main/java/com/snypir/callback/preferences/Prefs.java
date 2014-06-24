package com.snypir.callback.preferences;

import org.androidannotations.annotations.sharedpreferences.SharedPref;

/**
 * Created by stepangoncarov on 04/06/14.
 */
@SharedPref(SharedPref.Scope.APPLICATION_DEFAULT)
public interface Prefs {

    String login();

    String password();

    int balance();

    boolean callback();

}
