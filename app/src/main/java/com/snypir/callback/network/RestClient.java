package com.snypir.callback.network;

import com.snypir.callback.Config;

import org.androidannotations.annotations.rest.Get;
import org.androidannotations.annotations.rest.Post;
import org.androidannotations.annotations.rest.Rest;
import org.androidannotations.api.rest.RestClientSupport;
import org.springframework.http.converter.json.GsonHttpMessageConverter;

@Rest(rootUrl = Config.ROOT_URL, converters = { GsonHttpMessageConverter.class })
public interface RestClient extends RestClientSupport{

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

}