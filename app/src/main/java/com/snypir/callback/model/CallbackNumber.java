package com.snypir.callback.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.util.List;

/**
 * Created by stepangoncarov on 10/06/14.
 */
@Table(name = "callback_numbers")
public class CallbackNumber extends Model {

    @Column(name = "callback_number", unique = true)
    String callbackNumber;

    @Column(name = "phone_number", unique = true)
    String phoneNumber;

    @Column(name = "favorite")
    boolean favorite;

    public CallbackNumber() {
    }

    public CallbackNumber(
            String callbackNumber,
            String phoneNumber,
            boolean favorite) {
        this.callbackNumber = callbackNumber;
        this.phoneNumber = phoneNumber;
        this.favorite = favorite;
    }

    public static List<CallbackNumber> getAll() {
        return new Select()
                .from(CallbackNumber.class)
                .execute();
    }

    public String getCallbackNumber() {
        return callbackNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public boolean isFavorite() {
        return favorite;
    }

    @Override
    public String toString() {
        return "CallbackNumberInfo{" +
                "callbackNumber='" + callbackNumber + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", favorite=" + favorite +
                '}';
    }
}
