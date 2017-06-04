package org.algoritmed.mvp1.medic;

import java.security.Principal;
import java.util.Map;

import org.algoritmed.mvp1.util.EhealthUaRegistryWebClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EhealthUaRegistryRest {
	private static final Logger logger = LoggerFactory.getLogger(MedicalPatientRest.class);
	private @Autowired EhealthUaRegistryWebClient registryWebClient;
	
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
