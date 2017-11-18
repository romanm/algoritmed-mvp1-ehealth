package org.algoritmed.mvp1;

import java.io.IOException;
import java.security.Principal;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@PropertySource("classpath:sql.properties")
public class DbAlgoritmed {
	protected static final Logger logger = LoggerFactory.getLogger(DbAlgoritmed.class);
	@Autowired protected Environment env;
	
	public enum DocType {
		PATIENT(1)
		, MSP(12)
		, MSP_EHEALT_RESPONSE(48)
		, EMPLOYEE(13)
		, DECLARATION(14)
		;
		
		public int id() {
			return id;
		}
		
		private int id;

		DocType(int id){
			this.id=id;
		}
	}
	protected int doctype_patient = 1;
	protected int doctype_MSP = 12;
	protected int doctype_employee = 13;
	int doctype_declaration = 14;

	protected void update_sql_script(Map<String, Object> data) {
		String sql = (String) data.get("sql");
		String sql_from_env = env.getProperty(sql);
		data.put(sql, sql_from_env);
		logger.info("\n-----40--------\n"
				+ "\n" + data
				+ "\n" + data.get("physician_id")
				+ "\n" + data.get("patient_id")
				+ "\n" + sql_from_env
				);
		if(sql_from_env.contains(";")) {
			if(data.containsKey("docbodyMap")) {
				Map<String,Object> docbodyMap = (Map<String, Object>) data.get("docbodyMap");
				String docbody = objectToString(docbodyMap);
				data.put("docbody", docbody);
			}
			if(data.containsKey("lotOfNewIds")) {
				int lotOfNewIds = (int) data.get("lotOfNewIds");
				for (int i = 1; i <= lotOfNewIds; i++) {
					Integer nextDbId = nextDbId();
					data.put("nextDbId"+i, nextDbId);
					System.err.println("nextDbId"+i+"="+nextDbId);
				}
			}
			String[] sqls_from_env = sql_from_env.split(";");
			for (int i = 0; i < sqls_from_env.length; i++) {
				String sql_command = sqls_from_env[i].trim();
				System.err.print(sql_command);
				if(sql_command.length()==0)
					continue;
				System.err.print("-"+i+"->");
				String[] split = sql_command.split(" ");
				String first_word = split[0];
				System.err.println(first_word);
				if("INSERT".equals(first_word)
				|| "UPDATE".equals(first_word)
				|| "DELETE".equals(first_word)
				){
					if("docbody".equals(split[1])) {
						if(data.containsKey("docbodyMap")) {
							String docbody = objectToString(data.get("docbodyMap"));
							data.put("docbody", docbody);
						}
					}
					int update = db1ParamJdbcTemplate.update(sql_command, data);
					data.put("update"+i, update);
				}else if("SELECT".equals(first_word)) {
					read_select(data, sql_command, i);
				}
			}
		}else {
			int update = db1ParamJdbcTemplate.update(sql_from_env, data);
			data.put("update", update);
		}
	}
	
	protected void read_select(Map<String, Object> data, String sql_command, Integer i) {
		String nr = null==i?"":(""+i);
		System.err.println(sql_command);
		System.err.println(sql_command.indexOf("SELECT 'docbody' datatype"));
		if(sql_command.indexOf("SELECT 'docbody' datatype")==0) {
			List<Map<String, Object>> docbodyList = db1ParamJdbcTemplate.queryForList(sql_command, data);
			if(docbodyList.size()>0) {
				Map<String, Object> docbodyMap = docbodyList.get(0);
				String docbodyStr = (String) docbodyMap.get("docbody");
				Map<String, Object> docbodyStr2Map = stringToMap(docbodyStr);
				docbodyMap.put("docbody", docbodyStr2Map);
				data.put("docbody"+nr, docbodyMap);
			}
		}else{
			List<Map<String, Object>> list = db1ParamJdbcTemplate.queryForList(sql_command, data);
			data.put("list"+nr, list);
		}
	}
	
	protected void insertChildWithReference(Integer parentId, Integer reference) {
		Map<String, Object> data = new HashMap<String, Object>();
		Integer doc_id = nextDbId();
		data.put("doc_id", doc_id);
		insertDocElement(data, parentId, doctype_MSP);
		//with reference to msp
		data.put("reference", reference);
		int update = db1ParamJdbcTemplate.update(sql_doc_update_reference, data);
	}

