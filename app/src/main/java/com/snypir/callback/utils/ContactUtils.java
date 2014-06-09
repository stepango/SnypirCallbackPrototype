package com.snypir.callback.utils;

import android.database.Cursor;
import android.provider.ContactsContract;

/**
 * Created by stepangoncarov on 08/06/14.
 */
public class ContactUtils {

    private ContactUtils(){}

    public static long contactId(final Cursor c){
        return c.getLong(c.getColumnIndex(ContactsContract.Contacts._ID));
    }

}
