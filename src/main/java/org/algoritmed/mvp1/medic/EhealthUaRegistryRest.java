package org.algoritmed.mvp1.medic;

import java.security.Principal;
import java.util.Map;

import org.algoritmed.mvp1.DbAlgoritmed;
import org.algoritmed.mvp1.util.EhealthUaRegistryWebClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EhealthUaRegistryRest extends DbAlgoritmed{
	private static final Logger logger = LoggerFactory.getLogger(MedicalPatientRest.class);
	private @Autowired EhealthUaRegistryWebClient registryWebClient;
	
	@PostMapping("/r/saveMsp")
	public @ResponseBody Map<String, Object> saveMsp(
			@RequestBody Map<String, Object> data
			, Principal userPrincipal) {
		logger.info("\n---------------\n"
				+ "/r/saveMsp"
				+ "\n" + data
				);
		if(data.containsKey("doc_id")){//update
			System.err.println("--30----------update--------");
		}else{//insert
			data.put("doctype", 12);//MSP
			Integer doc_id = nextDbId();
			data.put("doc_id", doc_id);
			insertDocElementWithDocbody(data, doc_id, data);
		}
		return data;
	}
	@PostMapping("/r/legal_entities")
	public @ResponseBody Map<String, Object> legal_entities(
			@RequestBody Map<String, Object> data
			, Principal userPrincipal) {
		logger.info("\n---------------\n"
				+ "/r/legal_entities"
				+ "\n" + data
				);
		Map legal_entitiesPut = registryWebClient.legal_entitiesPut(data);
		if(legal_entitiesPut!= null)
			return legal_entitiesPut;
		return data;
	}
}
