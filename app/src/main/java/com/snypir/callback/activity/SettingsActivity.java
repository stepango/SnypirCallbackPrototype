package com.snypir.callback.activity;

import com.snypir.callback.R;
import com.snypir.callback.fragment.SettingsFragment_;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;

/**
 * Created by stepangoncarov on 14/06/14.
 */
@EActivity(R.layout.ac_root)
public class SettingsActivity extends BaseActivity{

    @AfterViews
    void init(){
        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        getFragmentManager()
                .beginTransaction()
                .replace(R.id.lay_root, SettingsFragment_.builder().build())
                .commit();
    }
}
