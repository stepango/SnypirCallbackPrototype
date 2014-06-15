package com.snypir.callback.model;

import android.support.annotation.NonNull;

import com.snypir.callback.Config;

import java.io.Serializable;

/**
 * Created by stepangoncarov on 14/06/14.
 */
public class TaggedPhone implements Serializable{

    private String phone;
    private String tag;

    public TaggedPhone(@NonNull final String phone, @NonNull final String tag) {
        this.phone = phone;
        this.tag = tag;
    }

    public String getPhone() {
        return phone;
    }

    public boolean isSnypir(){
        return Config.SNYPIR_TAG.equals(tag);
    }

    public String getTag() {
        return tag;
    }

    @Override
    public String toString() {
        return phone;
    }
}
