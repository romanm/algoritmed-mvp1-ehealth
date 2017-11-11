package org.algoritmed.mvp1.medic;

import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping(value = "${config.security_prefix}")
public class OAuthRest2 extends OAuthRestCommon{
	@Autowired ObjectMapper mapper = new ObjectMapper();
	@GetMapping(value = "/to_oauth_tokens2")
	public String  to_oauth_tokens(@RequestParam("code") String code, HttpServletResponse response){
		logger.info("---------------\n"
				+ "/from_oauth_tokens2"
				+ "\n" 
				+ "\n" +response
				);
		Map bodyMapForOAuthTokenRequest = getBodyMapForOAuthTokenRequest(code);
		
		String oauth_tokens_body = mapToString(bodyMapForOAuthTokenRequest);
		System.err.println("---------------");
		System.err.println(oauth_tokens_body);
//		testCURL(oauth_tokens_body);
		
		HttpHeaders headers = getRestTemplateHeader();
	    System.err.println(headers);
	    System.err.println(uri_oauth2_code_grant);
	    System.err.println(31);
	    System.err.println("bodyMapForOAuthTokenRequest");
	    System.err.println(bodyMapForOAuthTokenRequest);
	    
	    Map postForObject = restTemplate.postForObject(uri_oauth2_code_grant, bodyMapForOAuthTokenRequest, Map.class);
	    System.err.println(30);
	    System.err.println("postForObject");
	    System.err.println(postForObject);
	    System.err.println("postForObject END");
		return "redirect:/v/admin-msp";
	}
	
	
	protected @Autowired RestTemplate restTemplate;
	private static final Logger logger = LoggerFactory.getLogger(OAuthRest2.class);
}
