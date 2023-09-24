package com.mtech.banking.customerservice.mapper;

import com.mtech.banking.customerservice.dto.CustomerCreateDTO;
import com.mtech.banking.customerservice.dto.CustomerDTO;
import com.mtech.banking.customerservice.dto.CustomerUpdateDTO;
import com.mtech.banking.customerservice.model.Customer;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface CustomerMapper {

  CustomerDTO entityToDTO(Customer customer);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void updateCustomerFromDto(CustomerUpdateDTO dto, @MappingTarget Customer entity);
  Customer createDTOToEntity(CustomerCreateDTO customerDTO);
}
