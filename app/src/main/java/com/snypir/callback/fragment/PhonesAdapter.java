package com.snypir.callback.fragment;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.snypir.callback.utils.ContactUtils;

import org.androidannotations.annotations.EBean;

/**
 * Created by stepangoncarov on 14/06/14.
 */
@EBean
public class PhonesAdapter extends CursorAdapter {

    private final LayoutInflater inflater;

    public PhonesAdapter(final Context context) {
        super(context, null, false);
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View newView(final Context context, final Cursor cursor, final ViewGroup viewGroup) {
        return inflater.inflate(android.R.layout.simple_spinner_dropdown_item, viewGroup, false);
    }

    @Override
    public void bindView(final View view, final Context context, final Cursor cursor) {
        ((TextView) view.findViewById(android.R.id.text1)).setText(ContactUtils.getFormattedNumber(cursor));
    }
}
