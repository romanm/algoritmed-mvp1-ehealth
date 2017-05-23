package org.algoritmed.mvp1;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DbAlgoritmed {

	protected void docbodyToMap(Map<String, Object> map2) {
		String docbody = (String) map2.get("docbody");
		if(docbody!=null){
			Map<String, Object> docbodyMap = stringToMap(docbody);
			map2.put("docbody", docbodyMap);
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
		int numberOfDeletedRows = db1ParamJdbcTemplate.update(sql_doc_delete, dbSaveObj);
		dbSaveObj.put("numberOfDeletedRows", numberOfDeletedRows);
	}
	/**
	 * Запис в абстрактного вузла в "системі документ" БД.
	 * Базісний елемент системи. 
	 * @param dbSaveObj
	 * @param parentId
	 */
	protected void insertDocElement(Map<String, Object> dbSaveObj, Integer parentId) {
		dbSaveObj.put("parent_id", parentId);
		if(!dbSaveObj.containsKey("created")){
			Timestamp created = now();
			dbSaveObj.put("created", created);
		}
		System.err.println(dbSaveObj);
		System.err.println(52);
		System.err.println(sql_doc_insert
				.replace(":"+"doc_id", ""+dbSaveObj.get("doc_id"))
				.replace(":"+"doctype", ""+dbSaveObj.get("doctype"))
				.replace(":"+"parent_id", ""+dbSaveObj.get("parent_id"))
				);
		int update = db1ParamJdbcTemplate.update(sql_doc_insert, dbSaveObj);
		int update2 = db1ParamJdbcTemplate.update(sql_doctimestamp_insert, dbSaveObj);
	}
	private @Value("${sql.doctimestamp.insert}")	String sql_doctimestamp_insert;
	private @Value("${sql.doc.insert}")				String sql_doc_insert;
	protected void insertDocElementWithDocbody(Map<String, Object> dbSaveObj, Integer parentId
			, Map<String, Object> docbodyMap) {
		insertDocElementWithDocbody(dbSaveObj, parentId);
		dbSaveObj.put("docbody_id", dbSaveObj.get("doc_id"));
		String docbody = objectToString(docbodyMap);
		dbSaveObj.put("docbody", docbody);
		Timestamp created = (Timestamp) dbSaveObj.get("created");
		dbSaveObj.put("updated", created);
		System.err.println(dbSaveObj);
		updateDocbody(dbSaveObj);
	}
	private @Value("${sql.docbody.insertEmpty}")	String sql_docbody_insertEmpty;
	protected void insertDocElementWithDocbody(Map<String, Object> dbSaveObj, Integer parentId) {
		insertDocElement(dbSaveObj, parentId);
		int update3 = db1ParamJdbcTemplate.update(sql_docbody_insertEmpty, dbSaveObj);
	}
	protected @Value("${sql.docbody.update}")		String sql_docbody_update;
	protected @Value("${sql.doctimestamp.update}")	String sql_doctimestamp_update;
	protected void updateDocbody(Map<String, Object> dbSaveObj) {
		int update = db1ParamJdbcTemplate.update(sql_docbody_update, dbSaveObj);
		int update2 = db1ParamJdbcTemplate.update(sql_doctimestamp_update, dbSaveObj);
	}
	
	private @Value("${sql.doc.byId}")				String sql_doc_byId;
	protected Map<String, Object> readDocElement(Map<String, Object> dbSaveObj) {
		Map<String, Object> docElement = db1ParamJdbcTemplate.queryForMap(sql_doc_byId, dbSaveObj);
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

	protected @Autowired JdbcTemplate db1JdbcTemplate;
	protected @Autowired NamedParameterJdbcTemplate db1ParamJdbcTemplate;

}
