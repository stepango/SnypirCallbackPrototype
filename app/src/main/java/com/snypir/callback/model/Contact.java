package com.snypir.callback.model;

import android.support.annotation.NonNull;

/**
 * Created by stepangoncarov on 29/05/14.
 */
public class Contact {
    private String mName;
    private String mPhone;

    @NonNull
    public String getName() {
        return mName;
    }

    @NonNull
    public String getPhone() {
        return mPhone;
    }
}
