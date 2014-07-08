package com.snypir.callback.fragment;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.snypir.callback.Config;
import com.snypir.callback.R;
import com.snypir.callback.model.CallbackNumberInfo;
import com.snypir.callback.network.AuthRestClient;
import com.snypir.callback.network.Phone;
import com.snypir.callback.network.ResponseTemplate;
import com.snypir.callback.utils.ContactUtils;
import com.snypir.callback.utils.ContentProviderUtils;
import com.snypir.callback.utils.ErrorHandler;
import com.snypir.callback.widget.ContactsSnypirAdapter;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.ItemLongClick;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.springframework.web.client.RestClientException;

/**
 * Created by stepangoncarov on 20/05/14.
 */
@EFragment(R.layout.fmt_list)
public class ContactsSnypirFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    @ViewById(R.id.list)
    ListView mListView;

    @Bean
    AuthRestClient rest;

    @Bean
    ErrorHandler errorHandler;

    private ContactsSnypirAdapter mCursorAdapter;
    private ActionMode mActionMode;
    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

        // Called when the action mode is created; startActionMode() was called
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Inflate a menu resource providing context menu items
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.snypir_contacts_context_menu, menu);
            mode.setTitle(R.string.choose_items_to_delete);
            return true;
        }

        // Called each time the action mode is shown. Always called after onCreateActionMode, but
        // may be called multiple times if the mode is invalidated.
        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false; // Return false if nothing is done
        }

        // Called when the user selects a contextual menu item
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.menu_delete:
                    deleteSelectedItems();
                    mode.finish(); // Action picked, so close the CAB
                    return true;
                default:
                    return false;
            }
        }

        // Called when the user exits the action mode
        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
            mCursorAdapter.clearSelection();
        }
    };

    @Background
    void removeNumber(@NonNull final String number, long rawContactId) {
        //TODO: if getting error from server execute delete request before getting updated numbers from server
        try {
            CallbackNumberInfo info = CallbackNumberInfo.getByCallbackNumber(number);
            final ResponseTemplate response = rest.client.cancelFavorite(new Phone(info.getPhoneNumber()));
            if (response.isError()) {
                errorHandler.showInfo(response);
            } else {
                showMessage(R.string.number_deleted);
            }
        } catch (RestClientException e) {
            e.printStackTrace();
            errorHandler.showInfo(e);
        }
    }

    @AfterViews
    void init() {
        mListView.setFastScrollEnabled(true);
        mCursorAdapter = new ContactsSnypirAdapter(getActivity());
        mListView.setAdapter(mCursorAdapter);
        getLoaderManager().initLoader(R.id.snypir_contacts_loader, null, this);
    }

    @ItemLongClick(R.id.list)
    void initContextMenu(Cursor clickedItem) {
        if (mActionMode != null) {
            return;
        }

        // Start the CAB using the ActionMode.Callback defined above
        mActionMode = getActivity().startActionMode(mActionModeCallback);
        mCursorAdapter.select(clickedItem.getPosition());
    }

    @ItemClick(R.id.list)
    void call(int position) {
        if (mActionMode == null) {
            final Cursor cursor = mCursorAdapter.getCursor();
            if (cursor != null) {
                cursor.moveToPosition(position);
                startDialActivity(ContactUtils.getNumber(cursor));
            }
        } else {
            mCursorAdapter.select(position);
        }
    }

    private void startDialActivity(String phone) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + phone));
        startActivity(intent);
    }

    @Override
    public Loader<Cursor> onCreateLoader(final int i, final Bundle bundle) {
        return new CursorLoader(
                getActivity(),
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                ContactsContract.CommonDataKinds.Phone.LABEL + "=?",
                new String[]{Config.SNYPIR_TAG},
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

    @UiThread
    public void showMessage(@StringRes int id) {
        showMessage(getString(id));
    }

    @UiThread
    public void showMessage(@NonNull final String msg) {
        if (getActivity() != null) {
            Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteSelectedItems() {
        final Cursor c = mCursorAdapter.getCursor();
        if (c == null) {
            return;
        }
        final boolean[] selection = mCursorAdapter.getSelection();
        for (int i = 0, selectionLength = selection.length; i < selectionLength; i++) {
            if (selection[i]) {
                c.moveToPosition(i);
                final String number = ContactUtils.getNumber(c);
                final long rawContactId = ContactUtils.getRawContactId(c);
                if (ContentProviderUtils.removePhone(getActivity(), number) > 0) {
                    removeNumber(number, rawContactId);
                }
            }
        }

    }
}