	private @Value("${sql.doc.update.reference}")	String sql_doc_update_reference;
	
	protected void persistRootElement(Map<String, Object> data, int doctype) {
		System.err.println("--43--------");
		System.err.println(data.containsKey("doc_id"));
		System.err.println(data.get("doc_id"));
		if(data.containsKey("doc_id")){//update
			System.err.println("--180----------update--------");
			if(!data.containsKey("docbody_id"))
				data.put("docbody_id", data.get("doc_id"));
			System.err.println(data);
//			updateDocbody(data, (Map)data.get("docbody"), now());// change for autoSave $scope.doc_employee
			updateDocbody(data, data, now());// change for autoSave $scope.doc_employee
			data.put("update_sql", true);
		}else{//insert
			System.err.println("--183----------insert--------");
			data.put("update_sql", false);
			Integer doc_id = nextDbId();
			data.put("doc_id", doc_id);
			data.put("doctype", doctype);
			insertDocElementWithDocbody(data, doc_id, data);
		}
	}

	protected void persistRootElement(Map<String, Object> data, int doctype, String sql_insert, String sql_update) {
		persistRootElement(data, doctype);
		persistContentElement(data,sql_insert,sql_update);
	}

	protected void persistContentElement(Map<String, Object> data, String sql_insert, String sql_update) {
		boolean update_sql = (boolean) data.get("update_sql");
		if(update_sql){//update
//			if(data.containsKey("doc_id")){//update
			System.err.println("213 upd "+sql_update);
			int update = db1ParamJdbcTemplate.update(sql_update, data);
		}else{//insert
			System.err.println("216 ins "+sql_insert);
			int update = db1ParamJdbcTemplate.update(sql_insert, data);
		}
	}
	protected void docbodyToMap(Map<String, Object> map) {
		String docbody = (String) map.get("docbody");
		docbodyStrToMap(map, docbody);
	}

	protected void docbodyStrToMap(Map<String, Object> map, String docbody) {
		if(docbody!=null){
			Map<String, Object> docbodyMap = stringToMap(docbody);
			map.put("docbody", docbodyMap);
		}
	}

	protected List<Map<String, Object>> getList(String sql, Map<String, Object> map) {
		return db1ParamJdbcTemplate.queryForList(sql, map);
	}
	protected Map<String, Object> getMap(String sql, Map<String, Object> map) {
		return db1ParamJdbcTemplate.queryForMap(sql, map);
	}

	@Value("${sql.doc.delete}") protected				String sql_doc_delete;
	protected void removeDocElement(Map<String, Object> dbSaveObj) {
		System.err.println(sql_doc_delete.replace(":"+"doc_id", ""+dbSaveObj.get("doc_id")));
		int numberOfDeletedRows = db1ParamJdbcTemplate.update(sql_doc_delete, dbSaveObj);
		dbSaveObj.put("numberOfDeletedRows", numberOfDeletedRows);
	}

	protected void updateDocbody(Map<String, Object> dbSaveObj, Map<String, Object> docbodyMap) {
		updateDocbody(dbSaveObj, docbodyMap, now());
	}

	protected void updateDocbody(Map<String, Object> dbSaveObj, Map<String, Object> docbodyMap, Timestamp updated) {
		System.err.println(docbodyMap);
		String docbody = objectToString(docbodyMap);
		System.err.println("------56--------");
		System.err.println(docbody);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("docbody", docbody);
		map.put("docbody_id", docbodyMap.get("docbody_id"));
		map.put("doc_id", docbodyMap.get("docbody_id"));
		map.put("updated", updated);
		System.err.println(map);
//		if(true) return;
		int update = db1ParamJdbcTemplate.update(sql_docbody_update, map);
		int update2 = db1ParamJdbcTemplate.update(sql_doctimestamp_update, map);
	}
	protected @Value("${sql.docbody.update}")		String sql_docbody_update;
	protected @Value("${sql_doctimestamp_update}")	String sql_doctimestamp_update;

