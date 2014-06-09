package com.snypir.callback.fragment;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.snypir.callback.R;
import com.snypir.callback.utils.ContentProviderUtils;
import com.snypir.callback.view.PhoneView_;
import com.squareup.picasso.Picasso;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by stepangoncarov on 01/06/14.
 */
@EFragment(R.layout.fmt_contact_info)
public class ContactInfoFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(final View view) {
            ContentProviderUtils.addPhone(getActivity(), (long) view.getTag(),
                    "1-SNYPIR-" + String.valueOf(new Random().nextInt()).substring(0, 4));
            init();
        }
    };
    @FragmentArg("contactId")
    long mContactId;

    @ViewById(R.id.image)
    ImageView mContactImage;

    @ViewById(R.id.text1)
    TextView mTextName;

    @ViewById(R.id.txt_invite)
    TextView mTextInvite;

    @ViewById(R.id.lay_phones)
    LinearLayout mLayPhones;

    private ArrayList<String> mPhones = new ArrayList<>();

    @Click(R.id.btn_call)
    void call() {

    }

    @Click(R.id.btn_snype)
    void snype() {

    }

    @Click(R.id.btn_sent_invitation)
    void invite() {
        Uri uri = Uri.parse("smsto:" + mPhones.get(0));
        Intent it = new Intent(Intent.ACTION_SENDTO, uri);
        it.putExtra("sms_body", getString(R.string.invitation_text));
        startActivity(it);
    }

    @AfterViews
    void init() {
        final LoaderManager manager = getLoaderManager();
        if (manager != null) {
            getLoaderManager().restartLoader(R.id.contact_info_loader, null, this);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(final int i, final Bundle bundle) {
        if (i == R.id.contact_info_loader) {
            return new ContactInfoLoader(getActivity(), mContactId);
        }
        return null;
    }

    @Override
    public void onLoadFinished(final Loader<Cursor> loader, final Cursor cursor) {
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                showContactInfo(cursor);
                mLayPhones.removeAllViews();
                while (!cursor.isAfterLast()) {
                    addPhoneView(cursor);
                    cursor.moveToNext();
                }
            }
            cursor.close();
        }
    }

    private void addPhoneView(final Cursor c) {
        final String phone = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
        final int typeIndex = c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE);
        final long rawContactId = c.getLong(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID));
        final CharSequence type = getTypeLabel(c, typeIndex);
        mPhones.add(phone);
        mLayPhones.addView(PhoneView_
                .build(getActivity())
                .setTexts(phone, type)
                .setOnStarClickListener(mOnClickListener)
                .setRawContactId(rawContactId));
    }

    private CharSequence getTypeLabel(final Cursor c, final int typeIndex) {
        final int type = c.getInt(typeIndex);
        if (type != ContactsContract.CommonDataKinds.Phone.TYPE_CUSTOM) {
            return ContactsContract.CommonDataKinds.Phone.getTypeLabel(getActivity().getResources(), type, "");
        } else {
            return c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.LABEL));
        }
    }

    private void showContactInfo(final Cursor c) {
        final String name = c.getString(
                c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY));
        final String photoUri = c.getString(
                c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));
        mTextName.setText(name);
        mTextInvite.setText(getString(R.string.send_invitation_description, name));
        if (!TextUtils.isEmpty(photoUri)) {
            Picasso.with(getActivity())
                    .load(Uri.parse(photoUri))
                    .placeholder(R.drawable.ic_launcher)
                    .into(mContactImage);
        } else {
            mContactImage.setImageResource(R.drawable.ic_launcher);
        }
    }

    @Override
    public void onLoaderReset(final Loader<Cursor> loader) {

    }

    private static class ContactInfoLoader extends CursorLoader {
        public ContactInfoLoader(final Context context, long contactId) {
            super(context,
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?",
                    new String[]{String.valueOf(contactId)},
                    null);
        }
    }

}
