package com.snypir.callback.network;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by stepangoncarov on 05/07/14.
 */
public class PhoneNumbers {

    List<String> PhoneNumbers;

    public PhoneNumbers(@NonNull List<String> phoneNumbers) {
        PhoneNumbers = phoneNumbers;
    }

    public PhoneNumbers(@NonNull String phoneNumber) {
        PhoneNumbers = new ArrayList<>();
        PhoneNumbers.add(phoneNumber);
    }
}
