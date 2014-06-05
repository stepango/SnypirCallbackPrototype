package com.snypir.callback.network;

import java.io.Serializable;

/**
 * Created by stepangoncarov on 04/06/14.
 */
public class RegistrationData implements Serializable{
    String RegistrationNumber;
    String AuthMessage;
    String AuthCodeType;
    String AuthCodeFormat;
    int AuthCodeLength;

    public String getRegistrationNumber() {
        return RegistrationNumber;
    }

    public String getAuthMessage() {
        return AuthMessage;
    }

    public String getAuthCodeType() {
        return AuthCodeType;
    }

    public String getAuthCodeFormat() {
        return AuthCodeFormat;
    }

    public int getAuthCodeLength() {
        return AuthCodeLength;
    }
}
