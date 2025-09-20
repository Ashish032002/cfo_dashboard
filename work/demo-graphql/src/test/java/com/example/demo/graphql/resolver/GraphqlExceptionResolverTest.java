package com.example.demo.graphql.resolver;

import graphql.GraphQLError;
import graphql.language.Field;
import graphql.language.SourceLocation;
import graphql.schema.DataFetchingEnvironment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.validation.BindException;
import org.springframework.core.MethodParameter;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GraphqlExceptionResolverTest {

    private final GraphqlExceptionResolver resolver = new GraphqlExceptionResolver();

    @Mock
    DataFetchingEnvironment env;

    @BeforeEach
    void setUp() {
        Field field = Field.newField("dummy")
                .sourceLocation(new SourceLocation(1, 1))
                .build();
        when(env.getField()).thenReturn(field);
    }

    @Test
    void notFound_mapsToNotFound() {
        WebClientResponseException ex = WebClientResponseException.create(
                404, "NF", HttpHeaders.EMPTY, new byte[0], StandardCharsets.UTF_8);

        GraphQLError err = resolver.resolveToSingleError(ex, env);

        assertThat(err.getErrorType()).isEqualTo(ErrorType.NOT_FOUND);
    }

    @Test
    void genericDownstream_mapsToInternal() {
        WebClientResponseException ex = WebClientResponseException.create(
                502, "Bad Gateway", HttpHeaders.EMPTY, new byte[0], StandardCharsets.UTF_8);

        GraphQLError err = resolver.resolveToSingleError(ex, env);

        assertThat(err.getErrorType()).isEqualTo(ErrorType.INTERNAL_ERROR);
    }

    @Test
    void validation_mapsToBadRequest() throws NoSuchMethodException {
        // a tiny bean with a "name" field so BindingResult can attach a FieldError
        class Dummy {
            private String name;
            public String getName() { return name; }
            public void setName(String name) { this.name = name; }
        }
        // create a MethodParameter for a dummy controller-like method
        class DummyController { @SuppressWarnings("unused") void handle(Dummy d) {} }
        var method = DummyController.class.getDeclaredMethod("handle", Dummy.class);
        var parameter = new MethodParameter(method, 0);

        // build a BindingResult with a field error on "name"
        var target = new Dummy();
        var br = new BeanPropertyBindingResult(target, "dummy");
        br.rejectValue("name", "NotBlank", "must not be blank");

        // the exception many Spring paths throw for @Valid failures
        var ex = new MethodArgumentNotValidException(parameter, br);

        var err = resolver.resolveToSingleError(ex, env);

        assertThat(err.getErrorType())
                .isEqualTo(org.springframework.graphql.execution.ErrorType.BAD_REQUEST);
    }


    @Test
    void unexpected_mapsToInternal() {
        RuntimeException ex = new RuntimeException("boom");

        GraphQLError err = resolver.resolveToSingleError(ex, env);

        assertThat(err.getErrorType()).isEqualTo(ErrorType.INTERNAL_ERROR);
    }

}
