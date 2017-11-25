package org.algoritmed.mvp1.medic;

import java.io.IOException;
import java.security.Principal;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.Response;

import org.algoritmed.mvp1.DbAlgoritmed;
import org.algoritmed.mvp1.util.EhealthUaRegistryWebClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class EhealthUaFileUploadRest  extends DbAlgoritmed{
	@PostMapping("/msp_upload_division_P7sFile_to_delete")
	public String handleDivisionFileUpload(@RequestParam("file") MultipartFile file,
			@RequestParam("doc_id") String doc_id,
			@RequestParam("uri_prop") String uri_prop,
			Principal principal,
			RedirectAttributes redirectAttributes) {
		String uri = env.getProperty(uri_prop);
		logger.info("--------27-----------"
				+ "\n" + "/msp_upload_division_P7sFile" 
				+ "\n doc_id = " + doc_id
				+ "\n uri_prop = " + uri_prop
				+ "\n uri = " + uri
				+ "\n" + file);

		Map<String, Object> principalMap = super.principal(principal);
		System.err.println(principalMap);
		String msp_access_token = ""+principalMap.get("msp_access_token");

		System.err.println("-----4-5------------------------");
		System.err.println(msp_access_token);

		try {

			Map<String, Object> map = prepareFile(file, uri_prop, uri);
			String dataStr = mapper.writeValueAsString(map);
			Entity<String> dataJson = Entity.json(dataStr);
			Builder wsClientInvocation = registryWebClient.getInvocationBuilder(uri
					, msp_access_token);
			System.err.println("--------55-------------");
			System.err.println(wsClientInvocation);
			Response response = wsClientInvocation.post(dataJson);
			
			System.err.println("--------74-------------");
			System.err.println("status " + response.getStatus());
			String readEntity_body = response.readEntity(String.class);
			System.err.println(readEntity_body);

		} catch (IOException e) {
			e.printStackTrace();
		}

		return "redirect:/v/admin-msp";
	}
	
	@Autowired ObjectMapper mapper = new ObjectMapper();
	
	@PostMapping("/msp_uploadP7sFile__to_delete")
	public String handleFileUpload(@RequestParam("file") MultipartFile file,
			@RequestParam("doc_id") String doc_id,
			@RequestParam("uri_prop") String uri_prop,
			Principal principal,
			RedirectAttributes redirectAttributes) {
		String uri = env.getProperty(uri_prop);
		logger.info("--------44-----------"
				+ "\n" + "/msp_uploadP7sFile" 
				+ "\n doc_id = " + doc_id
				+ "\n uri_prop = " + uri_prop
				+ "\n uri = " + uri
				+ "\n" + file);
		redirectAttributes.addAttribute("doc_id", doc_id);
		try {
			Map<String, Object> map = prepareFile(file, uri_prop, uri);
			String token_bearer = env.getProperty("config.token_bearer");
			String legal_entities_response_body = registryWebClient.legal_entitiesPutStr(map, uri, token_bearer);
			
			saveResponse(legal_entities_response_body, doc_id);
//			String registry_response_file_name = registry_response_file_name(doc_id);
//			FileCopyUtils.copy(legal_entities_response_body.getBytes(), new File(registry_response_file_name));
		} catch (IOException e) {
			e.printStackTrace();
		}
		logger.info("---------40-----------\n" + "/msp_uploadP7sFile" + "\n" + file);
		return "redirect:/v/admin-msp";
	}
	private Map<String, Object> prepareFile(MultipartFile file, String uri_prop, String uri) throws IOException {
		Map<String, Object> map = new HashMap<>();
		String encodeToString = Base64.getEncoder().encodeToString(file.getBytes());
		map.put("signed_legal_entity_request", encodeToString);
		map.put("signed_content_encoding", "base64");
		System.err.println(uri_prop+" = "+uri);
		return map;
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
			paramMap.put("sql", "sql.msp.msp_ehealth_response.update");
			Map<String, Object> map = mspEhalethResponseList.get(0);
			MSP_EHEALT_RESPONSE_id = (Integer) map.get("doc_id");
		}else{//insert
			paramMap.put("sql", "sql.msp.msp_ehealth_response.insert");
			MSP_EHEALT_RESPONSE_id = nextDbId();
			paramMap.put("doc_id", MSP_EHEALT_RESPONSE_id);
			paramMap.put("doctype", DocType.MSP_EHEALT_RESPONSE.id());
			paramMap.put("parent_id", doc_id);
			paramMap.put("docbody_id", MSP_EHEALT_RESPONSE_id);
			
			paramMap.put("MSP_CLIENT_ID_type", DocType.MSP_CLIENT_ID.id());
			Integer MSP_CLIENT_ID_doc_id = nextDbId();
			paramMap.put("MSP_CLIENT_ID_doc_id", MSP_CLIENT_ID_doc_id);
			Map<String, Object> legal_entities_response_map = stringToMap(legal_entities_response_body);
			String msp_client_id = mapUtil.getString(legal_entities_response_map, "urgent", "security", "client_id");
			System.err.println("-----78-----------msp_client_id = "+msp_client_id);
			paramMap.put("msp_client_id", msp_client_id);
		}
		System.err.println(paramMap);
		paramMap.put("docbody", legal_entities_response_body);
		System.err.println(legal_entities_response_body);
		update_sql_script(paramMap);
	}

	private @Autowired EhealthUaRegistryWebClient registryWebClient;

}

