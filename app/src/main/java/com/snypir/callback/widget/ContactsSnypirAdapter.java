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
import android.widget.TextView;

import com.snypir.callback.R;
import com.squareup.picasso.Picasso;

public class ContactsSnypirAdapter extends CursorAdapter {

    private final Context mContext;
    private LayoutInflater mInflater;

    public ContactsSnypirAdapter(Context context) {
        super(context, null, true);
        mContext = context;
        mInflater = LayoutInflater.from(context);
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
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.li_contact, parent, false);
            holder.name = (TextView) convertView.findViewById(R.id.text1);
            holder.phone = (TextView) convertView.findViewById(R.id.text2);
            holder.image = (ImageView) convertView.findViewById(R.id.image);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final Cursor c = getCursor();
        if (c != null) {
            c.moveToPosition(position);
            int nameIndex = c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY);
            int phoneIndex = c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            int imageIndex = c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI);
            holder.name.setText(c.getString(nameIndex));
            holder.phone.setText(c.getString(phoneIndex));
            String uri = c.getString(imageIndex);
            if (!TextUtils.isEmpty(uri)) {
                Picasso.with(mContext)
                        .load(Uri.parse(uri))
                        .placeholder(R.drawable.ic_launcher)
                        .into(holder.image);
            } else {
                holder.image.setImageResource(R.drawable.ic_launcher);
            }
        }

        return convertView;
    }

    @Override
    public View newView(final Context context, final Cursor cursor, final ViewGroup viewGroup) {
        return mInflater.inflate(R.layout.li_fav_contact, viewGroup, false);
    }

    @Override
    public void bindView(final View view, final Context context, final Cursor c) {
        int nameIndex = c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY);
        int phoneIndex = c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
        ((TextView) view.findViewById(R.id.text1)).setText(getCursor().getString(nameIndex));
        ((TextView) view.findViewById(R.id.text2)).setText(getCursor().getString(phoneIndex));
    }

    class ViewHolder {
        TextView name;
        TextView phone;
        ImageView image;
    }

}