package com.snypir.callback.utils;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.snypir.callback.Config;

import java.util.ArrayList;

/**
 * Created by stepangoncarov on 29/05/14.
 */
public class ContentProviderUtils {

    @Nullable
    public static Cursor findPhoneNumber(@NonNull final Context c, @NonNull final String number) {
        return c.getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                ContactsContract.CommonDataKinds.Phone.NUMBER + "=?",
                new String[]{number},
                null
        );
    }

    public static boolean isNumberExists(@NonNull Context context, @NonNull String number){
        Cursor c = findPhoneNumber(context, number);
        if (c != null && c.getCount() >= 1) {
            c.close();
            return true;
        }
        return false;
    }

    public static void addPhone(final Context context, final long rawContactId, @NonNull String number) {
        number = ContactUtils.modifyPhoneNumber(number);
        if (isNumberExists(context, number)){
            return;
        }

        ArrayList<ContentProviderOperation> ops =
                new ArrayList<>();

        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValue(ContactsContract.Data.RAW_CONTACT_ID, rawContactId)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, number)
                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_CUSTOM)
                .withValue(ContactsContract.CommonDataKinds.Phone.LABEL, Config.SNYPIR_TAG)
                .build());
        try {
            context.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
        } catch (RemoteException | OperationApplicationException e) {
            e.printStackTrace();
        }

    }

    public static int removePhone(final Context context, final String phone) {
        ArrayList<ContentProviderOperation> ops =
                new ArrayList<>();

        ops.add(ContentProviderOperation.newDelete(ContactsContract.Data.CONTENT_URI)
                .withSelection(
                        ContactsContract.CommonDataKinds.Phone.NUMBER + "=?",
                        new String[]{phone})
                .build());
        try {
            return context.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops).length;
        } catch (RemoteException | OperationApplicationException e) {
            e.printStackTrace();
        }
        return 0;
    }

}
