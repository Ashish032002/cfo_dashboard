package com.example.demo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootApplication
public class GraphqlApplication {

    @Bean
    public WebClient restApiClient() {
        // Your REST API base URL
        return WebClient.builder()
                .baseUrl("http://localhost:9090/api")
                .build();
    }

    public static void main(String[] args) {
        SpringApplication.run(GraphqlApplication.class, args);
    }
}