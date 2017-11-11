package org.algoritmed.mvp1.ehealth1.rest;

import java.util.Map;

import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class LegalEntityRestTest {
//	@Test
	public void legal_entities_by_id() {
		RestTemplate restTemplate = new RestTemplate();
		String string = "http://localhost:8090/r/eh1cc/api/legal_entities/bf48fba2-e4e8-4a06-aeaa-345d8346d7bb";
		ResponseEntity<Map> exchange = restTemplate.exchange(string, HttpMethod.GET
				, null, Map.class);
		System.err.println("Hello World "+exchange.getBody());
	}
	@Test
	public void helloWorld() {
		System.err.println("Hello World");
	}

}
