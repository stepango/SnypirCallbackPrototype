package com.snypir.callback.network;

import com.snypir.callback.Config;
import com.snypir.callback.model.CallbackNumberInfo;
import com.snypir.callback.network.model.AbonentInfo;
import com.snypir.callback.network.model.AuthData;

import org.androidannotations.annotations.rest.Get;
import org.androidannotations.annotations.rest.Post;
import org.androidannotations.annotations.rest.Rest;
import org.androidannotations.api.rest.RestClientSupport;
import org.springframework.http.converter.json.GsonHttpMessageConverter;

@Rest(rootUrl = Config.ROOT_URL, converters = {GsonHttpMessageConverter.class})
public interface RestClient extends RestClientSupport {

    @Post(Service.REGISTER + "/mobileNumber")
    ResponseTemplate<RegistrationData> registerMobileNumber(UserMobileData data);

    @Post(Service.REGISTER + "/callbackApp")
    ResponseTemplate<AuthData> registerCallBackApp(UserAuthData data);


    @Get(Service.CALLBACK + "/getStatus")
    ResponseTemplate<CallBackStatus> getCallbackStatus();

    @Get(Service.CALLBACK + "/enable")
    ResponseTemplate enableCallback();

    @Get(Service.CALLBACK + "/disable")
    ResponseTemplate disableCallback();


    @Post(Service.PSTN_CALL_PRICE + "/getByPhoneNumber")
    ResponseTemplate<CallPrice> callPriceByPhoneNumber(Phone phone);


    @Post(Service.ABONENT + "/getInfoByPhoneNumber")
    ResponseTemplate<AbonentInfo> getInfoByPhoneNumber(Phone phone);


    @Get(Service.PROFILE + "/getBalance")
    ResponseTemplate<Balance> getBalance();


    @Post(Service.CALLBACK_NUMBER + "/getByFavoritePhoneNumber")
    ResponseTemplate<CallbackNumberInfo> getByFavoritePhoneNumber(Phone phone);

    @Post(Service.CALLBACK_NUMBER + "/cancelFavorite")
    ResponseTemplate cancelFavorite(Phone phone);

    @Get(Service.CALLBACK_NUMBER + "/getAll")
    ResponseTemplate<CallbackNumbersList> CallbackNumbers();


    @Post(Service.PSTN_ACCOUNT + "/auth")
    ResponseTemplate authenticate(PhoneNumber number);

    @Post(Service.PSTN_ACCOUNT + "/add")
    ResponseTemplate addNumber(SecondaryNumberInfo info);

    @Get(Service.PSTN_ACCOUNT + "/getAll")
    ResponseTemplate<AccountNumbersList> getAllAccountNumbers();

    @Post(Service.CONTACT_PHONE_NUMBER + "/addRange")
    ResponseTemplate addRange(PhoneNumbers numbers);

}