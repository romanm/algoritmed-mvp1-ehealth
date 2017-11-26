package org.algoritmed.mvp1.ehealth;

import java.io.IOException;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.multipart.MultipartFile;

//@RestController redirect:* not work
@Controller
@RequestMapping(value = "${config.security_prefix}")
public class LegalEntityRegistryRest extends OAuthRestCommon {

	@PostMapping("/msp_uploadP7sFile")
	public String handleFileUpload(@RequestParam("file") MultipartFile file,
			@RequestParam("doc_id") String doc_id,
			@RequestParam("uri_prop") String uri_prop,
			Principal principal) {
		
		String uri = env.getProperty(uri_prop);
		logger.info("--------44-----------"
				+ "\n" + "/msp_uploadP7sFile" 
				+ "\n doc_id = " + doc_id
				+ "\n uri_prop = " + uri_prop
				+ "\n uri = " + uri
				+ "\n" + file);
		String token_bearer = env.getProperty("config.token_bearer");
		
		// not correct work with 4xx http code, no body
		restTemplate(file, doc_id, uri, token_bearer);
		

		
		
		return "redirect:/v/admin-msp";
	}

	private void restTemplate(MultipartFile file, String doc_id, String uri, String token_bearer) {
		HttpHeaders headers = getRestTemplateHeader(token_bearer);
		
		try {
			byte[] fileBytes = file.getBytes();
			String mapToString = byteToBase64String(fileBytes);
			ResponseEntity<String> legalEntityRegistryEntity = restTemplate.exchange(uri
					, HttpMethod.PUT, new HttpEntity(mapToString, headers), String.class);
			System.err.println("-----------------42-------legalEntityRegistryEntity------");
			System.err.println(legalEntityRegistryEntity.getStatusCode());
			System.err.println(legalEntityRegistryEntity.getStatusCodeValue());
			String legal_entities_response_body = legalEntityRegistryEntity.getBody();
			System.err.println(legal_entities_response_body);
			
			saveResponse(legal_entities_response_body, doc_id);

		} catch (HttpClientErrorException ce) {
			System.err.println(ce.getStatusCode());
			System.err.println(ce.getRawStatusCode());
			System.err.println(ce.getStatusText());
			System.err.println(ce.getMessage());
			System.err.println("-----70----------------------");
			String legal_entities_response_body = ce.getResponseBodyAsString();
			System.err.println(legal_entities_response_body);
			saveResponse(legal_entities_response_body, doc_id);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void saveResponse(String legal_entities_response_body, String doc_id) {
		System.err.println("--------------46-----------saveResponse------");
		System.err.println(doc_id);
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("MSP_EHEALT_RESPONSE_type", DocType.MSP_EHEALT_RESPONSE.id());
		paramMap.put("msp_id", doc_id);
		System.err.println(paramMap);
		List<Map<String, Object>> mspEhalethResponseList = 
				db1ParamJdbcTemplate.queryForList(env.getProperty("sql.msp.msp_ehealth_response.select"), paramMap);
		System.err.println(mspEhalethResponseList);
		Integer MSP_EHEALT_RESPONSE_id;
		String sql;
		if(mspEhalethResponseList.size()>0){//update
			paramMap.put("sql", "sql.msp.msp_ehealth_response.update");
			Map<String, Object> map = mspEhalethResponseList.get(0);
			MSP_EHEALT_RESPONSE_id = (Integer) map.get("doc_id");
		}else{//insert
			paramMap.put("sql", "sql.msp.msp_ehealth_response.insert");
			MSP_EHEALT_RESPONSE_id = nextDbId();
			paramMap.put("doc_id", MSP_EHEALT_RESPONSE_id);
			paramMap.put("doctype", DocType.MSP_EHEALT_RESPONSE.id());
			paramMap.put("parent_id", doc_id);
			paramMap.put("docbody_id", MSP_EHEALT_RESPONSE_id);
			
			paramMap.put("MSP_CLIENT_ID_type", DocType.MSP_CLIENT_ID.id());
			Integer MSP_CLIENT_ID_doc_id = nextDbId();
			paramMap.put("MSP_CLIENT_ID_doc_id", MSP_CLIENT_ID_doc_id);
			Map<String, Object> legal_entities_response_map = stringToMap(legal_entities_response_body);
			String msp_client_id = mapUtil.getString(legal_entities_response_map, "urgent", "security", "client_id");
			System.err.println("-----78-----------msp_client_id = "+msp_client_id);
			paramMap.put("msp_client_id", msp_client_id);
		}
		System.err.println(paramMap);
		paramMap.put("docbody", legal_entities_response_body);
		System.err.println(legal_entities_response_body);
		update_sql_script(paramMap);
	}
}
