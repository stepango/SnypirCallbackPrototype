package com.snypir.callback.widget;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.snypir.callback.R;
import com.snypir.callback.utils.ContactUtils;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.Nullable;

@Deprecated
public class ContactsSnypirAdapter extends CursorAdapter {

    private final Context mContext;
    private LayoutInflater mInflater;
    private boolean[] selection;

    public ContactsSnypirAdapter(Context context) {
        super(context, null, true);
        mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    @Nullable
    @Override
    public Cursor swapCursor(final Cursor newCursor) {
        Cursor cursor = super.swapCursor(newCursor);;
        clearSelection(cursor);
        return cursor;
    }

    public void select(int i){
        if (selection != null){
            selection[i] = !selection[i];
        }
        notifyDataSetChanged();
    }

    @Nullable
    @Override
    public View newView(final Context context, final Cursor cursor, final ViewGroup viewGroup) {
        return mInflater.inflate(R.layout.li_fav_contact, viewGroup, false);
    }

    @Override
    public void bindView(final View view, final Context context, final Cursor c) {
        int nameIndex = c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY);
        int imageIndex = c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI);
        view.findViewById(R.id.lay_root).setFocusable(isItemSelected(c.getPosition()));
        ((TextView) view.findViewById(R.id.text1)).setText(c.getString(nameIndex));
        ((TextView) view.findViewById(R.id.text2)).setText(ContactUtils.getFormattedNumber(c));
        ImageView image = (ImageView) view.findViewById(R.id.image);
        String uri = c.getString(imageIndex);
        if (!TextUtils.isEmpty(uri)) {
            Picasso.with(mContext)
                    .load(Uri.parse(uri))
                    .placeholder(R.drawable.ic_contact_picture)
                    .error(R.drawable.ic_contact_picture)
                    .into(image);
        } else {
            Picasso.with(mContext).load(R.drawable.ic_contact_picture).into(image);
        }
    }

    private boolean isItemSelected(final int position) {
        return selection != null && selection[position];
    }

    private int getBackGround(final int position){
        return isItemSelected(position) ? mContext.getResources().getColor(R.color.blue) : Color.WHITE;
    }

    public void clearSelection(){
        Cursor c = getCursor();
        clearSelection(c);
    }

    private void clearSelection(final Cursor c){
        if (c != null) {
            selection = new boolean[c.getCount()];
        } else {
            selection = null;
        }
        notifyDataSetChanged();
    }

    public boolean[] getSelection() {
        return selection;
    }
}