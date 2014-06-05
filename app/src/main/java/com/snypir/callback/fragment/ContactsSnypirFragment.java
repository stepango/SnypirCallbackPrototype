package com.snypir.callback.fragment;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;

import com.snypir.callback.R;
import com.snypir.callback.utils.ContentProviderUtils;
import com.snypir.callback.widget.ContactsSnypirAdapter;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import de.timroes.android.listview.EnhancedListView;

/**
 * Created by stepangoncarov on 20/05/14.
 */
@EFragment(R.layout.fmt_enchanced_list)
public class ContactsSnypirFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    @ViewById(R.id.list)
    EnhancedListView mListView;

    private ContactsSnypirAdapter mCursorAdapter;

    @AfterViews
    void init() {
        mListView.setDismissCallback(new EnhancedListView.OnDismissCallback() {
            @Override
            public EnhancedListView.Undoable onDismiss(final EnhancedListView enhancedListView, final int i) {
                final Cursor c = mCursorAdapter.getCursor();
                if (c != null) {
                    c.moveToPosition(i);
                    mCursorAdapter.putPendingDismiss(c.getLong(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID)));
                    mCursorAdapter.notifyDataSetChanged();
                    final String phone = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    final long rawContactId = c.getLong(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID));
                    ContentProviderUtils.removePhone(getActivity(), phone);
                    return new EnhancedListView.Undoable() {
                        @Override
                        public void undo() {
                            ContentProviderUtils.addPhone(getActivity(), rawContactId, phone);
                        }
                    };
                }
                return null;
            }
        });
        mListView.setSwipeDirection(EnhancedListView.SwipeDirection.START);
        mListView.setSwipingLayout(R.layout.li_header);
        mListView.enableSwipeToDismiss();
        mListView.setFastScrollEnabled(true);
        mCursorAdapter = new ContactsSnypirAdapter(getActivity());
        mListView.setAdapter(mCursorAdapter);
        getLoaderManager().initLoader(R.id.snypir_contacts_loader, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(final int i, final Bundle bundle) {
        return new CursorLoader(
                getActivity(),
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                ContactsContract.CommonDataKinds.Phone.LABEL + "=?",
                new String[]{"Snypir" },
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY + " COLLATE LOCALIZED ASC"
        );
    }

    @Override
    public void onLoadFinished(final Loader<Cursor> loader, final Cursor c) {
        if (loader.getId() == R.id.snypir_contacts_loader) {
            mCursorAdapter.swapCursor(c);
        }
    }

    @Override
    public void onLoaderReset(final Loader<Cursor> objectLoader) {

    }
}
