package com.snypir.callback.model;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;

import com.activeandroid.Model;

/**
 * Created by stepangoncarov on 16/07/14.
 */
public abstract class NamePhone extends Model{

    public abstract void setName(String name);

    public abstract String getName();

    public abstract String getPhoneNumber();

    public abstract String getCallbackNumber();

    public abstract String setCallbackNumber(String number);

    public void prepare(final Context context) {
        Cursor cursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME},
                ContactsContract.CommonDataKinds.Phone.NUMBER + "=?",
                new String[]{getPhoneNumber()},
                null
        );
        if (cursor == null){
            return;
        }
        if (cursor.moveToFirst()){
            setName(cursor.getString(0));
        }
        cursor.close();
    }

}
