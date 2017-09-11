package com.tech.cadt.order.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.tech.cadt.OrderRepository;
import com.tech.cadt.order.entity.OrderProduct;
import com.tech.cadt.order.rest.JwtUtil;

@RestController
@RequestMapping("/orders")
@CrossOrigin
@Component
public class OrderRestServiceController {
	
	
@Autowired
OrderRepository orderRepository;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(OrderRestServiceController.class);
    private static final String signingKey = "signingKey";
    
   
	@RequestMapping(value="/create", method = RequestMethod.POST)
	@CrossOrigin
	public OrderProduct createOrder(@RequestBody OrderProduct order,HttpServletResponse httpServletResponse,@RequestHeader HttpHeaders headers,HttpServletRequest httpServletRequest) {
		
		List<String> token = headers.get("jwtToken");
		String ipAddressOrder =  httpServletRequest.getRemoteAddr();
		
		Map mapValue=new HashMap();		
		mapValue.put("token", token);		
		mapValue.put("ipAddressOrder", ipAddressOrder);		
	
		RestTemplate restTemplate = new RestTemplate();		 
		String res=restTemplate.postForObject("http://localhost:8222/loginService/isValid", mapValue, String.class);
		
		System.out.print("inside create");
		
		if(res.equals("validToken")){
			
			order.getOrderDate();
			order = orderRepository.save(order);
		}
		
	    return order;
	}

	
	@RequestMapping("/getOrderByCustomer")	
	@CrossOrigin
	
	public List<OrderProduct> getOrderBasedOnCustomerId(@RequestParam long customerId) {
		
		
		System.out.println("inside get by customer");
		System.out.println("inside get by customer"+customerId);
		List<OrderProduct> getCustomer=orderRepository.findByCustomerId(customerId);	
	    return getCustomer;
		
	    
	}
	@RequestMapping("/getOrder")	
	@CrossOrigin
	@ResponseBody 
	public List<OrderProduct> getAll() {
		
		
		
		Iterable<OrderProduct> getCustomer=null;		
		getCustomer=orderRepository.findAll();		
		List<OrderProduct> list = new ArrayList<OrderProduct>();
	    if(getCustomer != null) {
	      for(OrderProduct e: getCustomer) {
	        list.add(e);
	      }
	    }
		
	    return list;
		
	    
	}
	
	
	@Consumes(MediaType.APPLICATION_JSON)
	@RequestMapping(value="/createOrder", method = RequestMethod.POST)
	@CrossOrigin
	@ResponseBody 
	public  String createnewOrder(HttpServletResponse httpServletResponse,@RequestHeader HttpHeaders headers,HttpServletRequest httpServletRequest) {

		
		String ipAddressOrder =  httpServletRequest.getRemoteAddr();
    	System.out.println("IP Address: "+ipAddressOrder);
    	
		ClientConfig clientConfig = new ClientConfig();
		
			clientConfig.getGroupConfig().setName("dev").setPassword("dev-pass");
			clientConfig.getNetworkConfig().addAddress("172.18.32.189:5701", "172.18.32.189:5702");
		
			HazelcastInstance client = HazelcastClient.newHazelcastClient(clientConfig);
			IMap map = client.getMap( "jwttoken" );	
 
			
			
	    	 System.out.println( "Token" + map.values() );
	    	 System.out.println("key"+map.keySet().toString());
			
	    	String authvalue= map.keySet().toString();
	    	String  result = authvalue.replaceAll("[\\[\\]]","");
	    	System.out.println("authvalue--"+result);
			
		
		LOGGER.info("Inside createnewOrder() new Order Details::");
		 RestTemplate restTemplate = new RestTemplate();
		 OrderProduct resultObj = null ;
	
			
			String usernameVal = null;
		
			LOGGER.info("Headers::::::" + headers);
			List<String> token = headers.get("jwtToken");
			LOGGER.info("JWT token:::::" + token.get(0));
			String listObj = token.get(0);
			String jwtUsername = JwtUtil.getSubject(httpServletResponse,listObj, signingKey);
			String jwtPassword = JwtUtil.getPassword(httpServletResponse,listObj, signingKey);
			LOGGER.info("::::::Jwt User credentials after Parsing:::::");
			LOGGER.info("username::" + jwtUsername);
			LOGGER.info("password::" + jwtPassword);

		
			LOGGER.info("Customer Object from Customer Service ::::");
			 String valueOrder = new StringBuilder(ipAddressOrder).append("-").append(jwtUsername).toString();
			    
			    System.out.println("valueOrder----"+valueOrder);
			    
			try {
				
				boolean validation=false;
				if (jwtUsername != null) {
					if ((token.equals(map.values())) && (result.equals(valueOrder)) ) {
						validation = true;
						
						LOGGER.info("JWT Token Validataion Sucessful in Order Service.");
						return "if!"+token;
					} 
					else if(result.equals(null)||result.equals("")){
						
						System.out.println("inside result null");
						return result;
					}
					else
					{
						
					
						LOGGER.info("JWT Token Validataion failed in Order Service.");
						validation = false;
						return "else!"+token;
					}
				} else {
					validation = false;
					return result;
				}

				
				
			} catch (Exception e) {
				
			}
			
		return result;	
				
		
}
		
	}