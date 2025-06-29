package com.currencyexchange.ExchangeRateApi.exceptions;

import java.util.List;
import java.util.Map;

import org.springframework.graphql.execution.DataFetcherExceptionResolver;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.currencyexchange.ExchangeRateApi.exceptions.interfaces.ICustomStatusException;

import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;
import reactor.core.publisher.Mono;

@Component
public class GraphqlExceptionHandler implements DataFetcherExceptionResolver {
  @Override
  public Mono<List<GraphQLError>> resolveException(Throwable exception, DataFetchingEnvironment environment) {
    if (exception instanceof ICustomStatusException ex) {
      ErrorType errorType = mapHttpStatusToErrorType(ex.getStatusCode());
      return Mono.just(List.of(buildError(exception, errorType, environment)));
    }
    return Mono.empty();
  }

  /**
   * Helper to convert a standard HTTP Status into a GraphQL ErrorType.
   */
  private ErrorType mapHttpStatusToErrorType(HttpStatus status) {
    if (status.is4xxClientError()) {
      return switch (status) {
        case BAD_REQUEST -> ErrorType.BAD_REQUEST;
        case UNAUTHORIZED -> ErrorType.UNAUTHORIZED;
        case FORBIDDEN -> ErrorType.FORBIDDEN;
        case NOT_FOUND -> ErrorType.NOT_FOUND;
        case CONFLICT -> ErrorType.BAD_REQUEST; // there's no conflict here
        default -> ErrorType.BAD_REQUEST;
      };
    }
    // Treat all 5xx server errors as internal errors
    return ErrorType.INTERNAL_ERROR;
  }

  private GraphQLError buildError(Throwable ex, ErrorType errorType, DataFetchingEnvironment env) {
    return GraphqlErrorBuilder.newError()
        .message(ex.getMessage())
        .errorType(errorType)
        .path(env.getExecutionStepInfo().getPath())
        .location(env.getField().getSourceLocation())
        .extensions(Map.of("classification", errorType.name()))
        .build();
  }
}
