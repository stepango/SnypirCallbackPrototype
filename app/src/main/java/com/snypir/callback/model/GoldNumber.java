package com.snypir.callback.model;

import android.provider.BaseColumns;

import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;

/**
 * Created by stepangoncarov on 16/07/14.
 */
@Table(name = "gold_numbers", id = BaseColumns._ID)
public class GoldNumber extends NamePhone {

    public static final String CALLBACK_NUMBER = "callback_number";
    public static final String PHONE_NUMBER = "phone_number";
    public static final String FAVORITE = "favorite";
    public static final String FORMULA = "formula";
    public static final String TIMESTAMP = "timestamp";
    public static final String NAME = "name";

    @Column(name = CALLBACK_NUMBER, unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    String CallbackNumber;
    @Column(name = PHONE_NUMBER)
    String PhoneNumber;
    @Column(name = FAVORITE)
    boolean Favorite;
    @Column(name = FORMULA)
    String CustomCallPriceFormula;
    @Column(name = TIMESTAMP)
    long TimeStamp;
    @Column(name = NAME)
    String name;

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getPhoneNumber() {
        return PhoneNumber;
    }

    @Override
    public String getCallbackNumber() {
        return CallbackNumber;
    }

    @Override
    public String setCallbackNumber(String number) {
        return CallbackNumber = number;
    }

    public static void deleteAll() {
        new Delete().from(BlueNumber.class).execute();
    }
}
