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
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.springframework.web.client.RestClientException;

import de.timroes.android.listview.EnhancedListView;

/**
 * Created by stepangoncarov on 20/05/14.
 */
@EFragment(R.layout.fmt_enchanced_list)
public class ContactsSnypirFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    @ViewById(R.id.list)
    EnhancedListView mListView;

    @Bean
    AuthRestClient rest;

    @Bean
    ErrorHandler errorHandler;

    private ContactsSnypirAdapter mCursorAdapter;

    private EnhancedListView.OnDismissCallback mDismissCallback = new EnhancedListView.OnDismissCallback() {
        @Override
        public EnhancedListView.Undoable onDismiss(final EnhancedListView enhancedListView, final int i) {
            final Cursor c = mCursorAdapter.getCursor();
            if (c != null) {
                c.moveToPosition(i);
                final String number = ContactUtils.getNumber(c);
                final long rawContactId = ContactUtils.getRawContactId(c);
                if (ContentProviderUtils.removePhone(getActivity(), number) > 0) {
                    mCursorAdapter.putPendingDismiss(ContactUtils.getPhoneId(c));
                    mCursorAdapter.notifyDataSetChanged();
                    removeNumber(number, rawContactId);
                    return null;
                }
            }
            return null;
        }
    };

    @Background
    void removeNumber(@NonNull final String number, long rawContactId) {
        //TODO: if getting error from server execute delete request before getting updated numbers from server
        try {
            CallbackNumberInfo info = CallbackNumberInfo.getByCallbackNumber(number);
            final ResponseTemplate response = rest.client.cancelFavorite(new Phone(info.getPhoneNumber()));
            if (response.isError()){
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
        mListView.setDismissCallback(mDismissCallback);
        mListView.setSwipeDirection(EnhancedListView.SwipeDirection.START);
        mListView.setSwipingLayout(R.layout.li_header);
        mListView.enableSwipeToDismiss();
        mListView.setFastScrollEnabled(true);
        mCursorAdapter = new ContactsSnypirAdapter(getActivity());
        mListView.setAdapter(mCursorAdapter);
        getLoaderManager().initLoader(R.id.snypir_contacts_loader, null, this);
    }

    @ItemClick(R.id.list)
    void call(int position) {
        final Cursor cursor = mCursorAdapter.getCursor();
        if (cursor != null) {
            cursor.moveToPosition(position);
            startDialActivity(ContactUtils.getNumber(cursor));
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
    public void showMessage(@StringRes int id){
        showMessage(getString(id));
    }

    @UiThread
    public void showMessage(@NonNull final String msg){
        if (getActivity() != null) {
            Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
        }
    }
}
