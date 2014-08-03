package com.snypir.callback.fragment;

import android.animation.ObjectAnimator;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.SearchView;

import com.snypir.callback.R;
import com.snypir.callback.activity.ContactInfoActivity_;
import com.snypir.callback.loader.ContactsPhoneSearchLoader;
import com.snypir.callback.utils.ContactUtils;
import com.snypir.callback.widget.ContactsAdapter;
import com.snypir.callback.widget.SnypirSearchView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

/**
 * Created by stepangoncarov on 20/05/14.
 */
@EFragment(R.layout.fmt_sticky_list)
public class ContactsAllFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemClickListener, View.OnTouchListener, AbsListView.OnScrollListener {

    private static final int IDLE = 0;
    private static final int DRAG = 1;
    @ViewById(R.id.list)
    StickyListHeadersListView mListView;

    @ViewById(R.id.floating_search)
    SnypirSearchView search;

    float initialY;
    float minY;

    private ContactsAdapter mAdapter;

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
                        ContactsAllFragment.this
                );
            }
            return false;
        }
    };

    @AfterViews
    void init() {
        mAdapter = new ContactsAdapter(getActivity());
        View v = new View(getActivity());
        v.setMinimumHeight((int) getResources().getDimension(R.dimen.search_view_height));
        mListView.addHeaderView(v, null, false);
        mListView.setAdapter(mAdapter);
        mListView.setFastScrollEnabled(true);
        mListView.setOnItemClickListener(this);
        mListView.setOnTouchListener(this);
        mListView.setOnScrollListener(this);
        initialY = search.getY();
        minY = initialY - search.getMeasuredHeight();
        search.getSearch().setOnQueryTextListener(mListener);
        search.clearFocus();
        getLoaderManager().initLoader(R.id.contatcs_search_loader, null, this);
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

    @Override
    public void onItemClick(final AdapterView<?> adapterView, final View view, final int i, final long l) {
        final Cursor c = mAdapter.getCursor();
        if (c != null) {
            c.moveToPosition(i + getPositionOffset());
            ContactInfoActivity_.intent(this).contactId(ContactUtils.contactId(c)).start();
        }
    }

    private int getPositionOffset() {
        return -mListView.getHeaderViewsCount();
    }

    float y;
    int mode;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                y = event.getY();     //saving the initial Y
                mode = DRAG;
                break;
            case MotionEvent.ACTION_MOVE:
                if (mode == DRAG) {
                    float scrollByY = event.getY() - y;    //computing the scroll in X
                    if (scrollByY > 0) {
                        showSearch();
                    } else {
                        hideSearch();
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                mode = IDLE;
                break;
        }
        return false;
    }

    private void hideSearch() {
        ObjectAnimator.ofFloat(search, "translationY", -search.getHeight()).start();
    }

    private void showSearch() {
        ObjectAnimator.ofFloat(search, "translationY", 0).start();
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (firstVisibleItem == 0){
            showSearch();
        }
    }
}
