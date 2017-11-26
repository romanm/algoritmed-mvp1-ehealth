package org.algoritmed.mvp1.ehealth;

import java.io.IOException;
import java.security.Principal;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class DivisionRegistryRest extends OAuthRestCommon {
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
			String mapToString = byteToBase64String(fileBytes);
//			String mapToString = file.getBytes().toString();
			ResponseEntity<String> divisionRegistryEntity = restTemplate.exchange(uri
					, HttpMethod.POST, new HttpEntity(mapToString, headers), String.class);
			System.err.println("-----------------45---------divisionRegistryEntity------");
			System.err.println(divisionRegistryEntity.getStatusCode());
			System.err.println(divisionRegistryEntity.getStatusCodeValue());
			System.err.println(divisionRegistryEntity.getBody());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return "redirect:/v/admin-msp";
	}

}
