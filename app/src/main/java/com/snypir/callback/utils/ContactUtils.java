package com.snypir.callback.utils;

import android.database.Cursor;
import android.provider.ContactsContract;

/**
 * Created by stepangoncarov on 08/06/14.
 */
public class ContactUtils {

    private ContactUtils() {
    }

    public static long contactId(final Cursor c) {
        return c.getLong(c.getColumnIndex(ContactsContract.Contacts._ID));
    }

    public static long getPhoneId(final Cursor c) {
        return c.getLong(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID));
    }

    public static long getRawContactId(final Cursor c) {
        return c.getLong(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID));
    }

    public static String getNumber(final Cursor c) {
        return c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
    }

    public static String modifyPhoneNumber(final String number) {
        if (number.length() > 1 && number.charAt(0) != '+') {
            return '+' + number;
        }
        return number;
    }

}
