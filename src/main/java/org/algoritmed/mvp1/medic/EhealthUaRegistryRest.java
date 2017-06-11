package org.algoritmed.mvp1.medic;

import java.security.Principal;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.algoritmed.mvp1.DbAlgoritmed;
import org.algoritmed.mvp1.util.EhealthUaRegistryWebClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EhealthUaRegistryRest extends DbAlgoritmed{
	private static final Logger logger = LoggerFactory.getLogger(MedicalPatientRest.class);
	private @Autowired EhealthUaRegistryWebClient registryWebClient;
	
	private @Value("${sql.docbody.byId}")				String sql_docbody_byId;
	@GetMapping(value = "/r/read_msp/{msp_id}")
	public @ResponseBody Map<String, Object>  patient(@PathVariable Integer msp_id) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("msp_id", msp_id);
		map.put("doc_Id", msp_id);
		String docbody = db1ParamJdbcTemplate.queryForObject(sql_docbody_byId, map, String.class);
		docbodyStrToMap(map, docbody);
		return map;
	}

	private @Value("${sql.msp.list}")				String sql_msp_list;
	@GetMapping(value = "/r/msp_list")
	public @ResponseBody Map<String, Object>  msp_list() {
		Map<String, Object> map = new HashMap<String, Object>();
		List<Map<String, Object>> msp_list = db1JdbcTemplate.queryForList(sql_msp_list);
		map.put("msp_list", msp_list);
		return map;
	}
	
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
			insertMsp(data);
		}
		return data;
	}
	private @Value("${sql.msp.insert}")				String sql_msp_insert;
	private void insertMsp(Map<String, Object> dbSaveObj) {
		System.err.println("--insertMsp---");
		System.err.println(sql_msp_insert
				.replace(":"+"doc_id", ""+dbSaveObj.get("doc_id"))
				.replace(":"+"doctype", ""+dbSaveObj.get("name"))
				.replace(":"+"parent_id", ""+dbSaveObj.get("public_name"))
				);
		int update = db1ParamJdbcTemplate.update(sql_msp_insert, dbSaveObj);
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
