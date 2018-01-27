package org.algoritmed.mvp1.ehealth;

import java.security.Principal;
import java.util.Calendar;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class EmployeeRegistryRest  extends RestTemplateCommon {

	@PostMapping("/mspEmployeeToEHealth")
	public @ResponseBody Map<String, Object> mspDivisionToEHealth(
			@RequestBody Map<String, Object> data
			,HttpServletRequest request
			,Principal principal
			) {
		long timeInMillis = Calendar.getInstance().getTimeInMillis();
		data.put("timeInMillis", timeInMillis);
		String uri = env.getProperty("config.path_registry_msp_employee");
		System.err.println("----29--------- uri = "+uri);
		Map<String, Object> a6 = (Map<String, Object>) data.get("to_eHealth");
		System.err.println("------31------------");
		System.err.println(a6);
		Map<String, Object> registryDivisionInEHealth = registryEmployeeInEHealth(uri, principal, a6);
		return registryDivisionInEHealth;
	}
	
	private Map<String, Object> registryEmployeeInEHealth(String uri, Principal principal, Map<String, Object> data) {
		HttpHeaders headers = getHead(principal);
		
		ResponseEntity<Map> ehRegistryEntity = restTemplate.exchange(uri
				, HttpMethod.POST, new HttpEntity(data, headers), Map.class);
		System.err.println("-----------------45---------ehRegistryEntity------");
		System.err.println(ehRegistryEntity.getStatusCode());
		System.err.println(ehRegistryEntity.getStatusCodeValue());
		
		Map<String, Object> ehealth_response = ehRegistryEntity.getBody();

		return ehealth_response;
	}

	private HttpHeaders getHead(Principal principal) {
		Map<String, Object> principalMap = super.principal(principal);
		String msp_access_token = ""+principalMap.get("msp_access_token");
		
		System.err.println("----38--------------msp_access_token----------");
		System.err.println(msp_access_token);
		HttpHeaders headers = getRestTemplateHeader(msp_access_token);
		return headers;
	}

	private Map<String, Object> registryEmployeeInEHealth2(String uri, Principal principal, Map<String, Object> data) {
		HttpHeaders headers = getHead(principal);
		String employee_mapToString = mapToString(data);
		System.err.println("-------------65------");
		System.err.println(employee_mapToString);
		return data;
	}
}
