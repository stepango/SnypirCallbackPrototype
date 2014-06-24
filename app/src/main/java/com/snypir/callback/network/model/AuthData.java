package com.snypir.callback.network.model;

/**
 * Created by stepangoncarov on 05/06/14.
 */
public class AuthData {

    private String Login;

    private String Password;

    @Override
    public String toString() {
        return String.format("{%s, %s}", Login, Password);
    }

    public String getLogin() {
        return Login;
    }

    public String getPassword() {
        return Password;
    }
}
