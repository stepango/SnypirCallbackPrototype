package com.snypir.callback.widget;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.snypir.callback.R;
import com.squareup.picasso.Picasso;

import org.androidannotations.annotations.EBean;

import java.util.ArrayList;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

@EBean
public class ContactsAdapter extends CursorAdapter implements
        StickyListHeadersAdapter, SectionIndexer {

    private final Context mContext;
    private int[] mSectionIndices;
    private Character[] mSectionLetters;
    private LayoutInflater mInflater;

    public ContactsAdapter(Context context) {
        super(context, null, true);
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mSectionIndices = getSectionIndices();
        mSectionLetters = getSectionLetters();
    }

    private int[] getSectionIndices() {
        final Cursor c = getCursor();
        if (c == null) {
            return new int[]{0};
        }
        ArrayList<Integer> sectionIndices = new ArrayList<>();
        if (c.moveToFirst()) {
            int index = c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY);
            char lastFirstChar = Character.toUpperCase(c.getString(index).charAt(0));
            sectionIndices.add(0);
            c.moveToNext();
            while (!c.isAfterLast()) {
                char letter = Character.toUpperCase(c.getString(index).charAt(0));
                if (letter != lastFirstChar) {
                    lastFirstChar = letter;
                    sectionIndices.add(c.getPosition());
                }
                c.moveToNext();
            }
        }
        int[] sections = new int[sectionIndices.size()];
        for (int i = 0; i < sectionIndices.size(); i++) {
            sections[i] = sectionIndices.get(i);
        }
        return sections;
    }

    private Character[] getSectionLetters() {
        final Cursor c = getCursor();
        if (c == null) {
            return new Character[]{};
        }
        Character[] letters = new Character[mSectionIndices.length];
        int index = getCursor().getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY);
        for (int i = 0; i < mSectionIndices.length; i++) {
            getCursor().moveToPosition(mSectionIndices[i]);
            letters[i] = Character.toUpperCase(getCursor().getString(index).charAt(0));
        }
        return letters;
    }

    @Override
    public int getCount() {
        if (getCursor() == null) {
            return 0;
        }
        return getCursor().getCount();
    }

    @Override
    public Object getItem(int position) {
        final Cursor c = getCursor();
        if (c == null) {
            return null;
        }
        c.moveToPosition(position);
        return c;
    }

    @Override
    public long getItemId(int position) {
        final Cursor c = getCursor();
        if (c == null) {
            return 0;
        }
        c.moveToPosition(position);
        return c.getLong(c.getColumnIndex(BaseColumns._ID));
    }

    @Override
    public View newView(final Context context, final Cursor cursor, final ViewGroup viewGroup) {
        return mInflater.inflate(R.layout.li_contact, viewGroup, false);
    }

    @Override
    public void bindView(final View view, final Context context, final Cursor c) {
        int nameIndex = c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY);
        int imageIndex = c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI);
        ((TextView) view.findViewById(R.id.text1)).setText(c.getString(nameIndex));
        ImageView image = (ImageView) view.findViewById(R.id.image);
        String uri = c.getString(imageIndex);
        if (!TextUtils.isEmpty(uri)) {
            Picasso.with(mContext)
                    .load(Uri.parse(uri))
                    .placeholder(R.drawable.ic_launcher)
                    .into(image);
        } else {
            image.setImageResource(R.drawable.ic_launcher);
        }
    }

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        HeaderViewHolder holder;

        if (convertView == null) {
            holder = new HeaderViewHolder();
            convertView = mInflater.inflate(R.layout.li_header, parent, false);
            holder.text = (TextView) convertView.findViewById(R.id.text1);
            convertView.setTag(holder);
        } else {
            holder = (HeaderViewHolder) convertView.getTag();
        }
        Cursor c = getCursor();
        if (c == null) return convertView;
        c.moveToPosition(position);
        // set header text as first char in name
        int index = c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY);
        String header = c.getString(index);
        if (!TextUtils.isEmpty(header)) {
            CharSequence headerChar = header.subSequence(0, 1).toString().toUpperCase();
            holder.text.setText(headerChar);
        }
        return convertView;
    }

    /**
     * Remember that these have to be static, postion=1 should always return
     * the same Id that is.
     */
    @Override
    public long getHeaderId(int position) {
        // return the first character of the country as ID because this is what
        // headers are based upon
        Cursor c = getCursor();
        if (c == null) return 0;
        c.moveToPosition(position);
        int index = c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY);
        String header = c.getString(index);
        if (!TextUtils.isEmpty(header)) {
            return Character.toUpperCase(header.subSequence(0, 1).charAt(0));
        }
        return 0;
    }

    @Override
    public int getPositionForSection(int section) {
        if (section >= mSectionIndices.length) {
            section = mSectionIndices.length - 1;
        } else if (section < 0) {
            section = 0;
        }
        return mSectionIndices[section];
    }

    @Override
    public int getSectionForPosition(int position) {
        for (int i = 0; i < mSectionIndices.length; i++) {
            if (position < mSectionIndices[i]) {
                return i - 1;
            }
        }
        return mSectionIndices.length - 1;
    }

    @Override
    public Object[] getSections() {
        return mSectionLetters;
    }

    class HeaderViewHolder {
        TextView text;
    }

}