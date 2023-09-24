package com.mtech.banking.customerservice.exception;

import lombok.Getter;

@Getter
public class NotFoundException extends RuntimeException{

  private String source;
  private ErrorType errorType;

  public NotFoundException(String source,String message, ErrorType errorType) {
    super(message);
    this.source = source;
    this.errorType = errorType;
  }
}