	protected void insertDocElementWithDocbody(Map<String, Object> dbSaveObj, Integer parentId
			, Map<String, Object> docbodyMap) {
		insertDocElementWithDocbody(dbSaveObj, parentId);
		Timestamp created = (Timestamp) dbSaveObj.get("created");
		updateDocbody(dbSaveObj, docbodyMap, created);
	}
	private @Value("${sql.docbody.insertEmpty}")	String sql_docbody_insertEmpty;
	private @Value("${sql_doc_update_docbody}")		String sql_doc_update_docbody;
	protected void insertDocElementWithDocbody(Map<String, Object> dbSaveObj, Integer parentId) {
		insertDocElement(dbSaveObj, parentId);
		dbSaveObj.put("docbody_id", dbSaveObj.get("doc_id"));
		int update3 = db1ParamJdbcTemplate.update(sql_docbody_insertEmpty, dbSaveObj);
		int update2 = db1ParamJdbcTemplate.update(sql_doc_update_docbody, dbSaveObj);
	}
	private @Value("${sql.doc.insert}")				String sql_doc_insert;
	private @Value("${sql.doctimestamp.insert}")	String sql_doctimestamp_insert;
	/**
	 * Запис в абстрактного вузла в "системі документ" БД.
	 * Базісний елемент системи. 
	 * @param dbSaveObj
	 * @param parentId
	 */
	protected void insertDocElement(Map<String, Object> data, Integer parentId, Integer  doctype) {
		data.put("doctype", doctype);
		insertDocElement(data, parentId);
	}
	protected void insertDocElement(Map<String, Object> data, Integer parentId) {
		data.put("parent_id", parentId);
		if(!data.containsKey("created")){
			Timestamp created = now();
			data.put("created", created);
		}
		System.err.println(data);
		System.err.println("------127-----------");
		System.err.println(sql_doc_insert
			.replace(":"+"doc_id", ""+data.get("doc_id"))
			.replace(":"+"doctype", ""+data.get("doctype"))
			.replace(":"+"parent_id", ""+data.get("parent_id"))
		);
		int update = db1ParamJdbcTemplate.update(sql_doc_insert, data);
		int update2 = db1ParamJdbcTemplate.update(sql_doctimestamp_insert, data);
		if(data.containsKey("reference")){
			
		}
	}
	
