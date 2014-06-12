package com.snypir.callback.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.snypir.callback.R;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

/**
 * Created by stepangoncarov on 02/06/14.
 */
@EViewGroup(R.layout.li_phone)
public class PhoneView extends RelativeLayout {

    @ViewById(R.id.txt_phone)
    TextView mTxtPhone;

    @ViewById(R.id.txt_type)
    TextView mTxtType;

    @ViewById(R.id.btn_star)
    ImageButton mStar;

    public PhoneView(final Context context) {
        super(context);
    }

    public PhoneView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    public PhoneView(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
    }

    public PhoneView setTexts(final CharSequence phone, final CharSequence type){
        mTxtPhone.setText(phone);
        mTxtType.setText(type);
        return this;
    }

}
