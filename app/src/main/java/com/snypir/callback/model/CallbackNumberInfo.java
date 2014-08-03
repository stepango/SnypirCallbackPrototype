package com.snypir.callback.model;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.util.List;

/**
 * Created by stepangoncarov on 10/06/14.
 */
@Table(name = "callback_numbers")
public class CallbackNumberInfo extends Model {

    public static final String CALLBACK_NUMBER = "callback_number";
    public static final String PHONE_NUMBER = "phone_number";
    public static final String FAVORITE = "favorite";
    public static final String CALL_PRICE = "call_price";
    public static final String NAME = "name";

    @Column(name = CALLBACK_NUMBER, unique = true,
            onNullConflict = Column.ConflictAction.IGNORE,
            onUniqueConflict = Column.ConflictAction.IGNORE,
            onUniqueConflicts = Column.ConflictAction.IGNORE)
    String CallbackNumber;

    @Column(name = PHONE_NUMBER)
    String PhoneNumber;

    @Column(name = FAVORITE)
    boolean Favorite;

    @Column(name = CALL_PRICE)
    CallbackCallPriceInfo CallPrice;

    @Column(name = NAME)
    String Name;

    public CallbackNumberInfo() {
    }

    public CallbackNumberInfo(
            String callbackNumber,
            String phoneNumber,
            boolean favorite) {
        this.CallbackNumber = callbackNumber;
        this.PhoneNumber = phoneNumber;
        this.Favorite = favorite;
    }

    public static List<CallbackNumberInfo> getAll() {
        return new Select()
                .from(CallbackNumberInfo.class)
                .execute();
    }

    public void prepare(final Context context) {
        Cursor cursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME},
                ContactsContract.CommonDataKinds.Phone.NUMBER + "=?",
                new String[]{PhoneNumber},
                null
        );
        if (cursor == null){
            return;
        }
        if (cursor.moveToFirst()){
            Name = cursor.getString(0);
        }
    }

    public static CallbackNumberInfo getByCallbackNumber(final String number) {
        return getByNumber(CALLBACK_NUMBER, number);
    }

    public static CallbackNumberInfo getByPhoneNumber(final String number) {
        return getByNumber(PHONE_NUMBER, number);
    }

    private static CallbackNumberInfo getByNumber(final String column, final String number) {
        return new Select()
                .from(CallbackNumberInfo.class)
                .where(column + " = ?", number)
                .executeSingle();
    }

    public CallbackCallPriceInfo getCallPrice() {
        return CallPrice;
    }

    public String getСallbackNumber() {
        return CallbackNumber;
    }

    public void setCallbackNumber(final String callbackNumber) {
        CallbackNumber = callbackNumber;
    }

    public String getPhoneNumber() {
        return PhoneNumber;
    }

    public void setPhoneNumber(final String phoneNumber) {
        PhoneNumber = phoneNumber;
    }

    public boolean isFavorite() {
        return Favorite;
    }

    public void setFavorite(final boolean favorite) {
        Favorite = favorite;
    }

    @Override
    public String toString() {
        return "CallbackNumberInfo{" +
                "СallbackNumber='" + CallbackNumber + '\'' +
                ", PhoneNumber='" + PhoneNumber + '\'' +
                ", Favorite=" + Favorite +
                '}';
    }
}
