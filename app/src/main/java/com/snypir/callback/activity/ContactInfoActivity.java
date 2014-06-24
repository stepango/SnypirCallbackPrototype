package com.snypir.callback.activity;

import com.snypir.callback.R;
import com.snypir.callback.fragment.ContactInfoFragment_;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;

/**
 * Created by stepangoncarov on 01/06/14.
 */
@EActivity(R.layout.ac_root)
public class ContactInfoActivity extends BaseActivity {

    @Extra("contactId")
    long contactId;

    @AfterInject
    void init(){
        getFragmentManager()
                .beginTransaction()
                .add(R.id.lay_root, ContactInfoFragment_.builder()
                        .mContactId(contactId)
                        .build()
                ).commit();
    }

}
