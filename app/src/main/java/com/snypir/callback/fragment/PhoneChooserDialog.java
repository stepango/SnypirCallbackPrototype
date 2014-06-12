package com.snypir.callback.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.snypir.callback.R;
import com.snypir.callback.network.AuthRestClient;
import com.snypir.callback.network.CallbackNumberInfo;
import com.snypir.callback.network.Phone;
import com.snypir.callback.network.ResponseTemplate;
import com.snypir.callback.utils.ContentProviderUtils;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.UiThread;
import org.springframework.web.client.RestClientException;

/**
 * Created by stepangoncarov on 09/06/14.
 */
@EFragment
public class PhoneChooserDialog extends DialogFragment {

    @FragmentArg
    String[] numbers;

    @FragmentArg
    long rawContactId;

    @Bean
    AuthRestClient rest;

    Context mContext;

    DialogInterface.OnClickListener mOnClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(final DialogInterface dialogInterface, final int i) {
            mContext = getActivity();
            processNumber(i);
        }
    };

    @Background
    void processNumber(final int i) {
        try{
            final ResponseTemplate<CallbackNumberInfo> callbackNumber =
                    rest.client.getByFavoritePhoneNumber(new Phone(numbers[i]));
            Log.d("ADD CALLBACK NUMBER", callbackNumber.toString());
            ContentProviderUtils.addPhone(mContext, rawContactId,
                    callbackNumber.getData().getCallbackNumber());
            if (getFragmentManager() != null) {
                getFragmentManager().beginTransaction().remove(this).commit();
            }
        } catch (RestClientException e) {
            showError(e);
            e.printStackTrace();
        }
    }

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        final Context context = getActivity();
        if (context != null) {
            return new AlertDialog.Builder(getActivity())
                    .setTitle(getString(R.string.choose_number))
                    .setAdapter(new ArrayAdapter<>(getActivity(),
                            android.R.layout.simple_list_item_1, numbers), mOnClickListener)
                    .create();
        } else {
            return super.onCreateDialog(savedInstanceState);
        }
    }

    @UiThread
    void showError(Exception e) {
        if (getActivity() != null) {
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
