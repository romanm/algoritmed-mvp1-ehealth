package org.algoritmed.mvp1.medic;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class OAuthRestCommon {
	@Autowired ObjectMapper mapper = new ObjectMapper();
	String uri = "https://api.ehealth.world/oauth/tokens/";
	protected String getBodyForOAuthTokenRequest(String code) {
		Map oauth_tokenMap = getBodyMapForOAuthTokenRequest(code);
		String oauth_tokens_body = mapToString(oauth_tokenMap);
		return oauth_tokens_body;
	}

	protected String mapToString(Map oauth_tokenMap) {
		String oauth_tokens_body = "";
		try {
			oauth_tokens_body = mapper.writeValueAsString(oauth_tokenMap);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return oauth_tokens_body;
	}

	protected Map getBodyMapForOAuthTokenRequest(String code) {
		Map oauth_tokenMap = new HashMap<>();
		Map tokenMap = new HashMap<>();
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
	
}
