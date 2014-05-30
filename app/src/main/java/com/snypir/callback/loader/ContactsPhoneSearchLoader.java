package com.snypir.callback.loader;

import android.content.Context;
import android.content.CursorLoader;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.util.regex.Pattern;

/**
 * Created by stepangoncarov on 28/05/14.
 */
public class ContactsPhoneSearchLoader extends CursorLoader {

    public static final String QUERY_KEY = "query";

    private Pattern mPattern = Pattern.compile("[а-яА-Я]");

    private static final String SELECTION =
            "UPPER(" + ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY + ") %s";

    private static final Uri URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;

    private static final String SORT_ORDER =
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY + " COLLATE LOCALIZED ASC";

    public ContactsPhoneSearchLoader(@NonNull final Context context, @Nullable final Bundle bundle) {
        super(context);
        String selection;
        if (bundle != null && bundle.containsKey(QUERY_KEY)) {
            final String query = bundle.getString(QUERY_KEY);
            selection = String.format(SELECTION, "LIKE UPPER('%" + query + "%')");
            if (!TextUtils.isEmpty(query)
                    && mPattern.matcher(String.valueOf(query.charAt(0))).matches()) {
                String russianFix = String.valueOf(Character.toUpperCase(query.charAt(0)));
                if (query.length() > 1) {
                    russianFix += query.substring(1);
                }
                selection += " OR " + String.format(SELECTION, "LIKE '%" + russianFix + "%'");
            }

        } else {
            selection = "";
        }
        setUri(URI);
        setSelection(selection);
        setSortOrder(SORT_ORDER);
    }
}
