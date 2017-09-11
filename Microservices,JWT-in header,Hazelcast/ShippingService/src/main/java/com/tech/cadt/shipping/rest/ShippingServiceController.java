package com.tech.cadt.shipping.rest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.criteria.Order;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
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
import com.tech.cadt.entity.Shipping;
import com.tech.cadt.repository.ShippingRepository;

@RestController
@RequestMapping("/shipping")
@CrossOrigin
public class ShippingServiceController {
	
	@Autowired
	ShippingRepository shippingRepository;
	
	@RequestMapping(value="/create", method = RequestMethod.POST)
	@CrossOrigin
	public Shipping createShipping(@RequestBody Shipping shipping,HttpServletResponse httpServletResponse,@RequestHeader HttpHeaders headers,HttpServletRequest httpServletRequest) {
		
		List<String> token = headers.get("jwtToken");
		String ipAddressOrder =  httpServletRequest.getRemoteAddr();
		
		Map mapValue=new HashMap();		
		mapValue.put("token", token);		
		mapValue.put("ipAddressOrder", ipAddressOrder);		
	
		RestTemplate restTemplate = new RestTemplate();		 
		String res=restTemplate.postForObject("http://localhost:8222/loginService/isValid", mapValue, String.class);
		
		System.out.print("inside create");
		
		if(res.equals("validToken")){
			
		shipping = shippingRepository.save(shipping);
		}
	    return shipping;
	}
	
	
	@RequestMapping("/getShippingStatus")
	@CrossOrigin
	public Shipping getShippingStatus(@RequestParam long orderId) {
		
		System.out.print("inside get");
		
		Shipping shipping = shippingRepository.findByOrderId(orderId);
	    return shipping;
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(ShippingServiceController.class);
    private static final String signingKey = "signingKey";
	
	@Consumes(MediaType.APPLICATION_JSON)
	@RequestMapping(value="/newShipping", method = RequestMethod.POST)
	@CrossOrigin
	@ResponseBody
	public String createnewShipping(HttpServletResponse httpServletResponse,@RequestHeader HttpHeaders headers,HttpServletRequest httpServletRequest) {


		String ipAddressShipping =  httpServletRequest.getRemoteAddr();
    	System.out.println("IP Address: "+ipAddressShipping);
    	
		    ClientConfig clientConfig = new ClientConfig();
		
			clientConfig.getGroupConfig().setName("dev").setPassword("dev-pass");
			clientConfig.getNetworkConfig().addAddress("172.18.32.189:5701", "172.18.32.189:5702");
		
			HazelcastInstance client = HazelcastClient.newHazelcastClient(clientConfig);
			IMap map = client.getMap( "jwttoken" );	
 
			
			
	    	 System.out.println( "Token" + map.values() );
	    	 System.out.println("key"+map.keySet().toString());
			
	    	String authvalue= map.keySet().toString();
	    	String  result = authvalue.replaceAll("[\\[\\]]","");
	    	System.out.println("authvalue--  :"+result);
			
		
		    LOGGER.info("Inside createnewShipping() new Shipping Details::");
		    RestTemplate restTemplate = new RestTemplate();
		    Order resultObj = null ;
	
			
			String usernameVal = null;
		
			LOGGER.info("Headers::::::" + headers);
			List<String> token = headers.get("jwtToken");
			System.out.println("JWT token:::::" + token);
			LOGGER.info("JWT token:::::" + token.get(0));
			String listObj = token.get(0);
			String jwtUsername = JwtUtil.getSubject(httpServletResponse,listObj, signingKey);
			String jwtPassword = JwtUtil.getPassword(httpServletResponse,listObj, signingKey);
			LOGGER.info("::::::Jwt User credentials after Parsing:::::");
			LOGGER.info("username::" + jwtUsername);
			LOGGER.info("password::" + jwtPassword);

		
			LOGGER.info("Customer Object from Customer Service ::::");
		
			 String valueShipping = new StringBuilder(ipAddressShipping).append("-").append(jwtUsername).toString();
			    
			    System.out.println("valueShipping----"+valueShipping);
			    
			try {
				
				boolean validation=false;
				
					
				if (jwtUsername != null) {
					
					
					if ((token.equals(map.values())) && (result.equals(valueShipping)) ) {
						validation = true;
						
						LOGGER.info("JWT Token Validataion Sucessful in Shipping Service.");
						return "if!"+token;
					} 
					else if(result.equals(null)||result.equals("")){
						
						System.out.println("inside result null");
						return result;
					}
					else
					{
						
					
						LOGGER.info("JWT Token Validataion failed in Shipping Service.");
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