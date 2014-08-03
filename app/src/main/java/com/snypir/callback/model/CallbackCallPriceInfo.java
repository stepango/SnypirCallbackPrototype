package com.snypir.callback.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by stepangoncarov on 10/07/14.
 */
@Table(name = "callback_call_price")
public class CallbackCallPriceInfo extends Model{

    public static final String PRICE_RUB = "price_rub";
    public static final String IS_FREE = "is_free";
    public static final String SECONDS_PER_BUBBLE = "seconds_per_bubble";
    public static final String VALID_UNTIL_UNIX_TIME = "valid_until_unix_time";

    @Column(name = PRICE_RUB)
    String PriceRub;
    @Column(name = IS_FREE)
    boolean IsFree;
    @Column(name = SECONDS_PER_BUBBLE)
    int SecondsPerBubble;
    @Column(name = VALID_UNTIL_UNIX_TIME)
    long ValidUntilUnixTime;

}
