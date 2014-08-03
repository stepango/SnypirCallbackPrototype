package com.snypir.callback.widget;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.snypir.callback.R;
import com.snypir.callback.model.GoldNumber;
import com.snypir.callback.utils.ContactUtils;

import org.jetbrains.annotations.Nullable;

/**
 * Created by stepangoncarov on 17/07/14.
 */
public class GoldContactsAdapter extends CursorAdapter{

    private LayoutInflater inflater;
    private boolean[] selection;

    public GoldContactsAdapter(Context context) {
        super(context, null, false);
        inflater = LayoutInflater.from(context);
    }

    @Nullable
    @Override
    public View newView(final Context context, final Cursor cursor, final ViewGroup viewGroup) {
        return inflater.inflate(R.layout.li_fav_contact, viewGroup, false);
    }

    @Override
    public void bindView(final View view, final Context context, final Cursor c) {
        int nameIndex = c.getColumnIndex(GoldNumber.NAME);
        int callbackNumberIndex = c.getColumnIndex(GoldNumber.CALLBACK_NUMBER);
//        view.findViewById(R.id.lay_root).setFocusable(isItemSelected(c.getPosition()));
        ((TextView) view.findViewById(R.id.text1)).setText(c.getString(nameIndex));
        ((TextView) view.findViewById(R.id.text2)).setText(ContactUtils.modifyPhoneNumber(c.getString(callbackNumberIndex)));
    }

    public void select(int i) {
        selection[i] = !selection[i];
    }

    public void clearSelection() {
        selection = new boolean[getCount()];
    }

    public boolean[] getSelection() {
        return selection;
    }
}
