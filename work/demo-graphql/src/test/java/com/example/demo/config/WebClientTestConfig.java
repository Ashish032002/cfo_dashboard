package com.example.demo.config;

import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;

@TestConfiguration
class WebClientTestConfig {

    @Bean
    public WebClient restApiClient() {
        return Mockito.mock(WebClient.class);
    }

    @Bean
    public WebClient.RequestHeadersUriSpec<?> requestHeadersUriSpec() {
        return Mockito.mock(WebClient.RequestHeadersUriSpec.class);
    }

    @Bean
    public WebClient.RequestHeadersSpec<?> requestHeadersSpec() {
        return Mockito.mock(WebClient.RequestHeadersSpec.class);
    }

    @Bean
    public WebClient.RequestBodyUriSpec requestBodyUriSpec() {
        return Mockito.mock(WebClient.RequestBodyUriSpec.class);
    }

    @Bean
    public WebClient.ResponseSpec responseSpec() {
        return Mockito.mock(WebClient.ResponseSpec.class);
    }
}

