package org.algoritmed.mvp1.ehealth;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class DivisionRegistryRest extends RestTemplateCommon {
	@PostMapping("/msp_upload_division_P7sFile")
	public String handleDivisionFileUpload(@RequestParam("file") MultipartFile file,
			@RequestParam("doc_id") String doc_id,
			@RequestParam("uri_prop") String uri_prop,
			Principal principal) {
		String uri = env.getProperty(uri_prop);
		
		logger.info("--------27-----------"
				+ "\n" + "/msp_upload_division_P7sFile" 
				+ "\n doc_id = " + doc_id
				+ "\n uri_prop = " + uri_prop
				+ "\n uri = " + uri
				+ "\n" + file);
		Map<String, Object> fileToSaveAsMap =  null;
	    try {
			byte[] fileBytes = file.getBytes();
			String string = new String(file.getBytes(), StandardCharsets.UTF_8);
			logger.info("--------43-----------"
					+ "\n" + string);
			fileToSaveAsMap = stringToMap(string);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

	    if(fileToSaveAsMap !=  null && !fileToSaveAsMap.isEmpty()) {
	    	registryDivisionInEHealth(uri, principal, fileToSaveAsMap);
	    }
		
		return "redirect:/v/admin-msp";
	}
	

	@PatchMapping(value = "/mspDivisionToEHealth/deactivate/{division_id}")
	public @ResponseBody Map<String, Object>  mis_division_deactivate(@PathVariable String division_id) {
		logger.info("---------------\n"
				+ "/mspDivisionToEHealth/deactivate/{division_id}"
				+ "\n" +division_id
				);
		return null;
	}

	
	@PostMapping("/mspDivisionToEHealth")
	public @ResponseBody Map<String, Object> mspDivisionToEHealth(
			@RequestBody Map<String, Object> data
			,HttpServletRequest request
			,Principal principal
		) {
		logger.info("\n-------60-----\n"
				+ "/mspDivisionToEHealth"
				+ "\n"+data
				+ "\n"+data.get("id")
				);
		
		Map<String, Object> registryDivisionInEHealth = new HashMap<String, Object>();
		
		if(data.containsKey("id")) {//update
			System.err.println("update");
		}else {//insert
			String uri = env.getProperty("config.path_registry_msp_division");
			System.err.println("insert "+uri);
			registryDivisionInEHealth = registryDivisionInEHealth(uri, principal, data);
		}
		return registryDivisionInEHealth;
	}

	private Map<String, Object> registryDivisionInEHealth(String uri, Principal principal, Map<String, Object> fileToSaveAsMap) {
		Map<String, Object> principalMap = super.principal(principal);
//		System.err.println(principalMap);
		String msp_access_token = ""+principalMap.get("msp_access_token");
		
		System.err.println("----34--------------msp_access_token----------");
		System.err.println(msp_access_token);
		HttpHeaders headers = getRestTemplateHeader(msp_access_token);
		
		Map<String, Object> division_ehealth_response;
		try {
			//			String fileToSaveAsString = byteToBase64String(fileBytes);
			//			Map<String, Object> fileToSaveAsMap = stringToMap(fileToSaveAsString);
			//			String string = fileBytes.toString();
			ResponseEntity<Map> divisionRegistryEntity = restTemplate.exchange(uri
					, HttpMethod.POST, new HttpEntity(fileToSaveAsMap, headers), Map.class);
			//			String mapToString = file.getBytes().toString();
			//			ResponseEntity<String> divisionRegistryEntity = restTemplate.exchange(uri
			//					, HttpMethod.POST, new HttpEntity(fileToSaveAsString, headers), String.class);
			System.err.println("-----------------45---------divisionRegistryEntity------");
			System.err.println(divisionRegistryEntity.getStatusCode());
			System.err.println(divisionRegistryEntity.getStatusCodeValue());
			division_ehealth_response = divisionRegistryEntity.getBody();
		} catch (HttpClientErrorException ce) {
			System.err.println("-----59------HttpClientErrorException---------------");
			System.err.println(ce.getStatusCode());
			System.err.println(ce.getRawStatusCode());
			System.err.println(ce.getStatusText());
			System.err.println(ce.getMessage());
			String division_response_body = ce.getResponseBodyAsString();
			System.err.println(division_response_body);
			division_ehealth_response = stringToMap(division_response_body);
		}
		return division_ehealth_response;
	}

}
