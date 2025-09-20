package com.example.demo.client;

import com.example.demo.graphql.model.CreateUserInput;
import com.example.demo.graphql.model.User;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Production implementation. Tests do NOT use this – they mock RestApiClient instead.
 */
@Configuration
public class WebClientRestApiClient implements RestApiClient {

    private final WebClient webClient;

    public WebClientRestApiClient(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public Flux<User> getUsers() {
        return webClient.get()
                .uri("/users")
                .retrieve()
                .bodyToFlux(User.class);
    }

    @Override
    public Mono<User> getUser(Long id) {
        return webClient.get()
                .uri("/users/{id}", id)
                .retrieve()
                .bodyToMono(User.class);
    }

    @Override
    public Mono<User> createUser(CreateUserInput input) {
        return webClient.post()
                .uri("/users")
                .bodyValue(input)
                .retrieve()
                .bodyToMono(User.class);
    }

    @Override
    public Mono<User> updateUser(Long id, CreateUserInput input) {
        return webClient.put()
                .uri("/users/{id}", id)
                .bodyValue(input)
                .retrieve()
                .bodyToMono(User.class);
    }

    @Override
    public Mono<Boolean> deleteUser(Long id) {
        return webClient.delete()
                .uri("/users/{id}", id)
                .retrieve()
                .bodyToMono(Void.class)
                .map(v -> true);
    }
}
