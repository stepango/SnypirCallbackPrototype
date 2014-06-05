package com.snypir.callback.network;

import java.io.Serializable;

/**
 * Created by stepangoncarov on 04/06/14.
 */
public class RegistrationStatus implements Serializable{
    boolean IsError;
    String Status;
    String Description;
    RegistrationData Data;

    public boolean isError() {
        return IsError;
    }

    public String getStatus() {
        return Status;
    }

    public String getDescription() {
        return Description;
    }

    public RegistrationData getData() {
        return Data;
    }
}
