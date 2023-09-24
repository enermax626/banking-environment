package com.mtech.banking.customerservice.exception;

import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<ErrorResponse> handleNotFoundException(NotFoundException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(this.buildSingleErrorResponse(ex.getSource(), ex.getMessage(), ex.getErrorType()));
  }

  @ExceptionHandler(BusinessException.class)
  public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(this.buildSingleErrorResponse(ex.getSource(), ex.getMessage(), ex.getErrorType()));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
      MethodArgumentNotValidException ex) {

    List<Error> errors = ex.getBindingResult().getFieldErrors().stream()
        .map(error -> Error.builder().source(error.getField()).message(error.getDefaultMessage())
            .type(ErrorType.VALIDATION_ERROR).build()).toList();

    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(this.buildErrorResponse(errors));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
    log.error(ex.getMessage());
    return ResponseEntity.internalServerError()
        .body(
            this.buildSingleErrorResponse("", "Internal Server Error",
                ErrorType.INTERNAL_SERVER_ERROR));
  }

  ErrorResponse buildSingleErrorResponse(String source, String message, ErrorType errorType) {
    return ErrorResponse.builder().error(
        List.of(Error.builder()
            .source(source)
            .message(message)
            .type(errorType)
            .build())
    ).build();
  }

  ErrorResponse buildErrorResponse(List<Error> errors) {
    return ErrorResponse.builder().error(
        errors
    ).build();
  }

}
