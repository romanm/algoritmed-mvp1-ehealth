package org.algoritmed.mvp1.medic;

import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.algoritmed.mvp1.util.EhealthUaRegistryWebClient;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class EhealthUaRegistryFileUploadRest {

	@PostMapping("/r/msp_uploadP7sFile")
	public String handleFileUpload(@RequestParam("file") MultipartFile file,
			@RequestParam("doc_id") String doc_id,
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
			String legal_entitiesPutStr = registryWebClient.legal_entitiesPutStr(map);
			
			String fileName = "response_"+doc_id+".json";
			FileCopyUtils.copy(legal_entitiesPutStr.getBytes(), new File(tmpFolder+fileName));
//			Map legal_entitiesPut = registryWebClient.legal_entitiesPut(map);
//			logger.info("---------37-----------\n"  + legal_entitiesPut);
		} catch (IOException e) {
			e.printStackTrace();
		}
		String originalFilename = file.getOriginalFilename();
		logger.info("---------40-----------\n" + "/r/msp_uploadP7sFile" + "\n" + file);
		return "redirect:/v/admin-msp";
//		return "redirect:/v/testMvpMedic?doc_id={doc_id}";
	}
	
	private @Value("${config.tmpFolder}")			String tmpFolder;
	
	private @Autowired EhealthUaRegistryWebClient registryWebClient;
	private static final Logger logger = LoggerFactory.getLogger(EhealthUaRegistryFileUploadRest.class);
}
