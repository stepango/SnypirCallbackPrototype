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
import com.snypir.callback.model.CallbackNumberInfo;
import com.snypir.callback.model.TaggedPhone;
import com.snypir.callback.network.AuthRestClient;
import com.snypir.callback.network.Phone;
import com.snypir.callback.network.ResponseTemplate;
import com.snypir.callback.utils.ContentProviderUtils;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.UiThread;
import org.jetbrains.annotations.Nullable;
import org.springframework.web.client.RestClientException;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by stepangoncarov on 09/06/14.
 */
@EFragment
public class PhoneChooserDialog extends DialogFragment {

    @Nullable
    @FragmentArg
    ArrayList<TaggedPhone> numbers;

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
    ArrayAdapter<TaggedPhone> adapter;

    @Background
    void processNumber(final int i) {
        try {
            final ResponseTemplate<CallbackNumberInfo> callbackNumber =
                    rest.client.getByFavoritePhoneNumber(new Phone(numbers.get(i).getPhone()));
            Log.d("ADD CALLBACK NUMBER", callbackNumber.toString());
            ContentProviderUtils.addPhone(mContext, rawContactId,
                    callbackNumber.getData().getСallbackNumber());
            if (getFragmentManager() != null) {
                getFragmentManager().beginTransaction().remove(this).commit();
            }
        } catch (RestClientException e) {
            showError(e);
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        filterNumbers(getActivity());
    }

    @Background
    void filterNumbers(final Context context) {
        Iterator<TaggedPhone> iterator = numbers.iterator();
        while (iterator.hasNext()) {
            TaggedPhone s = iterator.next();
            if (s.isSnypir() || CallbackNumberInfo.getByPhoneNumber(s.getPhone()) != null ||
                    CallbackNumberInfo.getByCallbackNumber(s.getPhone()) != null) {
                iterator.remove();
            }
        }
        if (numbers.size() > 0) {
            populateAdapter();
        } else {
            showNoNumbersInfo(context);
            getFragmentManager().beginTransaction().remove(this).commit();
        }
    }

    @UiThread
    void showNoNumbersInfo(Context context) {
        Toast.makeText(context, R.string.all_numbers_already_favorite, Toast.LENGTH_SHORT).show();
    }

    @UiThread
    void populateAdapter() {
        adapter.addAll(numbers);
    }

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        final Context context = getActivity();
        if (context != null) {
            adapter = new ArrayAdapter<>(getActivity(),
                    android.R.layout.simple_list_item_1);
            return new AlertDialog.Builder(getActivity())
                    .setTitle(getString(R.string.choose_number))
                    .setAdapter(adapter, mOnClickListener)
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
