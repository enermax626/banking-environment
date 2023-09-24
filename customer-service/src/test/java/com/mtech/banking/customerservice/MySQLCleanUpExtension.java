package com.mtech.banking.customerservice;

import com.mtech.banking.customerservice.repository.CustomerRepository;
import java.util.List;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.test.context.junit.jupiter.SpringExtension;

public class MySQLCleanUpExtension implements BeforeEachCallback {

  @Override
  public void beforeEach(ExtensionContext context) {
    ApplicationContext applicationContext = SpringExtension
        .getApplicationContext(context);

    List.of(
        applicationContext.getBean(CustomerRepository.class)
    ).forEach(JpaRepository::deleteAll);
  }
}