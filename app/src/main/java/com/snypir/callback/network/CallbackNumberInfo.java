package com.snypir.callback.network;

/**
 * Created by stepangoncarov on 10/06/14.
 */
public class CallbackNumberInfo {

    String CallbackNumber;
    String PhoneNumber;
    boolean Favorite;

    public String getCallbackNumber() {
        return CallbackNumber;
    }

    public String getPhoneNumber() {
        return PhoneNumber;
    }

    public boolean isFavorite() {
        return Favorite;
    }

    @Override
    public String toString() {
        return "CallbackNumberInfo{" +
                "CallbackNumber='" + CallbackNumber + '\'' +
                ", PhoneNumber='" + PhoneNumber + '\'' +
                ", Favorite=" + Favorite +
                '}';
    }
}
