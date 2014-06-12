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
public class CallbackNumberInfo extends Model {

    @Column(name = "callback_number", unique = true,
            onNullConflict = Column.ConflictAction.IGNORE,
            onUniqueConflict = Column.ConflictAction.IGNORE,
            onUniqueConflicts = Column.ConflictAction.IGNORE)
    String CallbackNumber;

    @Column(name = "phone_number")
    String PhoneNumber;

    @Column(name = "favorite")
    boolean Favorite;

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

    public static CallbackNumberInfo getByCallbackNumber(final String number) {
        return new Select()
                .from(CallbackNumberInfo.class)
                .where("callback_number = ?", number)
                .executeSingle();
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
