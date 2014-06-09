package com.snypir.callback.fragment;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SearchView;

import com.snypir.callback.R;
import com.snypir.callback.activity.ContactInfoActivity_;
import com.snypir.callback.loader.ContactsPhoneSearchLoader;
import com.snypir.callback.utils.ContactUtils;
import com.snypir.callback.widget.ContactsAdapter;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.ViewById;

/**
 * Created by stepangoncarov on 28/05/14.
 */
@EFragment(R.layout.fmt_search)
public class ContactsSearchFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    @ViewById(R.id.list)
    ListView mList;

    @Bean
    ContactsAdapter mAdapter;

    SearchView mSearchView;
    private SearchView.OnQueryTextListener mListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(final String s) {
            return false;
        }

        @Override
        public boolean onQueryTextChange(final String s) {
            Bundle bundle = new Bundle();
            bundle.putString(ContactsPhoneSearchLoader.QUERY_KEY, s);
            if (getLoaderManager() != null) {
                getLoaderManager().restartLoader(
                        R.id.contatcs_search_loader,
                        bundle,
                        ContactsSearchFragment.this
                );
            }
            return false;
        }
    };

    @AfterViews
    void init() {
        setHasOptionsMenu(true);
        if (getLoaderManager() != null) {
            getLoaderManager().initLoader(R.id.contatcs_search_loader, null, this);
        }
        mList.setAdapter(mAdapter);
    }

    @ItemClick(R.id.list)
    void showInfo(int position){
        final Cursor c = mAdapter.getCursor();
        if (c != null) {
            c.moveToPosition(position);
            ContactInfoActivity_.intent(this).contactId(ContactUtils.contactId(c)).start();
        }
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.search, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        if (searchItem != null) {
            mSearchView = (SearchView) searchItem.getActionView();
            searchItem.expandActionView();
            mSearchView.setOnQueryTextListener(mListener);

        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(final int i, final Bundle bundle) {
        if (i == R.id.contatcs_search_loader) {
            return new ContactsPhoneSearchLoader(getActivity(), bundle);
        }
        return null;
    }

    @Override
    public void onLoadFinished(final Loader<Cursor> loader, final Cursor c) {
        if (loader.getId() == R.id.contatcs_search_loader) {
            mAdapter.swapCursor(c);
        }
    }

    @Override
    public void onLoaderReset(final Loader<Cursor> loader) {
        if (loader.getId() == R.id.contatcs_search_loader) {
            mAdapter.swapCursor(null);
        }
    }
}
