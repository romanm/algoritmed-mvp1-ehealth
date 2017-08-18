package org.algoritmed.mvp1.medic;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
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
	private static final Logger logger = LoggerFactory.getLogger(EhealthUaRegistryRest.class);
	private @Autowired EhealthUaRegistryWebClient registryWebClient;

	private @Value("${sql.msp_employee.list}")			String sql_msp_employee_list;
	private @Value("${sql.msp_employee.role.list}")		String sql_msp_employee_role_list;
	@GetMapping(value = "/r/read_msp_employee/{msp_id}")
	public @ResponseBody Map<String, Object>  msp_employee_list(@PathVariable Integer msp_id
			, Principal userPrincipal
		) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("msp_id", msp_id);
		System.err.println("/r/read_msp_employee/{msp_id} "+msp_id);
		System.err.println(sql_msp_employee_list.replace(":msp_id",""+msp_id));
		Map<Integer, Map<String, Object>> users = new HashMap();
		List<Map<String, Object>> msp_employee_list = db1ParamJdbcTemplate.queryForList(sql_msp_employee_list, map);
		for (Map<String, Object> m : msp_employee_list) {
			Integer person_id = (Integer) m.get("person_id");
			users.put(person_id, m);
			m.put("roles", new ArrayList());
		}
		List<Map<String, Object>> e_roles = db1ParamJdbcTemplate.queryForList(sql_msp_employee_role_list, map);
		for (Map<String, Object> m : e_roles) {
			Integer user_id = (Integer) m.get("user_id");
			List<Map> u_roles = (List<Map>) users.get(user_id).get("roles");
			u_roles.add(m);
		}
		map.put("msp_employee_list", msp_employee_list);
		return map;
	}

	private @Value("${sql.docbody.byId}")				String sql_docbody_byId;
	@GetMapping(value = "/r/read_docbody/{doc_id}")
	public @ResponseBody Map<String, Object>  read_docbody(@PathVariable Integer doc_id) {
		Map<String, Object> map = new HashMap<String, Object>();
		readDocbody(doc_id, map);
		return map;
	}

	@GetMapping(value = "/r/read_msp/{msp_id}")
	public @ResponseBody Map<String, Object>  read_msp(@PathVariable Integer msp_id) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("msp_id", msp_id);
		readDocbody(msp_id, map);
		return map;
	}

	private void readDocbody(Integer msp_id, Map<String, Object> map) {
		map.put("doc_id", msp_id);
		String docbody = db1ParamJdbcTemplate.queryForObject(sql_docbody_byId, map, String.class);
		docbodyStrToMap(map, docbody);
	}
	
	private @Value("${sql.employee.list}")				String sql_employee_list;
	@GetMapping(value = "/r/employee_list")
	public @ResponseBody Map<String, Object>  employee_list() {
		Map<String, Object> map = new HashMap<String, Object>();
		List<Map<String, Object>> list = db1JdbcTemplate.queryForList(sql_employee_list);
		map.put("employee_list", list);
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
	
	@PostMapping("/r/saveEmployee")
	public @ResponseBody Map<String, Object> saveEmployee(
			@RequestBody Map<String, Object> data
			, Principal userPrincipal) {
		logger.info("\n---------------\n"
				+ "/r/saveEmployee"
				+ "\n" + data
				);
		persistRootElement(data, doctype_employee, sql_employee_insert, sql_employee_update);

		return data;
	}
	
	@PostMapping("/r/savePersonRegistry")
	public @ResponseBody Map<String, Object> savePersonRegistry(
			@RequestBody Map<String, Object> data
			) {
		logger.info("\n---------------\n"
				+ "/r/savePersonRegistry"
				+ "\n" + data
				);
		return data;
	}
	
	@PostMapping("/r/saveDeclaration")
	public @ResponseBody Map<String, Object> saveDeclaration(
			@RequestBody Map<String, Object> data
			, Principal userPrincipal) {
		logger.info("\n---------------\n"
				+ "/r/saveDeclaration"
				+ "\n" + data
				);
		return data;
	}

	int doctype_MSP = 12;
	int doctype_employee = 13;
	int doctype_declaration = 14;
	
	@PostMapping("/r/saveMsp")
	public @ResponseBody Map<String, Object> saveMsp(
			@RequestBody Map<String, Object> data
			, Principal userPrincipal) {
		logger.info("\n---76------------\n"
				+ "/r/saveMsp"
				+ "\n" + data
				);
		persistRootElement(data, doctype_MSP, sql_msp_insert, sql_msp_update);
		return data;
	}

	private void persistRootElement(Map<String, Object> data, int doctype, String sql_insert, String sql_update) {
		if(data.containsKey("doc_id")){//update
			System.err.println("--86----------update--------");
			updateDocbody(data, data, now());
			int update = db1ParamJdbcTemplate.update(sql_update, data);
		}else{//insert
			System.err.println("--90----------insert--------");
			data.put("doctype", doctype);
			Integer doc_id = nextDbId();
			data.put("doc_id", doc_id);
			insertDocElementWithDocbody(data, doc_id, data);
			System.err.println(sql_insert);
			int update = db1ParamJdbcTemplate.update(sql_insert, data);
		}
	}
	
	private @Value("${sql.employee.update}")		String sql_employee_update;
	private @Value("${sql.employee.insert}")		String sql_employee_insert;
	private @Value("${sql.msp.update}")				String sql_msp_update;
	private @Value("${sql.msp.insert}")				String sql_msp_insert;

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
