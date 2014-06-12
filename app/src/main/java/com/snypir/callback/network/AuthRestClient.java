package com.snypir.callback.network;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.rest.RestService;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by stepangoncarov on 10/06/14.
 */
@EBean
public class AuthRestClient {

    @RestService
    public RestClient client;

    @Bean
    AuthInterceptor authInterceptor;

    @AfterInject
    void initAuth() {
        RestTemplate template = client.getRestTemplate();
        List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
        interceptors.add(authInterceptor);
        template.setInterceptors(interceptors);
    }

}
