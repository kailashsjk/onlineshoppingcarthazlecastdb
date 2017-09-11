package com.techm.auth.jwt;


import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;




@RestController
@RequestMapping(value="/loginService")
@Component
@CrossOrigin

public class GenerateJwtToken {	
	
	

@RequestMapping(value= "/isValid")
@CrossOrigin
public String isValid(@RequestBody Map jwt ,HttpServletResponse httpServletResponse){
	
	List<String> token=(List<String>) jwt.get("token");
	
	String ipAddressOrder=(String) jwt.get("ipAddressOrder");

	//String ipAddressOrder =  httpServletRequest.getRemoteAddr();
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
					result="validToken";
					LOGGER.info("JWT Token Validataion Sucessful in Order Service.");
					return result;
				} 
				else if(result.equals(null)||result.equals("")){
					result="notValid";
					System.out.println("inside result null");
					return result;
				}
				else
				{
					
					result="notValid";
					LOGGER.info("JWT Token Validataion failed in Order Service.");
					validation = false;
					return result;
				}
			} 
			else {
				result="username is not valid";
				validation = false;
				return result;
			}

			
			
		} catch (Exception e) {
			
		}
		
	return result;	
			
	
	
}

	
	
    private static final String signingKey = "signingKey";
    String token = null ;
    private static final Logger LOGGER = LoggerFactory.getLogger(GenerateJwtToken.class);
    String customlogger ="ADMS Loger::::";
   
	@RequestMapping(value="/generateToken",method = RequestMethod.POST)
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@CrossOrigin
	public String generateToken(HttpServletResponse httpServletResponse,@RequestBody String userCredentials,HttpServletRequest httpServletRequest) {
    
    	String ipAddress =  httpServletRequest.getRemoteAddr();
    	System.out.println("IP Address: "+ipAddress);
    	
    	//Hazelcast instance
        ClientConfig clientConfig = new ClientConfig();
		clientConfig.getGroupConfig().setName("dev").setPassword("dev-pass");
		clientConfig.getNetworkConfig().addAddress("172.18.32.189:5701", "172.18.32.189:5702");
		
		
		
	    HazelcastInstance client = HazelcastClient.newHazelcastClient(clientConfig);
	    
	    
        IMap<String, String> jwttoken = client.getMap("jwttoken");
   	
    	
        jwttoken.evictAll();
   	
       
		LOGGER.info(customlogger+"GenerateJwtToken Started");
		LOGGER.info(customlogger+"Loggedin User Credentials ::" + userCredentials);
		JSONObject userCredjson = null;
		String username = null;
		String password = null;
		try {
			if (userCredentials != null)
				userCredjson = new JSONObject(userCredentials.toString());
			if (userCredjson != null) {
				username = userCredjson.optString("username");
				password = userCredjson.optString("password");
				LOGGER.info(customlogger+"Entered username ::"+username+"\t Entered password ::"+password);
				if(username !=null)
				token = JwtUtil.generateToken(signingKey, username,password);
				LOGGER.info(customlogger+"Generated JwtToken::: " +token);
				System.out.println("success");
				String value = new StringBuilder(ipAddress).append("-").append(username).toString();
				System.out.println(value);
				jwttoken.put(value,token);
			}
		} catch (Exception e) {
			LOGGER.error(customlogger+"Exception Occured in generateToken() method");
			e.printStackTrace();
		}

		return token;
	}
	
	
@RequestMapping(value= "/test")
	public String getmessage(){
		
		return "sucess";
	}

}
