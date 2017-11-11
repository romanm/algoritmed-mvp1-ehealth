package org.algoritmed.mvp1.medic;

import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.algoritmed.mvp1.DbAlgoritmed;
import org.algoritmed.mvp1.util.EhealthUaRegistryWebClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RestController
public class EhealthUaFileUploadRest  extends DbAlgoritmed{
	@PostMapping("/msp_uploadP7sFile")
	public String handleFileUpload(@RequestParam("file") MultipartFile file,
			@RequestParam("doc_id") String doc_id,
			@RequestParam("uri_prop") String uri_prop,
			RedirectAttributes redirectAttributes) {
		System.err.println(23);
		System.err.println(doc_id);
		redirectAttributes.addAttribute("doc_id", doc_id);
		Map<String, Object> map = new HashMap<>();
		try {
			String encodeToString = Base64.getEncoder().encodeToString(file.getBytes());
//			System.err.println(26);
//			System.err.println(encodeToString);
//			System.err.println(28);
			map.put("signed_legal_entity_request", encodeToString);
			map.put("signed_content_encoding", "base64");
//			String uri = env.getProperty("config.path_registry_msp");
			String uri = env.getProperty(uri_prop);
			System.err.println(uri_prop);
			System.err.println(64);
			System.err.println(uri);
			String legal_entitiesPutStr = registryWebClient.legal_entitiesPutStr(map, uri);
			
			String registry_response_file_name = registry_response_file_name(doc_id);
			FileCopyUtils.copy(legal_entitiesPutStr.getBytes(), new File(registry_response_file_name));
//			Map legal_entitiesPut = registryWebClient.legal_entitiesPut(map);
//			logger.info("---------37-----------\n"  + legal_entitiesPut);
		} catch (IOException e) {
			e.printStackTrace();
		}
//		String originalFilename = file.getOriginalFilename();
		logger.info("---------40-----------\n" + "/msp_uploadP7sFile" + "\n" + file);
		return "redirect:/v/admin-msp";
//		return "redirect:/v/testMvpMedic?doc_id={doc_id}";
	}
	private @Autowired EhealthUaRegistryWebClient registryWebClient;

}

