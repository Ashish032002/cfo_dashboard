package com.example.demo.client;

import com.example.demo.graphql.model.CreateUserInput;
import com.example.demo.graphql.model.User;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Component
public interface RestApiClient {
    Flux<User> getUsers();
    Mono<User> getUser(Long id);
    Mono<User> createUser(CreateUserInput input);
    Mono<User> updateUser(Long id, CreateUserInput input);
    Mono<Boolean> deleteUser(Long id);
}
