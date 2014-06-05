package com.snypir.callback.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.EditText;

import com.snypir.callback.Action;
import com.snypir.callback.Extra;
import com.snypir.callback.R;

import org.androidannotations.annotations.EBean;

/**
 * Created by stepangoncarov on 04/06/14.
 */
@EBean
public class CallConfirmDialogFragment extends DialogFragment implements DialogInterface.OnClickListener{

    EditText mEditText;

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View confirmation = View.inflate(getActivity(), R.layout.fmt_confirmation, null);
        mEditText = (EditText) confirmation.findViewById(R.id.edit_digits);
        builder.setView(confirmation);
        builder.setPositiveButton(R.string.confirm, this);

        return builder.show();
    }

    @Override
    public void onClick(final DialogInterface dialogInterface, final int i) {
        final String confirmationCode = mEditText.getText().toString();
        if (confirmationCode.length() < 4){
            mEditText.setError(getString(R.string.wrong_confirmation_code_try_one_more_time));
        } else {
            Intent intent = new Intent(Action.CONFIRM);
            intent.putExtra(Extra.CONFIRMATION_CODE, confirmationCode);
            LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
            dismiss();
        }
    }
}
