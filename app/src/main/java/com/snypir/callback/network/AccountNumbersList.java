package com.snypir.callback.network;

import com.snypir.callback.network.model.PstnAccountInfo;

import java.util.List;

/**
 * Created by stepangoncarov on 27/06/14.
 */
public class AccountNumbersList {

    public List<PstnAccountInfo> getInfos() {
        return PstnAccounts;
    }

    List<PstnAccountInfo> PstnAccounts;

}
