package com.example.demo.graphql.resolver;

import graphql.GraphqlErrorBuilder;
import graphql.GraphQLError;
import graphql.execution.ResultPath;
import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import graphql.schema.DataFetchingEnvironment;

/**
 * Null-safe resolver that maps common downstream/validation exceptions to GraphQL ErrorType.
 * Works both in app and in pure unit tests (no Spring context).
 */
@Component
public class GraphqlExceptionResolver extends DataFetcherExceptionResolverAdapter {

    @Override
    protected GraphQLError resolveToSingleError(Throwable ex, DataFetchingEnvironment env) {
        // Validation → BAD_REQUEST
        if (ex instanceof WebExchangeBindException || ex instanceof MethodArgumentNotValidException) {
            return build(ErrorType.BAD_REQUEST, messageOf(ex), env);
        }

        // Downstream HTTP from WebClient
        if (ex instanceof WebClientResponseException wcre) {
            HttpStatus status = HttpStatus.resolve(wcre.getRawStatusCode());
            if (status == HttpStatus.NOT_FOUND) {
                return build(ErrorType.NOT_FOUND, messageOf(wcre), env);
            }
            if (status == HttpStatus.BAD_REQUEST) {
                return build(ErrorType.BAD_REQUEST, messageOf(wcre), env);
            }
            return build(ErrorType.INTERNAL_ERROR, messageOf(wcre), env);
        }

        // Fallback
        return build(ErrorType.INTERNAL_ERROR, messageOf(ex), env);
    }

    private static String messageOf(Throwable t) {
        return (t.getMessage() != null) ? t.getMessage() : t.getClass().getSimpleName();
    }

    private static GraphQLError build(ErrorType type, String msg, @Nullable DataFetchingEnvironment env) {
        var builder = GraphqlErrorBuilder.newError().errorType(type).message(msg);

        if (env != null) {
            // path if present
            if (env.getExecutionStepInfo() != null && env.getExecutionStepInfo().getPath() != null) {
                builder.path(env.getExecutionStepInfo().getPath());
            } else {
                builder.path(ResultPath.parse("/unknown"));
            }
            // location if present
            if (env.getField() != null && env.getField().getSourceLocation() != null) {
                builder.location(env.getField().getSourceLocation());
            }
        }
        return builder.build();
    }
}
