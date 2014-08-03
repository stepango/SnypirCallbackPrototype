package com.snypir.callback.activity;

import android.app.Fragment;

import com.snypir.callback.R;
import com.snypir.callback.fragment.ContactInfoFragment_;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;

/**
 * Created by stepangoncarov on 01/06/14.
 */
@EActivity(R.layout.ac_root)
public class ContactInfoActivity extends BaseActivity {

    @Extra("contactId")
    long contactId;

    @Override
    public Fragment getBaseFragment() {
        return ContactInfoFragment_.builder()
                .mContactId(contactId)
                .build();
    }
}
