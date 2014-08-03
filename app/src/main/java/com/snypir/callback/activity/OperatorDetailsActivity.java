package com.snypir.callback.activity;

import android.app.Fragment;

import com.snypir.callback.R;
import com.snypir.callback.fragment.OperatorDetailsFragment_;

import org.androidannotations.annotations.EActivity;

/**
 * Created by stepangoncarov on 28/07/14.
 */
@EActivity(R.layout.ac_root)
public class OperatorDetailsActivity extends BaseActivity {

    @Override
    public Fragment getBaseFragment() {
        return OperatorDetailsFragment_.builder().build();
    }
}
