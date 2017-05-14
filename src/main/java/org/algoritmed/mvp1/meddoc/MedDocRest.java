package org.algoritmed.mvp1.meddoc;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.algoritmed.mvp1.DbAlgoritmed;
import org.algoritmed.mvp1.util.HashMap2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MedDocRest extends DbAlgoritmed{
	private static final Logger logger = LoggerFactory.getLogger(MedDocRest.class);
	@Autowired
	protected NamedParameterJdbcTemplate db1ParamJdbcTemplate;
	
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
	
	private @Value("${sql.meddoc.protocoldd2icd10.insert}") String sqlMeddocProtocoldd2icd10Insert;
	private @Value("${sql.meddoc.protocol.datadictionary.icd10}") String sqlMeddocProtocolDatadictionaryIcd10;
	@PostMapping("/r/addDataDictionary")
	public @ResponseBody Map<String, Object> addDataDictionary(
			@RequestBody Map<String, Object> dbSaveObj
			, Principal userPrincipal) {
		if(dbSaveObj.containsKey("childs"))
			dbSaveObj.remove("childs");
		logger.info("\n ---- " + "/r/addDataDictionary"
				+ "\n" + dbSaveObj 
				);
		Integer parentId = (Integer) dbSaveObj.get("protocolId");
		dbSaveObj.put("parent_id", parentId);
		if(dbSaveObj.containsKey("icd_id")){
			//if icd10
			
			//insert to doc parentId = protocolId, doctype = 7-protocol.datadictionary.icd10
			//insert to doctimestamp
			//insert to Protocoldd2icd10
			Integer dbId = nextDbId();
			dbSaveObj.put("nextDbId", dbId);
			dbSaveObj.put("doc_id", dbId);
			dbSaveObj.put("doctype", 7);
			insertDocElement(dbSaveObj, parentId);
			int update = db1ParamJdbcTemplate.update(sqlMeddocProtocoldd2icd10Insert, dbSaveObj);
		}
		List<Map<String, Object>> protocolDatadictionaryIcd10 = db1ParamJdbcTemplate.queryForList(sqlMeddocProtocolDatadictionaryIcd10, dbSaveObj);
		dbSaveObj.put("protocolDatadictionaryIcd10", protocolDatadictionaryIcd10);
		return dbSaveObj;
	}

	private @Value("${sql.meddoc.protocol.name.update}") String sqlMeddocProtocolNameUpdate;
	private @Value("${sql.meddoc.docbody.update}") String sqlMeddocDocbodyUpdate;
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
			int update = db1ParamJdbcTemplate.update(sqlMeddocProtocolNameUpdate, map);
			int update2 = db1ParamJdbcTemplate.update(sqlMeddocDocbodyUpdate, map);
		}else{//insert
			dbId = nextDbId();
			Map<String, Object> dbUuid = generateNewUuid(map, dbId);
			setShortName(dbSaveObj, dbId, map);
			if(dbSaveObj.notContainsOrStringIsEmpty("name"))
				dbSaveObj.put("name", "Long name: " + dbSaveObj.getString("title.shortName"));
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
		protocolAsString = objectToString(dbSaveObj);
		map.put("doc", protocolAsString);
	}
	
	private void setShortName(HashMap2 dbSaveObj, Integer nextDbId, Map<String, Object> map) {
		if(dbSaveObj.notContainsOrStringIsEmpty("title.shortName"))
			dbSaveObj.getMap("title").put("shortName", "Protocol " + nextDbId);
		//Значення поля name таблиці protocol
		//table protocol field name value
		map.put("name", dbSaveObj.getString("title.shortName"));
	}

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
		readValue = stringToMap(protocolDoc);
		return readValue;
	}

	private @Value("${sql.meddoc.openIcPc2SubGroup.en}") String sqlMeddocOpenIcPc2SubGroupEn;
	private @Value("${sql.meddoc.openIcPc2SubGroup}") String sqlMeddocOpenIcPc2SubGroup;
	private @Value("${sql.meddoc.demo_icpc2_ua.exclusion}") String sqlMeddocDemoIcpc2UaExclusion;
	private @Value("${sql.meddoc.icpc2icd10.code}") String sqlMeddocIcpc2icd10Code;
	private @Value("${sql.meddoc.protocol.select}") String sqlMeddocProtocolSelect;
	private @Value("${sql.meddoc.icd}") String sqlMeddocIcd;
	private @Value("${sql.meddoc.icdCode.limit}") String sqlMeddocIcdCodeLimit;
	private @Value("${sql.meddoc.icdCode.count}") String sqlMeddocIcdCodeCount;
	private @Value("${sql.meddoc.icdCodeP1}") String sqlMeddocIcdCodeP1;
	private @Value("${sql.meddoc.icdChildren}") String sqlMeddocIcdChildren;
	
	@GetMapping(value = "/r/meddoc/icdChildren/{parentId}")
	public @ResponseBody Map<String, Object> dbMeddocIcdChildren(@PathVariable Integer parentId) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("parentId", parentId);
		logger.info("\n"
				+ "/r/meddoc/icdChildren/{parentId}"
				+ "\n" + map
				);
		List<Map<String, Object>> icdChildren = db1ParamJdbcTemplate.queryForList(sqlMeddocIcdChildren, map);
		map.put("icdChildren", icdChildren);
		return map;
	}

	@GetMapping(value = "/r/meddoc/icdCode/{seekIcd}")
	public @ResponseBody Map<String, Object> dbMeddocIcdCode(@PathVariable String seekIcd) {
		Map<String, Object> map = new HashMap<String, Object>();
		seekIcd = "%"+seekIcd+"%";
		int limit = 20;
		map.put("limit", limit );
		map.put("seekIcd", seekIcd);
		logger.info("\n"
				+ "/r/meddoc/icdCode/{seekIcd}"
				+ "\n" + map
				+ "\n" + sqlMeddocIcdCodeLimit.replace(":seekIcd", "'"+seekIcd+"'")
				+ "\n" + sqlMeddocIcdCodeCount.replace(":seekIcd", "'"+seekIcd+"'")
				+ "\n" + sqlMeddocIcdCodeP1.replace(":seekIcd", "'"+seekIcd+"'")
				);
		Integer countAll = db1ParamJdbcTemplate.queryForObject(sqlMeddocIcdCodeCount, map, Integer.class);
		map.put("countAll", countAll);
		List<Map<String, Object>> meddocIcdCodeLimit = db1ParamJdbcTemplate.queryForList(sqlMeddocIcdCodeLimit, map);
		map.put("meddocIcdCodeLimit", meddocIcdCodeLimit);
		int size = meddocIcdCodeLimit.size();
		map.put("count", size);
		if(size>0){
			List<Map<String, Object>> meddocIcdCodeP1 = db1ParamJdbcTemplate.queryForList(sqlMeddocIcdCodeP1, map);
			int sizeP1 = meddocIcdCodeP1.size();
			if(sizeP1>0){
				map.put("meddocIcdCodeP1", meddocIcdCodeP1);
				map.put("countP1", sizeP1);
				for (int i = 0; i < sizeP1; i++) {
					Map<String, Object> p1Item = meddocIcdCodeP1.get(i);
				}
			}
		}
		return map;
	}

	@GetMapping(value = "/r/meddoc/icd")
	public @ResponseBody Map<String, Object> dbMeddocIcd() {
		Map<String, Object> map = new HashMap<String, Object>();
		logger.info("\n"
				+ "/r/meddoc/icd"
				+ "\n" + map
				);
//		List<Map<String, Object>> dbMeddocIcd = db1JdbcTemplate.queryForList(sqlMeddocIcd);
//		map.put("dbMeddocIcd", dbMeddocIcd);
		Map<String, Object> mapChilds
			, map3 = new HashMap<>();
		ArrayList<Map<String, Object>> listPath = new ArrayList<>();
		listPath.add(null);
		listPath.add(null);
		String sql = "SELECT * FROM icd "
				+ "WHERE icd_root = :icd_root "
				+ "AND icd_level > 1"
				+ "ORDER BY icd_left_key";
		List<Map<String, Object>> dbMeddocIcdL1 = db1JdbcTemplate.queryForList("SELECT * FROM icd WHERE icd_id=icd_root");
		map.put("icd", dbMeddocIcdL1);
		int i = 0;
		for (Map<String, Object> map2 : dbMeddocIcdL1) {
			listPath.set(1, map2);
			Integer icd_root = (Integer) map2.get("icd_root");
			if(icd_root<=100000){
				map3.put("icd_root", icd_root);
				List<Map<String, Object>> l = db1ParamJdbcTemplate.queryForList(sql, map3);
				for (Map<String, Object> map4 : l){
					addMapChilds(listPath, map4);
					i++;
//					System.err.print(i);
//					System.err.println(map4);
					if(i>200000)
						break;
				}
			}
		}
		logger.info("\n"
				+ "/r/meddoc/icd"
//				+ "\n" + map
				);
		return map;
	}
	private void addMapChilds(List<Map<String, Object>> listPath, Map<String, Object> mapIcdItem) {
		Integer icd_level = (Integer) mapIcdItem.get("icd_level");
//		System.err.print("listPath" + "="+listPath.size());
//		System.err.println(listPath);
//		System.err.print("icd_level=");
//		System.err.println(icd_level);
//		System.err.print("mapIcdItem" + "=");
//		System.err.println(mapIcdItem);
		if(icd_level>1){
			Map<String, Object> map = listPath.get(icd_level-1);
			if(!map.containsKey("childs"))
				map.put("childs", new ArrayList<List<Map<String, Object>>>());
			List<Map<String, Object>> listChilds = (List<Map<String, Object>>) map.get("childs");
			listChilds.add(mapIcdItem);
		}
		if(icd_level>=listPath.size())
			listPath.add(mapIcdItem);
		else
			listPath.set(icd_level, mapIcdItem);
	}
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
