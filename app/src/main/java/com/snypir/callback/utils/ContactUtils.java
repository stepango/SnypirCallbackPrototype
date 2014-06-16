package com.snypir.callback.utils;

import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;

/**
 * Created by stepangoncarov on 08/06/14.
 */
public class ContactUtils {

    private static PhoneFormatter sNumberUtil = new PhoneFormatter();

    private ContactUtils() {
    }

    public static long contactId(@NonNull final Cursor c) {
        return c.getLong(c.getColumnIndex(ContactsContract.Contacts._ID));
    }

    public static long getPhoneId(@NonNull final Cursor c) {
        return c.getLong(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID));
    }

    public static long getRawContactId(@NonNull final Cursor c) {
        return c.getLong(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID));
    }

    public static String getNumber(@NonNull final Cursor c) {
        return c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
    }

    public static String getFormattedNumber(@NonNull final Cursor c) {
        final String number = getNumber(c);
        synchronized (ContactUtils.class){
             return sNumberUtil.format(number);
        }
    }


    public static String modifyPhoneNumber(@NonNull final String number) {
        if (number.length() > 1 && number.charAt(0) != '+') {
            return '+' + number;
        }
        return number;
    }

    public static String getOrFilter(@NonNull String[] phones, @NonNull String column) {
        if (phones.length == 0) return "";
        StringBuilder buffer = new StringBuilder("(");
        for (int i = 0; i < phones.length; i++) {
            buffer.append(column).append("=?");
            if (i < phones.length - 1) {
                buffer.append(" OR ");
            }
        }
        buffer.append(")");
        return buffer.toString();
    }

}
