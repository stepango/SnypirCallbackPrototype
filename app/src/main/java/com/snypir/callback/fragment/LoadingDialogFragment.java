package com.snypir.callback.fragment;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.Window;

import com.snypir.callback.R;

import org.androidannotations.annotations.EFragment;

/**
 * Created by stepangoncarov on 04/06/14.
 */
@EFragment(R.layout.progress)
public class LoadingDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        final Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setCancelable(false);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }
}
