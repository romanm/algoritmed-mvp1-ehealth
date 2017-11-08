package org.algoritmed.mvp1.medic;

import java.util.Arrays;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

public class OAuthRest2 extends OAuthRestCommon{
	@Autowired ObjectMapper mapper = new ObjectMapper();
	@GetMapping(value = "/r/to_oauth_tokens2")
	public String  to_oauth_tokens(@RequestParam("code") String code){
		HttpHeaders headers = new HttpHeaders();
	    headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
	    headers.add("cache-control", "no-cache");
	    Map bodyMapForOAuthTokenRequest = getBodyMapForOAuthTokenRequest(code);
	    Map postForObject = restTemplate.postForObject(uri, bodyMapForOAuthTokenRequest, Map.class);
	    System.err.println(postForObject);
		return "redirect:/v/admin-msp";
	}
	
	protected @Autowired RestTemplate restTemplate;
	private static final Logger logger = LoggerFactory.getLogger(OAuthRest2.class);
}
