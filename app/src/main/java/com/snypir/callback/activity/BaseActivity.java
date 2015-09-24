package com.snypir.callback.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.MenuItem;

import com.snypir.callback.R;

/**
 * Created by stepangoncarov on 09/06/14.
 */
public abstract class BaseActivity extends Activity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        actionBarInit();
        if (savedInstanceState == null && getBaseFragment() != null){
            getFragmentManager().beginTransaction().add(R.id.lay_root, getBaseFragment()).commit();
        }
    }

    protected void actionBarInit(){
        ActionBar ab = getActionBar();
        if (ab != null){
            ab.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    public abstract Fragment getBaseFragment();
}
