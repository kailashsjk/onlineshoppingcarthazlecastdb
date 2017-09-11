package com.techm.shoppingcart.repository;

import org.springframework.data.repository.CrudRepository;

import com.techm.shoppingcart.entity.Customer;

public interface CustomerRepository  extends CrudRepository<Customer, Long>{
	public Customer findByUserName(String userName);

}
