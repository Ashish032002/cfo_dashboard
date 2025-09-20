package com.example.demo.graphql.controller;

import com.example.demo.client.RestApiClient;
import com.example.demo.graphql.model.CreateUserInput;
import com.example.demo.graphql.model.User;
import jakarta.validation.Valid;                  // <-- add
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated; // already imported
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Controller
@Validated                                          // <-- add this
public class UserGraphqlController {

    private final RestApiClient restApiClient;

    public UserGraphqlController(RestApiClient restApiClient) {
        this.restApiClient = restApiClient;
    }

    @QueryMapping
    public Mono<User> userById(@Argument Long id) {
        return restApiClient.getUser(id);
    }

    @QueryMapping
    public Flux<User> users() {
        return restApiClient.getUsers();
    }

    @MutationMapping
    public Mono<User> createUser(@Argument @Valid CreateUserInput input) {  // <-- @Valid
        return restApiClient.createUser(input);
    }

    @MutationMapping
    public Mono<User> updateUser(@Argument Long id, @Argument @Valid CreateUserInput input) { // <-- @Valid
        return restApiClient.updateUser(id, input);
    }

    @MutationMapping
    public Mono<Boolean> deleteUser(@Argument Long id) {
        return restApiClient.deleteUser(id);
    }
}
