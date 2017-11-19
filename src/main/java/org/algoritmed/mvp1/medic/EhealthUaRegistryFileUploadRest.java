package org.algoritmed.mvp1.medic;

import java.io.FileReader;
import java.io.IOException;
import java.security.Principal;
import java.util.Map;

import org.algoritmed.mvp1.DbAlgoritmed;
import org.algoritmed.mvp1.util.EhealthUaRegistryWebClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "${config.security_prefix}")
public class EhealthUaRegistryFileUploadRest extends DbAlgoritmed{

	
	@GetMapping(value = "/read_registry_response/{msp_id}")
	public @ResponseBody Map<String, Object>  read_registry_response(@PathVariable String msp_id
			, Principal userPrincipal
		) throws IOException {
		logger.info("---------------\n"
				+ "/read_registry_response/{msp_id}"
				+ "\n" + msp_id
				);
		String registry_response_file_name = registry_response_file_name(msp_id);
		FileReader fileReader = new FileReader(registry_response_file_name);
		String copyToString = FileCopyUtils.copyToString(fileReader);
		Map<String, Object> registry_response_map = stringToMap(copyToString);
		return registry_response_map;
	}
		
	private @Autowired EhealthUaRegistryWebClient registryWebClient;
	private static final Logger logger = LoggerFactory.getLogger(EhealthUaRegistryFileUploadRest.class);
}
