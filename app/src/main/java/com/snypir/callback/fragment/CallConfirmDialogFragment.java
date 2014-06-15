package com.snypir.callback.fragment;

import android.app.DialogFragment;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.EditText;

import com.snypir.callback.Action;
import com.snypir.callback.Extra;
import com.snypir.callback.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

/**
 * Created by stepangoncarov on 04/06/14.
 */
@EFragment(R.layout.fmt_confirmation)
public class CallConfirmDialogFragment extends DialogFragment {

    @ViewById(R.id.edit_digits)
    EditText mEditText;

    @AfterViews
    void init() {
        if (getDialog() != null) {
            getDialog().setTitle(getString(R.string.enter_confirmation_code));
        }
    }

    @Click(R.id.btn_confirm)
    void confirm(View v) {
        final String confirmationCode = mEditText.getText().toString();
        if (confirmationCode.length() < 4) {
            mEditText.setError(getString(R.string.wrong_confirmation_code_try_one_more_time));
        } else {
            Intent intent = new Intent(Action.CONFIRM);
            intent.putExtra(Extra.CONFIRMATION_CODE, confirmationCode);
            LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
            dismiss();
        }
    }

    @Click(R.id.btn_retry)
    void retry(View v) {
        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(new Intent(Action.RETRY));
    }

}
