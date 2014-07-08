package com.snypir.callback.network.model;

/**
 * Created by stepangoncarov on 27/06/14.
 */
public class PstnAccountInfo {

    String PhoneNumber;
    boolean IsCurrent;
    boolean IsRegistration;

    public String getPhoneNumber() {
        return PhoneNumber;
    }

    public boolean isCurrent() {
        return IsCurrent;
    }

    public boolean isRegistration() {
        return IsRegistration;
    }
}
