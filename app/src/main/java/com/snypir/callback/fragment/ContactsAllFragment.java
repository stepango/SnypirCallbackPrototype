package com.snypir.callback.fragment;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;

import com.snypir.callback.R;
import com.snypir.callback.widget.ContactsAdapter;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

/**
 * Created by stepangoncarov on 20/05/14.
 */
@EFragment(R.layout.fmt_list)
public class ContactsAllFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    @ViewById(R.id.list)
    StickyListHeadersListView mListView;

    private ContactsAdapter mCursorAdapter;

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mCursorAdapter = new ContactsAdapter(getActivity());
        getLoaderManager().initLoader(R.id.contacts_loader, null, this);
    }

    @AfterViews
    void init(){
        mListView.setFastScrollEnabled(true);
    }

    @Override
    public Loader<Cursor> onCreateLoader(final int i, final Bundle bundle) {
        return new CursorLoader(
                getActivity(),
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                null,
                null,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY + " COLLATE LOCALIZED ASC"
        );
    }

    @Override
    public void onLoadFinished(final Loader<Cursor> loader, final Cursor c) {
        if (loader.getId() == R.id.contacts_loader) {
            mCursorAdapter.swapCursor(c);
            mListView.setAdapter(mCursorAdapter);
        }
    }

    @Override
    public void onLoaderReset(final Loader<Cursor> objectLoader) {

    }
}
