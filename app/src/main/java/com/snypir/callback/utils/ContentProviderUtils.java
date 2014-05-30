package com.snypir.callback.utils;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.provider.ContactsContract;

import com.snypir.callback.model.Contact;

import java.io.IOException;
import java.util.ArrayList;

import static android.provider.ContactsContract.CommonDataKinds.StructuredName;
import static android.provider.ContactsContract.Data;
import static android.provider.ContactsContract.RawContacts;

/**
 * Created by stepangoncarov on 29/05/14.
 */
public class ContentProviderUtils {

    public static final String ACCOUNT_TYPE = "com.snypir";

    public static void addContact(Context context, Contact contact) throws IOException {
        ContentResolver contentResolver = context.getContentResolver();
        ArrayList<ContentProviderOperation> ops = new ArrayList<>();

        // add empty contact.
        Uri rawContactUri = contentResolver.insert(RawContacts.CONTENT_URI, new ContentValues());
        int rawContactId = (int) ContentUris.parseId(rawContactUri);

        // Add last name, first name and prefix
        ContentValues values = new ContentValues();
        if (!contact.getName().isEmpty()) {
            values.put(Data.RAW_CONTACT_ID, rawContactId);
            values.put(Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE);
            values.put(StructuredName.DISPLAY_NAME, contact.getName());
            contentResolver.insert(Data.CONTENT_URI, values);
        }

        if (!contact.getPhone().isEmpty()) {
            ops.add(ContentProviderOperation.
                    newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactId)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, contact.getPhone())
                    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                            ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                    .build());
        }
    }
}
