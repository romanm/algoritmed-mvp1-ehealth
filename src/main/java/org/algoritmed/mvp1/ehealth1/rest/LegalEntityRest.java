package org.algoritmed.mvp1.ehealth1.rest;

import java.util.Map;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping(value = "${config.security_prefix}/eh1cc/api/legal_entities")
public class LegalEntityRest extends EHealth1Common{
	
	@PatchMapping(value = "/{legal_entities_id}/actions/mis_verify")
	public @ResponseBody Map<String, Object>  mis_verified(@PathVariable String legal_entities_id) {
		String uri = env.getProperty("config.uri_registry_legal_entities")
				+ legal_entities_id
				+ "/actions/mis_verify";
		logger.info("---------------\n"
				+ "/eh1cc/api/legal_entities/{legal_entities_id}/actions/mis_verify"
				+ "\n" +legal_entities_id
				+ "\n" +uri
				);
		/*
		ResponseEntity<Map> personEntity = restTemplate.exchange(uri, HttpMethod.PUT, new HttpEntity(getRestTemplateHeader()), Map.class);
		ResponseEntity<Map> personEntity = restTemplate.exchange(uri, HttpMethod.PATCH, new HttpEntity(getRestTemplateHeader()), Map.class);
		 * */
		HttpHeaders patchRestTemplate = getPatchRestTemplateHeader();
		System.err.println(40);
		System.err.println(patchRestTemplate);
		ResponseEntity<String> personEntity = getPatchRestTemplate().exchange(uri, HttpMethod.PATCH, new HttpEntity(patchRestTemplate), String.class);
		System.err.println(personEntity);
		System.err.println(41);
		System.err.println(personEntity.getStatusCode());
		System.err.println(43);
		System.err.println(personEntity.getHeaders());
		System.err.println(45);
		System.err.println(personEntity.getStatusCodeValue());
		System.err.println(51);
		System.err.println(personEntity.getBody());
		return null;
		/*
		Entity<String> dataJson = Entity.json("{}");
		Response response = eHealth2Common.getInvocationBuilder(uri).put(dataJson);
		
		System.out.println("status: " + response.getStatus());
		System.out.println("headers: " + response.getHeaders());
		Map readEntity = response.readEntity(Map.class);
		System.out.println("body:" + readEntity);
		
		return null;
		 * */
	}
	@Autowired protected EHealth2Common eHealth2Common;
	@GetMapping
	public @ResponseBody Map<String, Object>  edrpou(@RequestParam("edrpou") String edrpou) {
		String uri = uri_registry_legal_entities+"?edrpou="+edrpou;
		logger.info("---------------\n"
				+ "/eh1cc/api/legal_entities"
				+ "\n" +uri
				+ "\n" +edrpou
				);
		return getResponseBody(uri);
	}
	@GetMapping(value = "/{legal_entities_id}")
	public @ResponseBody Map<String, Object>  legal_entities_by_id(@PathVariable String legal_entities_id) {
		String uri = uri_registry_legal_entities+"/"+legal_entities_id;
		logger.info("---------------\n"
				+ "/eh1cc/api/legal_entities/{legal_entities_id}"
				+ "\n" +uri
				+ "\n" +legal_entities_id
				);
		return getResponseBody(uri);
	}
	
	@Value("${config.uri_registry_legal_entities}")		protected String uri_registry_legal_entities;
}
