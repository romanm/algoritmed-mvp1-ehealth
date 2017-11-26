package org.algoritmed.mvp1.ehealth;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

//@RestController redirect:* not work
@Controller
@RequestMapping(value = "${config.security_prefix}")
public class OAuthRest2 extends OAuthRestCommon {

	@GetMapping(value = "/to_oauth_tokens")
	public String  to_oauth_tokens(@RequestParam("code") String code){
//		public String  to_oauth_tokens(@RequestParam("code") String code, HttpServletResponse response){
		String uri_oauth_token = env.getProperty("config.uri_oauth2_code_grant");
		logger.info("\n ------26------/to_oauth_tokens---"
				+ "\n uri_oauth_token = "+uri_oauth_token
				);
 
		Map bodyMapForOAuthTokenRequest = getBodyMapForOAuthTokenRequest(code);
		
//	    System.err.println("bodyMapForOAuthTokenRequest------40-------");
//	    System.err.println(mapToString(bodyMapForOAuthTokenRequest));
		Map oauthTokenEntity = restTemplate.postForObject(uri_oauth_token
	    		, bodyMapForOAuthTokenRequest, Map.class);
		// :-) xPath: /data/details/@refresh_token
	    String refresh_token = mapUtil.getString(oauthTokenEntity
	    		, "data","details","refresh_token");
	    logger.info("\n ------40------/to_oauth_tokens---"
	    		+ "\n refresh_token = "+refresh_token
	    		);
	    Map<String, Object> bodyMapForRefreshAccessTokenRequest = getBodyMapForRefreshAccessTokenRequest(refresh_token);
//	    System.err.println("bodyMapForRefreshAccessTokenRequest------48---------");
//	    System.err.println(mapToString(bodyMapForRefreshAccessTokenRequest));
	    /*
	    oauthTokenEntity = restTemplate.postForObject(uri_oauth2_code_grant
	    		, bodyMapForRefreshAccessTokenRequest, Map.class);
	    System.err.println("response");
	    System.err.println(oauthTokenEntity);
	     * */
//	    HttpHeaders headers = getRestTemplateHeader(refresh_token);
	    String token_bearer = env.getProperty("config.token_bearer");
	    HttpHeaders headers = getRestTemplateHeader(token_bearer);
	    ResponseEntity<Map> accessTokenEntity = restTemplate.exchange(uri_oauth_token
	    		, HttpMethod.POST, new HttpEntity(bodyMapForRefreshAccessTokenRequest, headers), Map.class);
	    Map accessTokenBody = accessTokenEntity.getBody();
//	    String access_token = mapUtil.getString(accessTokenBody, "data","id");
	    String access_token = mapUtil.getString(accessTokenBody, "data","value");
	    
	    logger.info("\n ------61------/to_oauth_tokens---"
	    		+ "\n access_token = "+access_token
	    		+ "\n response body = "
	    		+ "\n "+mapToString(accessTokenBody)
	    		);
	    
	    Map<String, Object> data = new HashMap<String, Object>();
	    System.err.println("--65--save---DB------msp_access_token---");
		Map<String, Object> paramMap = new HashMap<>();

		paramMap.put("sql", "sql.msp.msp_access_token.init");
		String msp_client_id = mapUtil.getString(accessTokenBody,"data","details","client_id");
		paramMap.put("msp_client_id", msp_client_id);
		update_sql_script(paramMap);

		paramMap.put("sql", "sql.doc.doc_docbody_node.insert");
		Integer nextDbId = nextDbId();
		paramMap.put("doc_id", nextDbId);
		paramMap.put("docbody_id", nextDbId);
		paramMap.put("doctype", DocType.msp_access_token_body.id());
		paramMap.put("docbody", mapToString(accessTokenBody));
		update_sql_script(paramMap);
		
		nextDbId = nextDbId();
		paramMap.put("doc_id", nextDbId);
		paramMap.put("docbody_id", nextDbId);
		paramMap.put("doctype", DocType.msp_access_token.id());
		paramMap.put("docbody", access_token);
		update_sql_script(paramMap);
		

	    /*
	    data.put("msp_id", msp_id)
		update_sql_script(data);
	    String uri_oauth2_refresh_tokens = env.getProperty("config.uri_oauth2_refresh_tokens");
	     * */
		return "redirect:/v/admin-msp";
		
	}
	
}
