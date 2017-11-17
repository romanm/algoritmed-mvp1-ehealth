package org.algoritmed.mvp1.medic;

import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

//@RestController redirect:* not work
@Controller
@RequestMapping(value = "${config.security_prefix}")
public class OAuthRest2 extends OAuthRestCommon{
	@Autowired ObjectMapper mapper = new ObjectMapper();
	@GetMapping(value = "/to_oauth_tokens")
	public String  to_oauth_tokens(@RequestParam("code") String code, HttpServletResponse response){
		logger.info("---------------\n"
				+ "/to_oauth_tokens"
				+ "\n" 
				+ "\n" +response
				+ "\n ------------------" 
				);

		Map bodyMapForOAuthTokenRequest = getBodyMapForOAuthTokenRequest(code);
		
	    String uri_oauth_token = env.getProperty("config.uri_oauth2_code_grant");
	    System.err.println(uri_oauth_token);
	    System.err.println("bodyMapForOAuthTokenRequest");
	    System.err.println(mapToString(bodyMapForOAuthTokenRequest));
		Map oauthTokenEntity = restTemplate.postForObject(uri_oauth_token
	    		, bodyMapForOAuthTokenRequest, Map.class);
	    String refresh_token = mapUtil.getString(oauthTokenEntity, "data","details","refresh_token");
	    System.err.println("refresh_token");
	    System.err.println(refresh_token);
	    Map<String, Object> bodyMapForRefreshAccessTokenRequest = getBodyMapForRefreshAccessTokenRequest(refresh_token);
	    System.err.println("bodyMapForRefreshAccessTokenRequest");
	    System.err.println(mapToString(bodyMapForRefreshAccessTokenRequest));
	    /*
	    oauthTokenEntity = restTemplate.postForObject(uri_oauth2_code_grant
	    		, bodyMapForRefreshAccessTokenRequest, Map.class);
	    System.err.println("response");
	    System.err.println(oauthTokenEntity);
	     * */
//	    HttpHeaders headers = getRestTemplateHeader(refresh_token);
	    HttpHeaders headers = getRestTemplateHeader();
	    System.err.println("headers");
	    System.err.println(headers);
	    ResponseEntity<Map> accessTokenEntity = restTemplate.exchange(uri_oauth_token
	    		, HttpMethod.POST, new HttpEntity(bodyMapForOAuthTokenRequest, headers), Map.class);
	    Map accessTokenBody = accessTokenEntity.getBody();
	    System.err.println("accessTokenBody");
	    System.err.println(accessTokenBody);
	    /*
	    String uri_oauth2_refresh_tokens = env.getProperty("config.uri_oauth2_refresh_tokens");
	     * */
		return "redirect:/v/admin-msp";
		
	}
	
	
	protected @Autowired RestTemplate restTemplate;
	private static final Logger logger = LoggerFactory.getLogger(OAuthRest2.class);
}
