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
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

public class EHealth1Common {
	
	protected Map<String, Object> getResponseBody(String uri) {
		ResponseEntity<Map> personEntity = null;
		Map<String, Object> body = null;
		try {
			personEntity = restTemplate.exchange(uri, HttpMethod.GET, new HttpEntity(getRestTemplateHeader()), Map.class);
			body = (Map<String, Object>) personEntity.getBody();
		} catch (HttpClientErrorException e) {
			System.err.println(e.getMessage());
			System.err.println(e.getLocalizedMessage());
		}
		return body;
	}
	protected HttpHeaders getRestTemplateHeader() {
		HttpHeaders headers = new HttpHeaders();
	    headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
	    headers.add("api-key", env.getProperty("config.mis_client_secret_client_id"));
	    headers.add("Authorization", "Bearer "+env.getProperty("config.token_bearer"));
		return headers;
	}
	private void addNoCache(HttpHeaders headers) {
		headers.add("cache-control", "no-cache");
	}
	protected HttpHeaders getPatchRestTemplateHeader() {
		HttpHeaders headers = new HttpHeaders();
		MediaType mediaType = new MediaType("application", "merge-patch+json");
		headers.setContentType(mediaType);
		headers.add("Authorization", "Bearer "+env.getProperty("config.token_bearer"));
		return headers;
	}
	RestTemplate getPatchRestTemplate() {
		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
		RestTemplate restTemplate = new RestTemplate(requestFactory);
		return restTemplate;
	}
	RestTemplate getRestTemplate1() {
		RestTemplate restTemplate = new RestTemplate();
		int TIMEOUT = 10;
		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
		requestFactory.setConnectTimeout(TIMEOUT);
		requestFactory.setReadTimeout(TIMEOUT);

		restTemplate.setRequestFactory(requestFactory);
		return restTemplate;
	}
	protected @Autowired RestTemplate restTemplate;
	@Autowired protected Environment env;
	protected static final Logger logger = LoggerFactory.getLogger(EHealth1Common.class);
}
