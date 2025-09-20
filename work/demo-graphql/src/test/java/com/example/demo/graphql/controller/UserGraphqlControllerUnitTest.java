package com.example.demo.graphql.controller;

import com.example.demo.client.RestApiClient;
import com.example.demo.graphql.model.CreateUserInput;
import com.example.demo.graphql.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserGraphqlControllerUnitTest {

    @Mock
    RestApiClient restApiClient;

    @InjectMocks
    UserGraphqlController controller;

    @Test
    void userById_returnsMono() {
        User u = new User();
        when(restApiClient.getUser(1L)).thenReturn(Mono.just(u));

        User out = controller.userById(1L).block();

        assertThat(out).isSameAs(u);
        verify(restApiClient).getUser(1L);
        verifyNoMoreInteractions(restApiClient);
    }

    @Test
    void users_returnsFlux() {
        User u1 = new User();
        User u2 = new User();
        when(restApiClient.getUsers()).thenReturn(Flux.just(u1, u2));

        java.util.List<User> list = controller.users().collectList().block();

        assertThat(list).containsExactly(u1, u2);
        verify(restApiClient).getUsers();
        verifyNoMoreInteractions(restApiClient);
    }

    @Test
    void createUser_postsAndReturnsMono() {
        CreateUserInput in = new CreateUserInput();
        User created = new User();
        when(restApiClient.createUser(in)).thenReturn(Mono.just(created));

        User out = controller.createUser(in).block();

        assertThat(out).isSameAs(created);
        verify(restApiClient).createUser(in);
        verifyNoMoreInteractions(restApiClient);
    }

    @Test
    void updateUser_putsAndReturnsMono() {
        CreateUserInput in = new CreateUserInput();
        User updated = new User();
        when(restApiClient.updateUser(5L, in)).thenReturn(Mono.just(updated));

        User out = controller.updateUser(5L, in).block();

        assertThat(out).isSameAs(updated);
        verify(restApiClient).updateUser(5L, in);
        verifyNoMoreInteractions(restApiClient);
    }

    @Test
    void deleteUser_deletesAndReturnsTrue() {
        when(restApiClient.deleteUser(9L)).thenReturn(Mono.just(true));

        Boolean out = controller.deleteUser(9L).block();

        assertThat(out).isTrue();
        verify(restApiClient).deleteUser(9L);
        verifyNoMoreInteractions(restApiClient);
    }
}
