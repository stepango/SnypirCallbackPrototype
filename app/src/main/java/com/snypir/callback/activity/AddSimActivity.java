package com.snypir.callback.activity;

import android.app.Fragment;

import com.snypir.callback.R;
import com.snypir.callback.fragment.AddSimFragment_;

import org.androidannotations.annotations.EActivity;

/**
 * Created by stepangoncarov on 23/07/14.
 */
@EActivity(R.layout.ac_add_sim)
public class AddSimActivity extends BaseActivity {
    @Override
    public Fragment getBaseFragment() {
        return AddSimFragment_.builder().build();
    }
}
