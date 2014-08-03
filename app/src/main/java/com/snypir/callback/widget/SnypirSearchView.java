package com.snypir.callback.widget;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.SearchView;

import com.snypir.callback.R;

/**
 * Created by stepangoncarov on 13/07/14.
 */
public class SnypirSearchView extends CardView {

    private SearchView search;

    public SnypirSearchView(Context context) {
        this(context, null);
    }

    public SnypirSearchView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SearchView getSearch() {
        return search;
    }

    public SnypirSearchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View.inflate(context, R.layout.search_view, this);
        search = (SearchView) findViewById(R.id.edit_search);
        search.onActionViewExpanded();
        search.clearFocus();
        int searchPlateId = search.getContext().getResources().getIdentifier("android:id/search_plate", null, null);
        // Getting the 'search_plate' LinearLayout.
        View searchPlate = search.findViewById(searchPlateId);
        // Setting background of 'search_plate' to earlier defined drawable.
        searchPlate.setBackgroundResource(android.R.drawable.screen_background_light_transparent);
    }
}
