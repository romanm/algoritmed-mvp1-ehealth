package org.algoritmed.mvp1;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DbAlgoritmed {
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
		int update = db1ParamJdbcTemplate.update(sqlDocInsert, dbSaveObj);
		int update2 = db1ParamJdbcTemplate.update(sqlDoctimestampInsert, dbSaveObj);
	}
	private @Value("${sql.doctimestamp.insert}")	String sqlDoctimestampInsert;
	private @Value("${sql.doc.insert}")				String sqlDocInsert;
	@Autowired
	protected NamedParameterJdbcTemplate db1ParamJdbcTemplate;
	
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
	@Autowired
	protected JdbcTemplate db1JdbcTemplate;

	/**
	 * SQL select - повертає наступний ID единий для всієй БД.
	 */
	private @Value("${sql.nextdbid}") String sqlNextDbId;
	/**
	 * Генератор наступного ID единого для всієї БД.
	 * @return Наступний ID единий для всієй БД.
	 */
	protected Integer nextDbId() {
		Integer nextDbId = db1JdbcTemplate.queryForObject(sqlNextDbId, Integer.class);
		return nextDbId;
	}

}
