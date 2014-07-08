package com.snypir.callback.utils;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;

import com.snypir.callback.model.ContactState;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by stepangoncarov on 08/06/14.
 */
public class ContactUtils {

    public static final int RAW_CONTACTS_VERSIONS_CONTACT_ID_COLUMN_INDEX = 0;
    public static final int RAW_CONTACTS_VERSIONS_VERSION_COLUMN_INDEX = 1;
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

    @Nullable
    public static String getNumber(@NonNull final Cursor c) {
        return c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
    }

    public static String getFormattedNumber(@NonNull final Cursor c) {
        final String number = getNumber(c);
        synchronized (ContactUtils.class) {
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

    public static boolean dumpContactStates(final Context context) {
        Cursor c = getRawContactsVersions(context);
        if (c == null) {
            return false;
        }
        List<ContactState> contactStates = new ArrayList<>(c.getCount());
        try {
            if (c.moveToFirst()) {
                while (!c.isAfterLast()) {
                    contactStates.add(new ContactState(
                                    c.getInt(RAW_CONTACTS_VERSIONS_CONTACT_ID_COLUMN_INDEX),
                                    c.getInt(RAW_CONTACTS_VERSIONS_VERSION_COLUMN_INDEX))
                    );
                    c.moveToNext();
                }
            } else {
                return false;
            }
        } finally {
            c.close();
        }
        DbUtils.bulkSave(contactStates);
        return true;
    }

    public static Cursor getRawContactsVersions(Context context) {
        return context.getContentResolver().query(
                ContactsContract.RawContacts.CONTENT_URI,
                new String[]{ContactsContract.RawContacts.CONTACT_ID, ContactsContract.RawContacts.VERSION},
                null,
                null,
                null
        );
    }

    public static ArrayList<String> getAllNumbers(Context context){
        return getNumbers(context, null, null);
    }

    @Nullable
    public static ArrayList<String> getNumbers(Context context, long contactId) {
        return getNumbers(
                context,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?",
                new String[]{String.valueOf(contactId)}
        );
    }

    @Nullable
    public static ArrayList<String> getNumbers(Context context, String selection, String[] selectionArgs) {
        Cursor c = context.getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                selection,
                selectionArgs,
                null
        );
        if (c == null) {
            return null;
        }
        try {
            if (c.moveToFirst()) {
                ArrayList<String> phones = new ArrayList<>();
                while (!c.isAfterLast()) {
                    phones.add(ContactUtils.getNumber(c));
                    c.moveToNext();
                }
                return phones;
            } else {
                return null;
            }
        } finally {
            c.close();
        }
    }
}
