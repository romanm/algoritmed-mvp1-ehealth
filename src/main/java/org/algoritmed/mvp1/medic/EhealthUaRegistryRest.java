package org.algoritmed.mvp1.medic;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.algoritmed.mvp1.CommonDbRest;
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
		logger.info("---------------\n"
				+ "/r/read_msp_employee/{msp_id}"
				+ "\n" + userPrincipal
				);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("msp_id", msp_id);
		System.err.println("/r/read_msp_employee/{msp_id} "+msp_id);
		System.err.println("sql.msp_employee.list");
		System.err.println(sql_msp_employee_list.replace(":msp_id",""+msp_id));
		System.err.println("sql.msp_employee.role.list");
		System.err.println(sql_msp_employee_role_list.replace(":msp_id",""+msp_id));
		Map<Integer, Map<String, Object>> users = new HashMap<Integer, Map<String, Object>>();
		List<Map<String, Object>> msp_employee_list = db1ParamJdbcTemplate.queryForList(sql_msp_employee_list, map);
		for (Map<String, Object> m : msp_employee_list) {
			Integer person_id = (Integer) m.get("person_id");
			users.put(person_id, m);
			m.put("roles", new ArrayList<Object>());
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

	private @Value("${sql.msp.seek}")				String sql_msp_seek;
	@GetMapping(value = "/r/seek_msp/{seek_msp}")
	public @ResponseBody Map<String, Object>  seek_msp(@PathVariable String seek_msp) {
		Map<String, Object> map = new HashMap<String, Object>();
		String seek_msp2 = "%"+seek_msp+"%";
		map.put("seek_msp", seek_msp2);
		System.err.println(sql_msp_seek.replace(":seek_msp",seek_msp2));
		List<Map<String, Object>> list = db1ParamJdbcTemplate.queryForList(sql_msp_seek,map);
		map.put("list", list);
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
	
	
	@PostMapping("/r/saveDeclaration")
	public @ResponseBody Map<String, Object> saveDeclaration(
			 @RequestBody Map<String, Object> data
			, Principal userPrincipal) {
		logger.info("\n-----110----------\n"
				+ "/r/saveDeclaration"
				+ "\n" + data
				);
		return data;
	}

	@PostMapping("/r/savePatient")
	public @ResponseBody Map<String, Object> savePatient(
			@RequestBody Map<String, Object> data
			, Principal userPrincipal) {
		logger.info("\n-----------104----\n"
				+ "/r/savePatient"
				+ "\n" + data
				+ "\n" + Integer.MAX_VALUE
				);
		persistRootElement(data
			, DocType.PATIENT.id()
			);
		if(!(boolean) data.get("update_sql")){//insert
			Integer dbId = (Integer)data.get("doc_id");
			Integer msp_id = (Integer)data.get("msp_id");
			data.put("patient_id", dbId);
			generateNewUuid(data, dbId);
			insertChildWithReference(dbId, msp_id);
			db1ParamJdbcTemplate.update(env.getProperty("sql.insertPatient"), data);
			data.put("sql","sql.patient.add_insert_patient");
			update_sql_script(data);
		}else {
			db1ParamJdbcTemplate.update( env.getProperty("sql.updatePatient") , data);
		}
		System.err.println(data);
		return data;
	}

	private @Value("${sql.msp.update}")				String sql_msp_update;
	private @Value("${sql.msp.insert}")				String sql_msp_insert;
	
	@PostMapping("/r/saveMsp")
	public @ResponseBody Map<String, Object> saveMsp(
			@RequestBody Map<String, Object> data
			, Principal userPrincipal) {
		logger.info("\n---76------------\n"
				+ "/r/saveMsp"
				+ "\n" + data
				+ "\n" + userPrincipal
				);
		Map<String, Object> principal = principal(userPrincipal);
		boolean isAdminMSPRole = hasAdminMSPRole(principal);
		if(isAdminMSPRole){
			persistRootElement(data, doctype_MSP, sql_msp_insert, sql_msp_update);
			boolean update_sql = (boolean) data.get("update_sql");
			if(!update_sql) {
				System.err.println(principal);
				System.err.println("додати головного лікаря");
				Integer parentId = (Integer) principal.get("user_id");
				Integer reference = (Integer) data.get("doc_id");
				insertChildWithReference(parentId, reference);
			}
		}
		return data;
	}

	private void persistRootElement2(Map<String, Object> data, int doctype, String sql_insert, String sql_update) {
		if(data.containsKey("doc_id")){//update
			System.err.println("--86----------update--------");
			updateDocbody(data, (Map<String, Object>)data.get("docbody"), now());// change for autoSave $scope.doc_employee
			//updateDocbody(data, data, now()); 
			System.err.println(sql_update);
			int update = db1ParamJdbcTemplate.update(sql_update, data);
		}else{//insert
			System.err.println("--90----------insert--------");
			Integer doc_id = nextDbId();
			data.put("doc_id", doc_id);
			data.put("doctype", doctype);
			insertDocElementWithDocbody(data, doc_id, data);
			System.err.println(sql_insert);
			int update = db1ParamJdbcTemplate.update(sql_insert, data);
		}
	}

	@PostMapping("/r/legal_entities")
	public @ResponseBody Map<String, Object> legal_entities(
			@RequestBody Map<String, Object> data
			, Principal userPrincipal) {
		logger.info("\n---------------\n"
				+ "/r/legal_entities"
				+ "\n" + data
				);
		Map<String, Object> legal_entitiesPut = registryWebClient.legal_entitiesPut(data);
		if(legal_entitiesPut!= null)
			return legal_entitiesPut;
		return data;
	}

}
