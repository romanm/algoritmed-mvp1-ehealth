package org.algoritmed.mvp1.medic;

import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.algoritmed.mvp1.DbAlgoritmed;
import org.algoritmed.mvp1.util.EhealthUaRegistryWebClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class EhealthUaFileUploadRest  extends DbAlgoritmed{
	@PostMapping("/msp_uploadP7sFile")
	public String handleFileUpload(@RequestParam("file") MultipartFile file,
			@RequestParam("doc_id") String doc_id,
			@RequestParam("uri_prop") String uri_prop,
			RedirectAttributes redirectAttributes) {
		logger.info("--------27-----------"
				+ "\n" + "/msp_uploadP7sFile" 
				+ "\n doc_id=" + doc_id
				+ "\n" + file);
		redirectAttributes.addAttribute("doc_id", doc_id);
		Map<String, Object> map = new HashMap<>();
		try {
			String encodeToString = Base64.getEncoder().encodeToString(file.getBytes());
			map.put("signed_legal_entity_request", encodeToString);
			map.put("signed_content_encoding", "base64");
			String uri = env.getProperty(uri_prop);
			System.err.println(uri_prop+" = "+uri);
			String legal_entities_response_body = registryWebClient.legal_entitiesPutStr(map, uri);
			
			String registry_response_file_name = registry_response_file_name(doc_id);
			saveResponse(legal_entities_response_body, doc_id);
			FileCopyUtils.copy(legal_entities_response_body.getBytes(), new File(registry_response_file_name));
		} catch (IOException e) {
			e.printStackTrace();
		}
		logger.info("---------40-----------\n" + "/msp_uploadP7sFile" + "\n" + file);
		return "redirect:/v/admin-msp";
	}
	private void saveResponse(String legal_entities_response_body, String doc_id) {
		System.err.println("--------------46-----------------");
		System.err.println(doc_id);
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("MSP_EHEALT_RESPONSE_type", DocType.MSP_EHEALT_RESPONSE.id());
		paramMap.put("msp_id", doc_id);
		System.err.println(paramMap);
		List<Map<String, Object>> mspEhalethResponseList = 
				db1ParamJdbcTemplate.queryForList(env.getProperty("sql.msp.msp_ehealth_response.select"), paramMap);
		System.err.println(mspEhalethResponseList);
		Integer MSP_EHEALT_RESPONSE_id;
		String sql;
		if(mspEhalethResponseList.size()>0){//update
			Map<String, Object> map = mspEhalethResponseList.get(0);
			MSP_EHEALT_RESPONSE_id = (Integer) map.get("doc_id");
			paramMap.put("sql", "sql.msp.msp_ehealth_response.update");
		}else{//insert
			MSP_EHEALT_RESPONSE_id = nextDbId();
			paramMap.put("doc_id", MSP_EHEALT_RESPONSE_id);
			paramMap.put("doctype", DocType.MSP_EHEALT_RESPONSE.id());
			paramMap.put("parent_id", doc_id);
			paramMap.put("docbody_id", MSP_EHEALT_RESPONSE_id);
			paramMap.put("sql", "sql.msp.msp_ehealth_response.insert");
		}
		System.err.println(paramMap);
		paramMap.put("docbody", legal_entities_response_body);
		System.err.println(legal_entities_response_body);
		update_sql_script(paramMap);
	}
	private @Autowired EhealthUaRegistryWebClient registryWebClient;

}

