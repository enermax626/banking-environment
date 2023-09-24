package com.mtech.banking.customerservice.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException{
  private final String source;
  private final ErrorType errorType;

  public BusinessException(String source,String message, ErrorType errorType) {
    super(message);
    this.source = source;
    this.errorType = errorType;
  }

}
