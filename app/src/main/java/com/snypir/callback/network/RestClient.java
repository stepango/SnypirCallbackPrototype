package com.snypir.callback.network;

import com.snypir.callback.Config;

import org.androidannotations.annotations.rest.Post;
import org.androidannotations.annotations.rest.Rest;
import org.androidannotations.api.rest.RestClientSupport;
import org.springframework.http.converter.json.GsonHttpMessageConverter;

@Rest(rootUrl = Config.ROOT_URL, converters = { GsonHttpMessageConverter.class })
public interface RestClient extends RestClientSupport{

    @Post("/register/mobilenumber")
    RegistrationStatus registerMobileNumber(UserMobileData data);

    @Post("/register/callbackApp")
    AuthStore registerCallBackApp(UserAuthData data);

}