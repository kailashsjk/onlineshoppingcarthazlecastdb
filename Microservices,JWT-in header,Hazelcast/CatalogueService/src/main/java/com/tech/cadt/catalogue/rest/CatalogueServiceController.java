package com.tech.cadt.catalogue.rest;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.config.Config;
import com.hazelcast.config.XmlConfigBuilder;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
//import com.tech.cadt.cache.rest.HazelCastServiceController;


import com.tech.cadt.CatalogueService;
import com.tech.cadt.product.entity.Product;
import com.tech.cadt.product.repository.ProductRepository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/catalogueService")
@Component
@CrossOrigin
public class CatalogueServiceController extends SpringBootServletInitializer implements Serializable{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CatalogueServiceController.class);
	String customlogger ="ADMS Loger::::";
	
	
	
    private static final String signingKey = "signingKey";
	
    @Autowired 
	ProductRepository productRepository;
	


	@RequestMapping("/create")
	public Product create(@Valid @RequestBody Product product) {
		
		
		
		try{				
			System.out.println("Product"+product.getCost());
			product = productRepository.save(product);	
		System.out.println("Product"+product);
		
	}catch(Exception e){
		System.out.println("Connection Exception"+e.getMessage());
	}
	    return product;
	}
	
	
	@RequestMapping(value = "/update", method = RequestMethod.POST)
	@CrossOrigin
	public Product update(@Valid @RequestBody Product product,HttpServletResponse httpServletResponse,@RequestHeader HttpHeaders headers,HttpServletRequest httpServletRequest) {
		Product products=null;
		
		List<String> token = headers.get("jwtToken");
		String ipAddressOrder =  httpServletRequest.getRemoteAddr();
		
		Map mapValue=new HashMap();		
		mapValue.put("token", token);		
		mapValue.put("ipAddressOrder", ipAddressOrder);		
	
		RestTemplate restTemplate = new RestTemplate();		 
		String res=restTemplate.postForObject("http://localhost:8222/loginService/isValid", mapValue, String.class);
		
		System.out.print("inside create");
		
		if(res.equals("validToken")){
			products= productRepository.findById(product.getId());
		
		products.setAvailableitems(product.getAvailableitems());
		
		products = productRepository.save(products);		
		}
	    return products;
	}
	
	
	@RequestMapping("/read-all")
	public  List<Product> readAll() throws InterruptedException {
		List<Product> product = productRepository.findAll();		
		 return product;
		
		}
	
	
 	
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	@CrossOrigin
	@ResponseBody

	public  String authenticateCustomer(HttpServletResponse httpServletResponse, @RequestHeader HttpHeaders headers,@RequestBody String userCredentials,HttpServletRequest httpServletRequest) {
		
		
		String ipAddressCatalogue =  httpServletRequest.getRemoteAddr();
    	System.out.println("IP Address: "+ipAddressCatalogue);
    	
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
			
		  
			
        
		LOGGER.info(customlogger+"userCredentials::" + userCredentials);
		JSONObject userCredjson = null;
		CatalogueService customerObj = null;
		String username = null;
		CatalogueService customerresultObj = null ;
		boolean validation =false;
		LOGGER.info(customlogger+"Header Object in Customer Service::::::"+headers);
		List<String> token = headers.get("jwtToken");
		LOGGER.info("JWT token:::::"+token.get(0));
		String listObj = token.get(0);
	    String jwtUsername = JwtUtil.getSubject(httpServletResponse, listObj, signingKey);
	    String jwtPassword = JwtUtil.getPassword(httpServletResponse, listObj, signingKey);
	    LOGGER.info(customlogger+"Jwt User credentials after Parsing:::::");
	    LOGGER.info(customlogger+"username::"+jwtUsername);
	    LOGGER.info(customlogger+"password::"+jwtPassword);
	    System.out.println("token"+token);
	    
	    String valueCatalogue = new StringBuilder(ipAddressCatalogue).append("-").append(jwtUsername).toString();
	    
	    
	    System.out.println("valueCatalogue----"+valueCatalogue);
	    
		try {
			
			if (jwtUsername != null) {
				
				if ((token.equals(map.values())) && (result.equals(valueCatalogue)) ) {
					validation = true;
					
					LOGGER.info("JWT Token Validataion Sucessful in Catalogue Service.");
					return "if!"+token;
				} 
				else if(result.equals(null)||result.equals("")){
					
					System.out.println("inside result null");
					return result;
				}
				else
				{
					
				
					LOGGER.info("JWT Token Validataion failed in Catalogue Service.");
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
