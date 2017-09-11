package com.techm.shoppingcart.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.techm.shoppingcart.entity.Customer;
import com.techm.shoppingcart.repository.CustomerRepository;

@RestController
@RequestMapping("/customer")
@CrossOrigin
public class CustomerController {

	@Autowired
	CustomerRepository customerRepository;
	
	@RequestMapping("/create")
	public Customer create(@Valid @RequestBody Customer customer) {
		
		System.out.print("inside create");
		customer = customerRepository.save(customer);
	    return customer;
	}
	/*@RequestMapping("/getCustomer")
	public List<Customer> getCustomer() {
		
		Iterable<Customer> getCustomer=null;		
		getCustomer=customerRepository.findAll();		
		List<Customer> list = new ArrayList<Customer>();
	    if(getCustomer != null) {
	      for(Customer e: getCustomer) {
	        list.add(e);
	      }
	    }
		
	    return list;
	}*/
	@RequestMapping("/getCustomer")
	public Customer getCustomerByName(@Valid @RequestBody Customer customer) {
		
	Customer cs=new Customer();
	cs=customerRepository.findByUserName(customer.getUserName());
	return cs;
	}



}
