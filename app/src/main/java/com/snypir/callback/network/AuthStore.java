package com.snypir.callback.network;

import org.androidannotations.annotations.EBean;

/**
 * Created by stepangoncarov on 04/06/14.
 */
@EBean(scope = EBean.Scope.Singleton)
public class AuthStore {

    private String Login;

    private String Password;

    public String getUsername() {
        return Login;
    }

    public String getPassword() {
        return Password;
    }

    @Override
    public String toString() {
        return String.format("{%s, %s}", Login, Password);
    }
}
