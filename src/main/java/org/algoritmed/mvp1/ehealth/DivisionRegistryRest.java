package org.algoritmed.mvp1.ehealth;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
		Map<String, Object> principalMap = super.principal(principal);
//		System.err.println(principalMap);
		String msp_access_token = ""+principalMap.get("msp_access_token");

		System.err.println("----34--------------msp_access_token----------");
		System.err.println(msp_access_token);
	    HttpHeaders headers = getRestTemplateHeader(msp_access_token);

		try {
			byte[] fileBytes = file.getBytes();
//			String fileToSaveAsString = byteToBase64String(fileBytes);
//			Map<String, Object> fileToSaveAsMap = stringToMap(fileToSaveAsString);
//			String string = fileBytes.toString();
			String string = new String(file.getBytes(), StandardCharsets.UTF_8);
			logger.info("--------43-----------"
					+ "\n" + string);
			Map<String, Object> fileToSaveAsMap = stringToMap(string);
			ResponseEntity<Map> divisionRegistryEntity = restTemplate.exchange(uri
					, HttpMethod.POST, new HttpEntity(fileToSaveAsMap, headers), Map.class);
//			String mapToString = file.getBytes().toString();
//			ResponseEntity<String> divisionRegistryEntity = restTemplate.exchange(uri
//					, HttpMethod.POST, new HttpEntity(fileToSaveAsString, headers), String.class);
			System.err.println("-----------------45---------divisionRegistryEntity------");
			System.err.println(divisionRegistryEntity.getStatusCode());
			System.err.println(divisionRegistryEntity.getStatusCodeValue());
			System.err.println(divisionRegistryEntity.getBody());
		} catch (HttpClientErrorException ce) {
			System.err.println("-----59------HttpClientErrorException---------------");
			System.err.println(ce.getStatusCode());
			System.err.println(ce.getRawStatusCode());
			System.err.println(ce.getStatusText());
			System.err.println(ce.getMessage());
			String legal_entities_response_body = ce.getResponseBodyAsString();
			System.err.println(legal_entities_response_body);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return "redirect:/v/admin-msp";
	}

}
