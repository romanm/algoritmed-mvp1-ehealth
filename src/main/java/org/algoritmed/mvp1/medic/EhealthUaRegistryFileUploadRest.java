package org.algoritmed.mvp1.medic;

import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.algoritmed.mvp1.util.EhealthUaRegistryWebClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class EhealthUaRegistryFileUploadRest {
	@PostMapping("/r/msp_uploadP7sFile")
	public String handleFileUpload(@RequestParam("file") MultipartFile file,
			RedirectAttributes redirectAttributes) {
		Map<String, Object> map = new HashMap<>();
		try {
			String encodeToString = Base64.getEncoder().encodeToString(file.getBytes());
			map.put("signed_legal_entity_request", encodeToString);
			map.put("signed_content_encoding", "base64");
			Map legal_entitiesPut = registryWebClient.legal_entitiesPut(map);
		} catch (IOException e) {
			e.printStackTrace();
		}
		String originalFilename = file.getOriginalFilename();
		logger.info("--------------------\n" + "/r/msp_uploadP7sFile" + "\n" + file);
		return "redirect:/v/testMvpMedic?doc_id=157";
	}
	private @Autowired EhealthUaRegistryWebClient registryWebClient;
	private static final Logger logger = LoggerFactory.getLogger(EhealthUaRegistryFileUploadRest.class);
}
