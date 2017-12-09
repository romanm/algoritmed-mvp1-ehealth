package org.algoritmed.mvp1.ehealth;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/eh")
public class ReadEHealthRest extends RestTemplateCommon{

	private Map<String, Object> readEHealth(String add_uri, Principal principal, HttpServletRequest request) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("add_uri", add_uri);
		map.put("requestURI", request.getRequestURI());
		
		String path_uri = ""+map.get("add_uri");
		if(null!=request.getQueryString()) {
			map.put("uri_query", request.getQueryString());
			path_uri += "?"+request.getQueryString();
		}
		String api_uri = domain_registry  + path_uri;
		map.put("api_uri", api_uri);

		logger.info("-------37-----------"
			+ "\n" + map
			);

		Map<String, Object> principalMap = super.principal(principal);
		String msp_access_token = ""+principalMap.get("msp_access_token");
		HttpHeaders headers = getRestTemplateHeader(msp_access_token);
		ResponseEntity<Map> responseEntity = restTemplate.exchange(api_uri
				, HttpMethod.GET, new HttpEntity(headers), Map.class);
		
		map.put("response", responseEntity.getBody());
		map.put("status_code", responseEntity.getStatusCode());
		
		return map;
	}
	
	private @Value("${config.domain_registry0}")			String domain_registry;
	@GetMapping(value = "/{u1}/{u2}/{u3}")
	public @ResponseBody Map<String, Object>  u3(
			@PathVariable String u1
			,@PathVariable String u2
			,@PathVariable String u3
			,HttpServletRequest request
			,Principal principal
			) {
		return readEHealth("/"+u1+"/"+u2+"/"+u3, principal, request);
	}
	@GetMapping(value = "/{u1}/{u2}")
	public @ResponseBody Map<String, Object>  u2(
			@PathVariable String u1
			,@PathVariable String u2
			,HttpServletRequest request
			,Principal principal
			) {
		return readEHealth("/"+u1+"/"+u2, principal, request);
	}
	@GetMapping(value = "/{u1}")
	public @ResponseBody Map<String, Object>  u1(@PathVariable String u1
			,HttpServletRequest request
			,Principal principal
			) {
		return readEHealth("/"+u1, principal, request);
	}

}
