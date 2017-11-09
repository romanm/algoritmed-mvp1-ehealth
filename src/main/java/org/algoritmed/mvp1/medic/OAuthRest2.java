package org.algoritmed.mvp1.medic;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class OAuthRest2 extends OAuthRestCommon{
	@Autowired ObjectMapper mapper = new ObjectMapper();
	@GetMapping(value = "/r/to_oauth_tokens2")
	public String  to_oauth_tokens(@RequestParam("code") String code, HttpServletResponse response){
		logger.info("---------------\n"
				+ "/r/from_oauth_tokens2"
				+ "\n" 
				+ "\n" +response
				);
		Map bodyMapForOAuthTokenRequest = getBodyMapForOAuthTokenRequest(code);
		
		String oauth_tokens_body = mapToString(bodyMapForOAuthTokenRequest);
		System.err.println("---------------");
		System.err.println(oauth_tokens_body);
		System.err.println("---------------");
		String bashCommand = "curl -X POST "
				+ uri_oauth2_refresh_tokens
				+ " -H 'cache-control: no-cache' "
				+ " -H 'content-type: application/json' "
				//				+ "-H 'postman-token: 560ff187-848c-467a-d1b5-d4383ecfa911' \\ \n"
				+ " -d '"
				+ oauth_tokens_body
				+ "' \n"
				+ "";
		
		System.err.println("---------------");
		System.err.println(bashCommand);
		System.err.println("---------------");
		runBashCommand(bashCommand);
		
		HttpHeaders headers = new HttpHeaders();
	    headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
	    headers.add("cache-control", "no-cache");
	    headers.add("Authorization", "Bearer c490c936651a0f6badeb426721076437");
	    System.err.println(headers);
	    System.err.println(headers.getETag());
	    System.err.println(uri_oauth2_refresh_tokens);
	    System.err.println(31);
	    System.err.println("bodyMapForOAuthTokenRequest");
	    System.err.println(bodyMapForOAuthTokenRequest);
	    
	    Map postForObject = restTemplate.postForObject(uri_oauth2_refresh_tokens, bodyMapForOAuthTokenRequest, Map.class);
	    System.err.println(30);
	    System.err.println("postForObject");
	    System.err.println(postForObject);
	    System.err.println("postForObject END");
		return "redirect:/v/admin-msp";
	}
	
	protected @Autowired RestTemplate restTemplate;
	private static final Logger logger = LoggerFactory.getLogger(OAuthRest2.class);
}
