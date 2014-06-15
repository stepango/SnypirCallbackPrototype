package com.snypir.callback.fragment;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.AdapterView;

import com.snypir.callback.R;
import com.snypir.callback.activity.ContactInfoActivity_;
import com.snypir.callback.utils.ContactUtils;
import com.snypir.callback.widget.ContactsAdapter;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

/**
 * Created by stepangoncarov on 20/05/14.
 */
@EFragment(R.layout.fmt_sticky_list)
public class ContactsAllFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemClickListener {

    @ViewById(R.id.list)
    StickyListHeadersListView mListView;

    private ContactsAdapter mCursorAdapter;

    @AfterViews
    void init() {
        mCursorAdapter = new ContactsAdapter(getActivity());
        mListView.setAdapter(mCursorAdapter);
        mListView.setFastScrollEnabled(true);
        mListView.setOnItemClickListener(this);
        getLoaderManager().initLoader(R.id.contacts_loader, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(final int i, final Bundle bundle) {
        if (i == R.id.phones_loader) {
            return new PhonesCursorLoader(getActivity());
        } else if (i == R.id.contacts_loader) {
            return new ContactsCursorLoader(getActivity());
        }
        return null;
    }

    @Override
    public void onLoadFinished(final Loader<Cursor> loader, final Cursor c) {
        if (loader.getId() == R.id.contacts_loader) {
            mCursorAdapter.swapCursor(c);
        }
    }

    @Override
    public void onLoaderReset(final Loader<Cursor> objectLoader) {

    }

    @Override
    public void onItemClick(final AdapterView<?> adapterView, final View view, final int i, final long l) {
        final Cursor c = mCursorAdapter.getCursor();
        if (c != null) {
            c.moveToPosition(i);
            ContactInfoActivity_.intent(this).contactId(ContactUtils.contactId(c)).start();
        }
    }

    private static class PhonesCursorLoader extends CursorLoader {

        private static final String[] PROJECTION = new String[]{
                ContactsContract.CommonDataKinds.Phone.NUMBER, //TODO: normalized number
                ContactsContract.CommonDataKinds.Phone._ID,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY,
                ContactsContract.CommonDataKinds.Phone.LABEL,
                ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
        };

        public PhonesCursorLoader(final Context c) {
            super(c,
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    PROJECTION,
                    null,
                    null,
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY + " COLLATE LOCALIZED ASC");
        }
    }

    private static class ContactsCursorLoader extends CursorLoader {
        public ContactsCursorLoader(final Context c) {
            super(c,
                    ContactsContract.Contacts.CONTENT_URI,
                    null,
                    ContactsContract.Contacts.HAS_PHONE_NUMBER + "<>0",
                    null,
                    ContactsContract.Contacts.DISPLAY_NAME_PRIMARY + " COLLATE LOCALIZED ASC");
        }
    }
}
