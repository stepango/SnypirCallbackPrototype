package com.snypir.callback.network;

import com.snypir.callback.model.BlueNumber;
import com.snypir.callback.model.GoldNumber;
import com.snypir.callback.model.UnusedNumber;

import java.util.List;

/**
 * Created by stepangoncarov on 11/06/14.
 */
public class CallbackNumbersList {

    List<UnusedNumber> UnusedNumbers;

    List<BlueNumber> BlueNumbers;

    List<GoldNumber> GoldNumbers;

    public List<UnusedNumber> getUnusedNumbers() {
        return UnusedNumbers;
    }

    public List<BlueNumber> getBlueNumbers() {
        return BlueNumbers;
    }

    public List<GoldNumber> getGoldNumbers() {
        return GoldNumbers;
    }
}
