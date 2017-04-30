package org.algoritmed.mvp1.meddoc;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.algoritmed.mvp1.util.HashMap2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class MedDocRest {
	private static final Logger logger = LoggerFactory.getLogger(MedDocRest.class);
	@Autowired
	protected NamedParameterJdbcTemplate db1ParamJdbcTemplate;
	@Autowired
	protected JdbcTemplate db1JdbcTemplate;
	
	/**
	 * SQL insert для запису UUID в БД
	 */
	private @Value("${sql.insertUUI}") String sqlInsertUUID;
	/**
	 * SQL select для зчитування UUID з БД
	 */
	private @Value("${sql.selectUUI_byId}") String sqlSelectUUID_byId;
	/**
	 * Створює новий UUID в БД
	 * @param map об'єкт для внесення даних
	 * @return 
	 */
	protected Map<String, Object> generateNewUuid(Map<String, Object> map, Integer nextDbId) {
		addUuid(map);
		map.put("nextDbId", nextDbId);
		map.put("dbId", nextDbId);
		
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

	@Autowired	ObjectMapper objectMapper;

	@PostMapping("/r/saveProtocol")
	public @ResponseBody Map<String, Object> saveProtocol(
			@RequestBody Map<String, Object> dbSaveObj1
			, Principal userPrincipal) {
		HashMap2 dbSaveObj = new HashMap2(dbSaveObj1);
		logger.info("\n --1--111-- " + "/r/saveProtocol"
				+ "\n" + dbSaveObj 
				+ "\n" + dbSaveObj.contains("dbUuid.uuid_dbid") 
				);
		Map<String, Object> map = new HashMap<String, Object>();
		Integer dbId ;
		if(dbSaveObj.contains("dbUuid.uuid_dbid")){//update
			dbId = dbSaveObj.getInteger("dbUuid.uuid_dbid");
			System.err.println("--1----------------- " +dbId);
			map.put("dbId", dbId);
			setShortName(dbSaveObj, dbId, map);
			protocolToString(dbSaveObj, map);
			logger.info("\n ----3-- " + "/r/saveProtocol"
					+ "\n" + map 
					+ "\n" + dbSaveObj 
					);
			int update = db1ParamJdbcTemplate.update(sqlMeddocProtocolUpdate, map);
		}else{//insert
			dbId = nextDbId();
			Map<String, Object> dbUuid = generateNewUuid(map, dbId);
			setShortName(dbSaveObj, dbId, map);
			if(dbSaveObj.notContainsOrStringIsEmpty("name"))
				dbSaveObj.put("name", "Long name: " + dbSaveObj.getString("shortName"));
			dbSaveObj.put("dbUuid", dbUuid);
			protocolToString(dbSaveObj, map);
			logger.info("\n ----2-- " + "/r/saveProtocol"
					+ "\n" + map 
					+ "\n" + dbSaveObj 
					);
			int update = db1ParamJdbcTemplate.update(sqlMeddocProtocolInsert, map);
		}
		Map<String, Object> protocol = dbProtocol(dbId);
//		map.put("protocol", protocol);

		logger.info("\n ----3-- " + "/r/saveProtocol"
				+ "\n" + map 
				+ "\n" + protocol 
				);

		return protocol;
//		return dbSaveObj;
	}
	private void protocolToString(HashMap2 dbSaveObj, Map<String, Object> map) {
		String protocolAsString = "{}";
		try {
			protocolAsString = objectMapper.writeValueAsString(dbSaveObj);
		} catch (JsonProcessingException e1) {
			e1.printStackTrace();
		}
		map.put("doc", protocolAsString);
	}
	private void setShortName(HashMap2 dbSaveObj, Integer nextDbId, Map<String, Object> map) {
		if(dbSaveObj.notContainsOrStringIsEmpty("shortName"))
			dbSaveObj.put("shortName", "Protocol " + nextDbId);
		map.put("name", dbSaveObj.getString("shortName"));
	}

	private @Value("${sql.meddoc.protocol.update}") String sqlMeddocProtocolUpdate;
	private @Value("${sql.meddoc.protocol.insert}") String sqlMeddocProtocolInsert;
	private @Value("${sql.meddoc.protocol.byId}") String sqlMeddocProtocolById;
	@GetMapping(value = "/r/meddoc/dbProtocol/{dbId}")
	public @ResponseBody Map<String, Object> dbProtocol(@PathVariable Integer dbId) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("dbId", dbId);
		logger.info("\n"
				+ "/r/meddoc/dbProtocol/{dbid}"
				+ "\n" + map
				);
		String protocolDoc = db1ParamJdbcTemplate.queryForObject(sqlMeddocProtocolById, map, String.class);
		Map<String, Object> readValue = map;
		try {
			readValue = objectMapper.readValue(protocolDoc, Map.class);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return readValue;
	}

	private @Value("${sql.meddoc.openIcPc2SubGroup.en}") String sqlMeddocOpenIcPc2SubGroupEn;
	private @Value("${sql.meddoc.openIcPc2SubGroup}") String sqlMeddocOpenIcPc2SubGroup;
	private @Value("${sql.meddoc.demo_icpc2_ua.exclusion}") String sqlMeddocDemoIcpc2UaExclusion;
	private @Value("${sql.meddoc.icpc2icd10.code}") String sqlMeddocIcpc2icd10Code;
	private @Value("${sql.meddoc.protocol.select}") String sqlMeddocProtocolSelect;
	
	@GetMapping(value = "/r/meddoc/dbProtocolListe")
	public @ResponseBody Map<String, Object> dbProtocolListe() {
		Map<String, Object> map = new HashMap<String, Object>();
		logger.info("\n"
				+ "/r/meddoc/dbProtocolListe"
				+ "\n" + map
				);
		List<Map<String, Object>> dbProtocolListe = db1JdbcTemplate.queryForList(sqlMeddocProtocolSelect);
		map.put("dbProtocolListe", dbProtocolListe);
		return map;
	}

	@GetMapping(value = "/r/meddoc/openIcPc2SubGroup/{code}")
	public @ResponseBody Map<String, Object> openIcPc2SubGroup(@PathVariable String code) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("code", code);
		logger.info("---------------\n"
				+ "/r/meddoc/openIcPc2SubGroup/{code}"
				+ "\n" + map
				+ "\n" + sqlMeddocDemoIcpc2UaExclusion.replace(":code", code)
				);
		List<Map<String, Object>> meddocIcpc2icd10Code = db1ParamJdbcTemplate.queryForList(sqlMeddocIcpc2icd10Code, map);
		map.put("meddocIcpc2icd10Code", meddocIcpc2icd10Code);
		List<Map<String, Object>> demoIcpc2UaExclusion = db1ParamJdbcTemplate.queryForList(sqlMeddocDemoIcpc2UaExclusion, map);
		map.put("demoIcpc2UaExclusion", demoIcpc2UaExclusion);
		Map<String, Object> openIcPc2SubGroup = db1ParamJdbcTemplate.queryForMap(sqlMeddocOpenIcPc2SubGroup, map);
		map.put("openIcPc2SubGroup", openIcPc2SubGroup);
//		Map<String, Object> openIcPc2SubGroupEn = db1ParamJdbcTemplate.queryForMap(sqlMeddocOpenIcPc2SubGroupEn, map);
//		map.put("openIcPc2SubGroupEn", openIcPc2SubGroupEn);
		return map;
	}

	String ALPHABET = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
	int ID_LENGTH = 4;

	@GetMapping(value = "/r/meddoc/amGenerateID")
	public @ResponseBody List<String> amGenerateID() {
		ArrayList<String> list = new ArrayList<>();
		for (int j = 0; j < 10; j++) {
			String amGenerateID = "";
			for (int i = 0; i < ID_LENGTH; i++) {
				int round = (int) Math.round(Math.floor(Math.random() * ALPHABET.length()));
				amGenerateID += ALPHABET.charAt(round);
			}
			if(!list.contains(amGenerateID))
				list.add(amGenerateID);
		}
		return list;
	}
}
