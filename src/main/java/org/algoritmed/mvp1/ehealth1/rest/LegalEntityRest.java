package org.algoritmed.mvp1.ehealth1.rest;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "${config.security_prefix}/eh1cc/api/legal_entities")
public class LegalEntityRest extends EHealth1Common{
	
	@GetMapping()
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
