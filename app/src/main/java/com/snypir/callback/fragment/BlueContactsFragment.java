package com.snypir.callback.fragment;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.widget.ListView;
import android.widget.Toast;

import com.activeandroid.content.ContentProvider;
import com.snypir.callback.R;
import com.snypir.callback.model.BlueNumber;
import com.snypir.callback.model.CallbackNumberInfo;
import com.snypir.callback.network.AuthRestClient;
import com.snypir.callback.network.Phone;
import com.snypir.callback.network.ResponseTemplate;
import com.snypir.callback.utils.ErrorHandler;
import com.snypir.callback.widget.BlueContactsAdapter;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.springframework.web.client.RestClientException;

/**
 * Created by stepangoncarov on 11/07/14.
 */
@EFragment(R.layout.fmt_list)
public class BlueContactsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    @ViewById(R.id.list)
    ListView mListView;

    @Bean
    AuthRestClient rest;

    @Bean
    ErrorHandler errorHandler;

    private BlueContactsAdapter mCursorAdapter;

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
        mCursorAdapter = new BlueContactsAdapter(getActivity());
        mListView.setAdapter(mCursorAdapter);
        getLoaderManager().initLoader(R.id.blue_contacts_loader, null, this);
    }

    @ItemClick(R.id.list)
    void call(int position) {
        final Cursor cursor = mCursorAdapter.getCursor();
        if (cursor != null) {
            cursor.moveToPosition(position);
            startDialActivity(cursor.getString(cursor.getColumnIndex(BlueNumber.CALLBACK_NUMBER)));
        }
    }

    private void startDialActivity(String phone) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + phone));
        startActivity(intent);
    }

    @Override
    public Loader<Cursor> onCreateLoader(final int i, final Bundle bundle) {
        if (i == R.id.blue_contacts_loader) {
            return new CursorLoader(
                    getActivity(),
                    ContentProvider.createUri(BlueNumber.class, null),
                    null,
                    null,
                    null,
                    BlueNumber.NAME + " ASC"
            );
        }
        return null;
    }

    @Override
    public void onLoadFinished(final Loader<Cursor> loader, final Cursor c) {
        if (loader.getId() == R.id.blue_contacts_loader) {
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

}
