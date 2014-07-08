package com.snypir.callback.widget;

import android.content.Context;
import android.preference.Preference;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.snypir.callback.R;

/**
 * Created by stepangoncarov on 14/06/14.
 */
public class IntTextViewPreference extends Preference {
    public IntTextViewPreference(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
    }

    public IntTextViewPreference(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    public IntTextViewPreference(final Context context) {
        super(context);
    }

    @Override
    protected void onBindView(@NonNull final View view) {
        super.onBindView(view);
        TextView textView = (TextView) view.findViewById(R.id.text);
        int count = getSharedPreferences().getInt(getKey(), -1);
        textView.setText(String.valueOf(count));
    }
}
