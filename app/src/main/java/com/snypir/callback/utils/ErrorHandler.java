package com.snypir.callback.utils;

import android.content.Context;
import android.widget.Toast;

import com.snypir.callback.network.ResponseTemplate;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.UiThread;
import org.springframework.web.client.RestClientException;

/**
 * Created by stepangoncarov on 11/06/14.
 */
@EBean
public class ErrorHandler {

    private Context mContext;

    public ErrorHandler(final Context context) {
        mContext = context;
    }

    @UiThread
    public void showInfo(RestClientException e) {
        Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @UiThread
    public void showInfo(ResponseTemplate response) {
        Toast.makeText(
                mContext,
                String.format("%s %s", response.getStatus(), response.getDescription()),
                Toast.LENGTH_SHORT
        ).show();
    }

}
