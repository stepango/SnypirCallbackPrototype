package com.snypir.callback.network;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.springframework.http.HttpAuthentication;
import org.springframework.http.HttpBasicAuthentication;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

@EBean(scope = EBean.Scope.Singleton)
public class AuthInterceptor implements ClientHttpRequestInterceptor {

    @Bean
    AuthStore authStore;

    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        HttpHeaders headers = request.getHeaders();
        HttpAuthentication auth = new HttpBasicAuthentication(authStore.getUsername(), authStore.getPassword());
        headers.setAuthorization(auth);
        return execution.execute(request, body);
    }
}