	private @Value("${sql.doc.byId}")	String sql_doc_byId;
	protected Map<String, Object> readDocElement(Map<String, Object> dbSaveObj) {
		Map<String, Object> docElement = db1ParamJdbcTemplate.queryForMap(sql_doc_byId, dbSaveObj);
		System.err.println(docElement);
		docbodyToMap(docElement);
		System.err.println(docElement);
		return docElement;
	}
	protected Timestamp now() {
		return new Timestamp(Calendar.getInstance().getTimeInMillis());
	}
	protected Map<String, Object> stringToMap(String protocolDoc) {
		Map map = null;
		try {
			map = objectMapper.readValue(protocolDoc, Map.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(map==null)
			map = new HashMap<>();
		return map;
	}
	@Autowired
	protected	ObjectMapper objectMapper;
	protected String objectToString(Object dbSaveObj) {
		String protocolAsString = "{}";
		try {
			protocolAsString = objectMapper.writeValueAsString(dbSaveObj);
		} catch (JsonProcessingException e1) {
			e1.printStackTrace();
		}
		return protocolAsString;
	}
	
	

	/**
	 * SQL select - повертає наступний ID единий для всієй БД.
	 */
	private @Value("${sql.nextDbId}") String sql_nextDbId;
	/**
	 * Генератор наступного ID единого для всієї БД.
	 * @return Наступний ID единий для всієй БД.
	 */
	protected Integer nextDbId() {
		Integer nextDbId = db1JdbcTemplate.queryForObject(sql_nextDbId, Integer.class);
		return nextDbId;
	}

	protected Integer currDbId() {
		String sql_currDbId = env.getProperty("sql.currDbId");
		Integer currDbId = db1JdbcTemplate.queryForObject(sql_currDbId, Integer.class);
		return currDbId;
	}

	protected @Autowired JdbcTemplate db1JdbcTemplate;
	protected @Autowired NamedParameterJdbcTemplate db1ParamJdbcTemplate;

	String adminMSPRoles = "ROLE_HEAD_MSP;ROLE_ADMIN_MSP;ROLE_ADMIN_APP";
	protected boolean hasAdminMSPRole(Map<String, Object> principal) {
		return hasRole(principal, adminMSPRoles);
	}

	private boolean hasRole(Map<String, Object> principal, String roles) {
		UsernamePasswordAuthenticationToken upat = (UsernamePasswordAuthenticationToken) principal.get("principal");
		for (GrantedAuthority grantedAuthority : upat.getAuthorities())
			if(roles.indexOf(grantedAuthority.getAuthority())>=0)
				return true;
		return false;
	}
	
	public Map<String, Object> principal(Principal principal) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("principal", principal);
		logger.info(" --------- \n"
				+ "/v/principal \n" + map);
		Map<String, Object> uriMap = new HashMap<String, Object>();
		if(null!=principal) {
			String name = principal.getName();
			System.err.println("name = "+name);
			map.put("username", name);
			Map<String, Object> user = db1ParamJdbcTemplate.queryForMap(sqlDb1UsersFromUsername, map);
			user.remove("password");
			map.put("user", user);

			Integer user_id = (Integer) user.get("user_id");
			
			read_user_msp(map, user_id);

			if("admin".equals(name)) {
				Map<String, Object> msp_list = msp_list();
				map.put("user_msp", msp_list.get("msp_list"));
			}
			uriMap.put("uri_registry", env.getProperty("config.uri_registry"));
			uriMap.put("uri_oauth2_sign_in", env.getProperty("config.uri_oauth2_sign_in"));
			uriMap.put("uri_oauth2_code_grant", env.getProperty("config.uri_oauth2_code_grant"));
			uriMap.put("uri_oauth2_refresh_tokens", env.getProperty("config.uri_oauth2_refresh_tokens"));
		}
		uriMap.put("security_prefix", env.getProperty("config.security_prefix"));
		map.put("uri", uriMap);
		return map;
	}

	private void read_user_msp(Map<String, Object> map, Integer user_id) {//as samples for use throws /read_sql_with_param
		map.put("user_id", user_id);
		List l = db1ParamJdbcTemplate.queryForList(sql_user_msp, map);
		map.put("user_msp", l);
	}

	private @Value("${sql.db1.users.fromUsername}")	String sqlDb1UsersFromUsername;
	private @Value("${sql.db1.user.msp}")			String sql_user_msp;
	private @Value("${sql.msp.list}")				String sql_msp_list;
	
	public Map<String, Object> msp_list() {
		Map<String, Object> map = new HashMap<String, Object>();
		List<Map<String, Object>> msp_list = db1JdbcTemplate.queryForList(sql_msp_list);
		map.put("msp_list", msp_list);
		return map;
	}

	/**
	 * Створює новий UUID в БД
	 * @param map об'єкт для внесення даних
	 * @return 
	 */
	protected Map<String, Object> generateNewUuid(Map<String, Object> map, Integer dbId) {
		addUuid(map);

		map.put("dbId", dbId);
		int update = db1ParamJdbcTemplate.update(sqlInsertUUID, map);
		
		Map<String, Object> dbUuid = db1ParamJdbcTemplate.queryForMap(sqlSelectUUID_byId, map);
		map.put("dbUuid", dbUuid);
		
		return dbUuid;
	}
	/**
	 * Генерує випадковий universally unique identifier (UUID), 
	 * додає його до map
	 * @param map об'єкт для довавання новоствореного UUID
	 * @return новостворений UUID
	 */
	protected UUID addUuid(Map<String, Object> map) {
		UUID uuid = UUID.randomUUID();
		map.put("uuid", uuid);
		return uuid;
	}
	/**
	 * SQL select для зчитування UUID з БД
	 */
	private @Value("${sql.selectUUI_byId}") String sqlSelectUUID_byId;
	/**
	 * SQL insert для запису UUID в БД
	 */
	private @Value("${sql.insertUUI}") String sqlInsertUUID;

	private @Value("${config.tmpFolder}")			String tmpFolder;
	protected String registry_response_file_name(String doc_id) {
		return tmpFolder+"response_"+doc_id+".json";
	}
}
