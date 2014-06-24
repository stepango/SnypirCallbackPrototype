package com.snypir.callback.network;

/**
 * Created by stepangoncarov on 18/06/14.
 */
public class SecondaryNumberInfo {

    String PhoneNumber;

    String OperatorName;

    String AuthCode;

    public SecondaryNumberInfo(final String phoneNumber, final String operatorName, final String authCode) {
        PhoneNumber = phoneNumber;
        OperatorName = operatorName;
        AuthCode = authCode;
    }
}
