package com.snypir.callback.activity;

import android.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.snypir.callback.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

/**
 * Created by stepangoncarov on 20/07/14.
 */
@EFragment(R.layout.ac_how_to)
public class HowToFragment extends Fragment implements View.OnClickListener {

    @ViewById(R.id.lay_prices)
    LinearLayout prices;

    @ViewById(R.id.cv_sim_reg)
    CardView cardSimReg;

    @Click(R.id.cv_sim_reg)
    void simReg() {
        LoginActivity_.intent(this).start();
    }

    @AfterViews
    void initPrices() {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
        layoutParams.topMargin = (int) getResources().getDimension(R.dimen.credit_price_offset);
        prices.addView(initBuyButton(), layoutParams);
        prices.addView(initBuyButton(), layoutParams);
        prices.addView(initBuyButton(), layoutParams);
    }

    private View initBuyButton() {
        View view = View.inflate(getActivity(), R.layout.v_credit_price, null);
        view.setOnClickListener(this);
        return view;
    }

    @Override
    @Click({R.id.cv_operator, R.id.cv_purchase})
    public void onClick(View v) {
        Toast.makeText(getActivity(), R.string.reg_sim_first, Toast.LENGTH_SHORT).show();
    }
}
