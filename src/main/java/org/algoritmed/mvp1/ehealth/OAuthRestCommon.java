package org.algoritmed.mvp1.ehealth;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.algoritmed.mvp1.DbAlgoritmed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class OAuthRestCommon  extends DbAlgoritmed{
	protected static final Logger logger = LoggerFactory.getLogger(OAuthRestCommon.class);

	@Autowired ObjectMapper mapper = new ObjectMapper();
	protected @Autowired RestTemplate restTemplate;
	
	protected String getBodyForOAuthTokenRequest(String code) {
		Map<String, Object> oauth_tokenMap = getBodyMapForOAuthTokenRequest(code);
		String oauth_tokens_body = mapToString(oauth_tokenMap);
		return oauth_tokens_body;
	}

	protected String mapToString(Map<String, Object> oauth_tokenMap) {
		String oauth_tokens_body = "";
		try {
			oauth_tokens_body = mapper.writeValueAsString(oauth_tokenMap);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return oauth_tokens_body;
	}

	protected Map<String, Object> getBodyMapForRefreshAccessTokenRequest(String refresh_token) {
		Map<String, Object> oauth_tokenMap = new HashMap<String, Object>();
		Map<String, Object> tokenMap = new HashMap<String, Object>();
		tokenMap.put("client_id", "bf48fba2-e4e8-4a06-aeaa-345d8346d7bb");
		tokenMap.put("grant_type", "refresh_token");
		tokenMap.put("client_secret", "ZHdqTTVGWjYrMFRRa0hoYmpGVTFldz09");
		tokenMap.put("refresh_token", refresh_token);
		oauth_tokenMap.put("token", tokenMap);
		return oauth_tokenMap;
	}
	protected Map<String, Object> getBodyMapForOAuthTokenRequest(String code) {
		Map<String, Object> oauth_tokenMap = new HashMap<String, Object>();
		Map<String, Object> tokenMap = new HashMap<String, Object>();
		tokenMap.put("client_id", "bf48fba2-e4e8-4a06-aeaa-345d8346d7bb");
		tokenMap.put("client_secret", "ZHdqTTVGWjYrMFRRa0hoYmpGVTFldz09");
		tokenMap.put("grant_type", "authorization_code");
		tokenMap.put("code", code);
//		tokenMap.put("redirect_uri", "https://medic.algoritmed.com"+env.getProperty("config.security_prefix")+"/from_oauth_tokens");
		tokenMap.put("redirect_uri", "https://medic.algoritmed.com"+env.getProperty("config.security_prefix")+"/to_oauth_tokens");
		tokenMap.put("scope", "employee:read employee:write"
				+ " employee_request:approve employee_request:read employee_request:write employee_request:reject"
				+ " legal_entity:read"
				+ " division:read division:write"
				+ " declaration_request:write declaration_request:read employee:deactivate"
				+ " otp:read otp:write ");
		oauth_tokenMap.put("token", tokenMap);
		return oauth_tokenMap;
	}
	
	protected void runBashCommand(String bashCommand) {
		String s;
        Process p;
        try {
            p = Runtime.getRuntime().exec(bashCommand);
            BufferedReader br = new BufferedReader(
                new InputStreamReader(p.getInputStream()));
            System.out.println("result");
            while ((s = br.readLine()) != null)
                System.out.println(s);
            System.out.println("result END --------------------------");
            p.waitFor();
            System.out.println ("\n exit: " + p.exitValue());
            p.destroy();
        } catch (Exception e) {}
	}
	
	protected HttpHeaders getRestTemplateHeader2() {
		HttpHeaders headers = new HttpHeaders();
	    headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
	    //client_secret
	    headers.add("api-key", env.getProperty("config.mis_api_key"));
	    String token_bearer = env.getProperty("config.token_bearer");
		headers.add("Authorization", "Bearer "+token_bearer);
		return headers;
	}
	protected HttpHeaders getRestTemplateHeader(String token_bearer) {
		HttpHeaders headers = new HttpHeaders();
//		headers.add("Content-Type", "application/json");
	    headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
//	    headers.add("cache-control", "no-cache");
	    headers.add("api-key", env.getProperty("config.mis_api_key"));
//		headers.add("api-key", env.getProperty("config.mis_client_secret_client_id"));
		headers.add("Authorization", "Bearer "+token_bearer);
	    System.err.println("headers--------------107---------------");
	    System.err.println(headers);
		return headers;
	}
	
	protected String byteToBase64String(byte[] fileBytes) {
		Map<String, Object> map = new HashMap<>();
		map.put("signed_content_encoding", "base64");
		String encodeToString = Base64.getEncoder().encodeToString(fileBytes);
		map.put("signed_legal_entity_request", encodeToString);
		String mapToString = mapToString(map);
		return mapToString;
	}
	
	protected HttpHeaders getRestTemplateHeader_to_delete() {
		String token = env.getProperty("config.token_bearer");
		return getRestTemplateHeader(token);
	}

	@Autowired protected Environment env;
	
	protected void testCURL(String oauth_tokens_body) {
		System.err.println("---------------");
		String bashCommand = "curl -X POST "
//				+ uri_oauth2_code_grant
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
	}
	
	private String bashCommand = "curl -X POST "
//			+ uri_oauth2_code_grant
			+ " "
			+ " -H  'cache-control: no-cache' "
			+ " -H 'content-type: application/json' "
			//				+ "-H 'postman-token: 560ff187-848c-467a-d1b5-d4383ecfa911' \\ \n"
			+ " -d '"
//			+ oauth_tokens_body
			+ "'"
			+ "";
}
