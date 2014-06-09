package com.snypir.callback.network;

import java.io.Serializable;

/**
 * Created by stepangoncarov on 04/06/14.
 */
public class ResponseTemplate<T> implements Serializable{
    boolean IsError;
    String Status;
    String Description;
    T Data;

    public boolean isError() {
        return IsError;
    }

    public String getStatus() {
        return Status;
    }

    public String getDescription() {
        return Description;
    }

    public T getData() {
        return Data;
    }

    @Override
    public String toString() {
        return String.format("{isError: %s, Status: %s, Description: %s, Data: %s}", String.valueOf(IsError), Status, Description, Data);
    }
}
