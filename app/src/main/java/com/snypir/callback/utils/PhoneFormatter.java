package com.snypir.callback.utils;

import android.support.annotation.NonNull;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import java.util.Locale;

/**
 * Created by stepangoncarov on 15/06/14.
 */
public class PhoneFormatter {

    PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();

    public String format(@NonNull String number) {
        if (number.startsWith("+")) {
            try {
                Phonenumber.PhoneNumber phone = phoneUtil.parse(number, "ZZ");
                return phoneUtil.format(phone, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL);
            } catch (NumberParseException e) {
                e.printStackTrace();
                return number;
            }
        } else {
            try {
                Phonenumber.PhoneNumber phone = phoneUtil.parse(number, Locale.getDefault().getCountry());
                return phoneUtil.format(phone, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL);
            } catch (NumberParseException e) {
                e.printStackTrace();
                return number;
            }
        }
    }
}
