package com.snypir.callback.fragment;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;

import com.snypir.callback.R;
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

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mCursorAdapter = new ContactsSnypirAdapter(getActivity());
        getLoaderManager().initLoader(R.id.snypir_contacts_loader, null, this);
    }

    @AfterViews
    void init() {
        mListView.setDismissCallback(new EnhancedListView.OnDismissCallback() {
            @Override
            public EnhancedListView.Undoable onDismiss(final EnhancedListView enhancedListView, final int i) {
                return null;
            }
        });
        mListView.setShouldSwipeCallback(new EnhancedListView.OnShouldSwipeCallback() {
            @Override
            public boolean onShouldSwipe(final EnhancedListView enhancedListView, final int i) {
                return true;
            }
        });
        mListView.setSwipeDirection(EnhancedListView.SwipeDirection.START);
        mListView.setSwipingLayout(R.layout.li_header);
        mListView.enableSwipeToDismiss();
        mListView.setFastScrollEnabled(true);
    }

    @Override
    public Loader<Cursor> onCreateLoader(final int i, final Bundle bundle) {
        return new CursorLoader(
                getActivity(),
                ContactsContract.Data.CONTENT_URI,
                null,
                null,
                null,
                ContactsContract.Data.DISPLAY_NAME_PRIMARY + " COLLATE LOCALIZED ASC"
        );
    }

    @Override
    public void onLoadFinished(final Loader<Cursor> loader, final Cursor c) {
        if (loader.getId() == R.id.snypir_contacts_loader) {
            mCursorAdapter.swapCursor(c);
            mListView.setAdapter(mCursorAdapter);
        }
    }

    @Override
    public void onLoaderReset(final Loader<Cursor> objectLoader) {

    }
}
