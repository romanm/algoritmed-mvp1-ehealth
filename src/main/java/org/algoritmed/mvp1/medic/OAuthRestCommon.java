package org.algoritmed.mvp1.medic;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class OAuthRestCommon {
	@Autowired ObjectMapper mapper = new ObjectMapper();
	@Value("${config.uri_oauth2_code_grant}")		protected String uri_oauth2_code_grant;
	@Value("${config.uri_oauth2_refresh_tokens}")	protected String uri_oauth2_refresh_tokens;
//	String uri = "https://api.ehealth.world/oauth/tokens/";
//	String uri = "https://demo.ehealth.world/api/oauth/tokens/";
	
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

	protected Map<String, Object> getBodyMapForOAuthTokenRequest(String code) {
		Map<String, Object> oauth_tokenMap = new HashMap<String, Object>();
		Map<String, Object> tokenMap = new HashMap<String, Object>();
		tokenMap.put("grant_type", "authorization_code");
		tokenMap.put("code", code);
		tokenMap.put("client_id", "bf48fba2-e4e8-4a06-aeaa-345d8346d7bb");
		tokenMap.put("client_secret", "ZHdqTTVGWjYrMFRRa0hoYmpGVTFldz09");
		tokenMap.put("redirect_uri", "https://medic.algoritmed.com/r/from_oauth_tokens");
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
	
	protected HttpHeaders getRestTemplateHeader() {
		HttpHeaders headers = new HttpHeaders();
	    headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
	    headers.add("cache-control", "no-cache");
	    headers.add("Authorization", "Bearer "+env.getProperty("config.token_bearer"));
		return headers;
	}
	@Autowired protected Environment env;
	
	protected void testCURL(String oauth_tokens_body) {
		System.err.println("---------------");
		String bashCommand = "curl -X POST "
				+ uri_oauth2_code_grant
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
			+ uri_oauth2_code_grant
			+ " "
			+ " -H  'cache-control: no-cache' "
			+ " -H 'content-type: application/json' "
			//				+ "-H 'postman-token: 560ff187-848c-467a-d1b5-d4383ecfa911' \\ \n"
			+ " -d '"
//			+ oauth_tokens_body
			+ "'"
			+ "";
}
