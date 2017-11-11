package org.algoritmed.mvp1.ehealth1.rest;

import java.util.Arrays;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class EHealth1Common {
	protected Map<String, Object> getResponseBody(String uri) {
		ResponseEntity<Map> personEntity = restTemplate.exchange(uri, HttpMethod.GET, new HttpEntity(getRestTemplateHeader()), Map.class);
		return (Map<String, Object>) personEntity.getBody();
	}
	protected HttpHeaders getRestTemplateHeader() {
		HttpHeaders headers = new HttpHeaders();
	    headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
//	    addNoCache(headers);
	    headers.add("Authorization", "Bearer "+env.getProperty("config.token_bearer"));
		return headers;
	}
	private void addNoCache(HttpHeaders headers) {
		headers.add("cache-control", "no-cache");
	}
	protected @Autowired RestTemplate restTemplate;
	@Autowired protected Environment env;
	protected static final Logger logger = LoggerFactory.getLogger(EHealth1Common.class);
}
