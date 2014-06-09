package com.snypir.callback.activity;

import android.app.ActionBar;
import android.view.MenuItem;

import com.snypir.callback.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;

/**
 * Created by stepangoncarov on 27/05/14.
 */
@EActivity(R.layout.ac_search)
public class SearchActivity extends BaseActivity {

    @AfterViews
    void init(){
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
}
