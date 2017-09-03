package org.algoritmed.mvp1.medic;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.algoritmed.mvp1.DbAlgoritmed;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EmployeeRest extends DbAlgoritmed{
	private static final Logger logger = LoggerFactory.getLogger(EhealthUaRegistryRest.class);

	@PostMapping("/r/remove_employee_msp")
	public @ResponseBody Map<String, Object> remove_employee_msp(
			@RequestBody Integer msp_id
			, Principal userPrincipal) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("msp_id",msp_id);
		map.put("doc_id",msp_id);
		logger.info("\n---------------\n"
				+ "/r/remove_employee_msp"
				+ "\n" + msp_id
				+ "\n" + map
				+ "\n" + userPrincipal
				);
		removeDocElement(map);
		return map;
	}

	@PostMapping("/r/add_employee_msp")
	public @ResponseBody Map<String, Object> add_employee_msp(
			@RequestBody Map<String, Object> data
			, Principal userPrincipal) {
		logger.info("\n---------------\n"
				+ "/r/add_employee_msp"
				+ "\n" + data
				+ "\n" + userPrincipal
				);

		//insert user doc child, with reference to msp
		String userName = userPrincipal.getName();
		Map<String, Object> userNameMap = checkUsername(userName);
		Integer parentId = (Integer) userNameMap.get("user_id");
		Integer doc_id = nextDbId();
		data.put("doc_id", doc_id);
		insertDocElement(data, parentId, doctype_MSP);

		//with reference to msp
		Integer reference = (Integer) data.get("msp_id");
		data.put("reference", reference);
		int update = db1ParamJdbcTemplate.update(sql_doc_update_reference, data);
		return data;
	}

	@PostMapping("/r/saveEmployee")
	public @ResponseBody Map<String, Object> saveEmployee(
			@RequestBody Map<String, Object> data
			, Principal userPrincipal) {
		logger.info("\n---------------\n"
				+ "/r/saveEmployee"
				+ "\n" + data
				+ "\n employee_info = " + data.get("employee_info")
				);
		// вперше додано для autoSave $scope.doc_employee
		Integer doc_id = (Integer) data.get("doc_id");
		data.put("docbody_id", doc_id);
		data.put("employee_id", doc_id);
		Map docbody = (Map) data.get("docbody");
		Map party = (Map) docbody.get("party");
		persistRootElement(data, doctype_employee, sql_employee_insert, sql_employee_update);
		System.err.println(party);
		party.put("person_id", doc_id);
		party.put("family_name", party.get("last_name"));
		System.err.println(party);
		int update = db1ParamJdbcTemplate.update(sql_person_update, party);
		return data;
	}
	
	@PostMapping("/r/add_user_role")
	public @ResponseBody Map<String, Object> add_user_role(
			@RequestBody Map<String, Object> data
			, Principal userPrincipal
			) {
		logger.info("\n---------------\n"
				+ "/r/add_user_role"
				+ "\n" + data
				);
//		addUserRole(data, doc_id, "ROLE_WAITING_FOR_CONFIRMATION");// as example
		int update = db1ParamJdbcTemplate.update(sql_user_role_deleteConfirmation, data);
		int update2 = db1ParamJdbcTemplate.update(sql_user_role_insert, data);
		return data;
	}

	private @Value("${sql.db1.user_role.deleteConfirmation}")	String sql_user_role_deleteConfirmation;
	private @Value("${sql.db1.user_role.insert}")	String sql_user_role_insert;

	private void addUserRole(Map<String, Object> data, Integer user_role_id, String role) {
		data.put("user_role_id", user_role_id);
		data.put("role", role);
		int update = db1ParamJdbcTemplate.update(sql_user_role_insert, data);
	}

	@PostMapping("/r/savePersonRegistry")
	public @ResponseBody Map<String, Object> savePersonRegistry(
			@RequestBody Map<String, Object> data
			, Principal userPrincipal
			) {
		logger.info("\n---------------\n"
				+ "/r/savePersonRegistry"
				+ "\n" + data
				);
		boolean isToSave=true;
		if(isToSave){
//			persistRootElement(data, doctype_employee, sql_person_insert, sql_person_update);
			persistRootElement(data, doctype_employee);
			Integer doc_id = (Integer) data.get("doc_id");
			
			Map data_party = (Map)data.get("party");
			data_party.put("person_id", doc_id);
			data_party.put("update_sql", data.get("update_sql"));
			persistContentElement(data_party, sql_person_insert, sql_person_update);
			
			data.put("user_id", doc_id);
			System.err.println(data);
			persistContentElement(data, sql_users_insert, sql_users_update);
			if(true!=(boolean)data.get("update_sql")){
				addUserRole(data, doc_id, "ROLE_WAITING_FOR_CONFIRMATION");
			}
			
			Map docbodyMap;
			if(true==(boolean)data.get("update_sql")){
				docbodyMap = (Map)data.get("docbody");
			}else {
				docbodyMap = data;
				docbodyMap.remove("password");
				docbodyMap.remove("password_control");
				docbodyMap.remove("docbody");
			}
			updateDocbody(data, docbodyMap, now());// change for autoSave $scope.doc_employee
			
			data.put("docbody_id", doc_id);
			data.put("employee_id", doc_id);
			persistContentElement(data, sql_employee_insert, sql_employee_update);
			
		}
		return data;
	}

	@PostMapping("/r/checkUsername")
	public @ResponseBody Map<String, Object> checkUsername(@RequestBody String username) {
		logger.info("\n---------------\n"
				+ "/r/checkUsername"
				+ "\n" + username
				);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("username", username);
		List<Map<String, Object>> checkUsername_list = db1ParamJdbcTemplate.queryForList(sql_checkUsername, map);
		System.err.println("128---------------");
		System.err.println(checkUsername_list);
		boolean exist = checkUsername_list.size()>0;
		map.put("exist", exist);
		if(exist) {
			Map<String, Object> user = checkUsername_list.get(0);
			map.put("user", user);
			map.put("user_id", user.get("user_id"));
		}
		return map;
	}
	
	
	private @Value("${sql.doc.update.reference}")	String sql_doc_update_reference;
	private @Value("${sql.employee.update}")		String sql_employee_update;
	private @Value("${sql.employee.insert}")		String sql_employee_insert;
	private @Value("${sql.db1.person.update}")		String sql_person_update;
	private @Value("${sql.db1.person.insert}")		String sql_person_insert;
	private @Value("${sql.db1.users.update}")		String sql_users_update;
	private @Value("${sql.db1.users.insert}")		String sql_users_insert;
	private @Value("${sql.db1.users.checkUsername}")		String sql_checkUsername;
	
}
