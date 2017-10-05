package org.algoritmed.mvp1.meddoc;

import java.net.URI;
import java.net.URISyntaxException;
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
import org.springframework.http.ResponseEntity;
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
	
	@PostMapping("/r/removeDataDictionary")
	public @ResponseBody Map<String, Object> removeDataDictionary(
			@RequestBody Map<String, Object> dbSaveObj
			, Principal userPrincipal) {
		logger.info("\n ---- " + "/r/removeDataDictionary"
				+ "\n" + dbSaveObj 
				);
		removeDocElement(dbSaveObj);
		addList(dbSaveObj);
		return dbSaveObj;
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
		addList(dbSaveObj);
		return dbSaveObj;
	}
	private void addList(Map<String, Object> dbSaveObj) {
		List<Map<String, Object>> protocolDatadictionaryIcd10 = db1ParamJdbcTemplate.queryForList(sqlMeddocProtocolDatadictionaryIcd10, dbSaveObj);
		dbSaveObj.put("protocolDatadictionaryIcd10", protocolDatadictionaryIcd10);
		logger.info("\n"
				+ "addList"
				+ "\n"+dbSaveObj
				+ "\n"+dbSaveObj.get("protocolDatadictionaryIcd10"));
	}

	private @Value("${sql.meddoc.protocol.name.update}") String sqlMeddocProtocolNameUpdate;
	@PostMapping("/r/saveProtocol")
	public @ResponseBody Map<String, Object> saveProtocol(
			@RequestBody Map<String, Object> dbSaveObj1
			, Principal userPrincipal) {
		HashMap2 dbSaveObj2 = new HashMap2(dbSaveObj1);
		logger.info("\n --1--111-- " + "/r/saveProtocol"
				+ "\n" + dbSaveObj2 
				+ "\n" + dbSaveObj2.contains("dbUuid.uuid_dbid") 
				);
		Map<String, Object> map = new HashMap<String, Object>();
		Integer dbId ;
		if(dbSaveObj2.contains("dbUuid.uuid_dbid")){//update
			dbId = dbSaveObj2.getInteger("dbUuid.uuid_dbid");
			System.err.println("--1----------------- " +dbId);
			map.put("dbId", dbId);
			setShortName(dbSaveObj2, dbId, map);
			protocolToString(dbSaveObj2, map);
			logger.info("\n ----3-- " + "/r/saveProtocol"
					+ "\n" + map 
					+ "\n" + dbSaveObj2 
					);
			int update = db1ParamJdbcTemplate.update(sqlMeddocProtocolNameUpdate, map);
			String sqlMeddocDocbodyUpdate = env.getProperty("sql.meddoc.docbody.update");
			int update2 = db1ParamJdbcTemplate.update(sqlMeddocDocbodyUpdate, map);
		}else{//insert
			dbId = nextDbId();
			Map<String, Object> dbUuid = generateNewUuid(map, dbId);
			setShortName(dbSaveObj2, dbId, map);
			if(dbSaveObj2.notContainsOrStringIsEmpty("name"))
				dbSaveObj2.put("name", "Long name: " + dbSaveObj2.getString("title.shortName"));
			dbSaveObj2.put("dbUuid", dbUuid);
			protocolToString(dbSaveObj2, map);
			logger.info("\n ----2-- " + "/r/saveProtocol"
					+ "\n" + map 
					+ "\n" + dbSaveObj2 
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
		String protocolAsString = objectToString(dbSaveObj);
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

	@GetMapping(value = "/r/meddoc/icpc2CodeExtention/{icpc2Code}")
	public @ResponseBody Map<String, Object> icpc2CodeExtention(@PathVariable String icpc2Code) {
		Map<String, Object> map = seekMap(icpc2Code);
		map.put("url", "/r/meddoc/icpc2CodeExtention/"+icpc2Code);
		map.put("icpc2", icpc2Code);
		logger.info("\n"
				+ "/r/meddoc/icpc2CodeExtention/{icpc2Code}"
				+ "\n" + map
				);
		List<Map<String, Object>> icd10 = db1ParamJdbcTemplate.queryForList(sqlIcpc2CodeIcd10, map);
		map.put("icd10", icd10);
		List<Map<String, Object>> consider = db1ParamJdbcTemplate.queryForList(sqlIcpc2CodeConsider, map);
		map.put("consider", consider);
		List<Map<String, Object>> inclusion = db1ParamJdbcTemplate.queryForList(sqlIcpc2CodeInclusion, map);
		map.put("inclusion", inclusion);
		List<Map<String, Object>> exclusion = db1ParamJdbcTemplate.queryForList(sqlIcpc2CodeExclusion, map);
		map.put("exclusion", exclusion);
		System.err.println(map);
		return map;
	}

	@GetMapping(value = "/r/meddoc/icd10InIcpc2/{icd10Code}")
	public @ResponseBody Map<String, Object> icpc2CodeIcd10Code(@PathVariable String icd10Code) {
		Map<String, Object> map = seekMap(icd10Code);
		map.put("url", "/r/meddoc/icpc2CodeIcd10Code/"+icd10Code);
		map.put("icd10Code", icd10Code);
//		String icd_code = icd10Code.split(".")[0];
		String icd_code = icd10Code;
		map.put("icd_code", icd_code+"%");
		logger.info("\n"
				+ "/r/meddoc/icpc2CodeIcd10Code/{icd10Code}"
				+ "\n" + map
				);
		List<Map<String, Object>> icd10InIcpc2 = db1ParamJdbcTemplate.queryForList(sqlIcd10InIcpc2, map);
		map.put("icd10InIcpc2", icd10InIcpc2);
		System.err.println(map);
		return map;
	}

	private @Value("${sql.icpc2Code.icd10}") String sqlIcpc2CodeIcd10;
	private @Value("${sql.icd10InIcpc2}") String sqlIcd10InIcpc2;
	private @Value("${sql.icpc2Code.consider}") String sqlIcpc2CodeConsider;
	private @Value("${sql.icpc2Code.inclusion}") String sqlIcpc2CodeInclusion;
	private @Value("${sql.icpc2Code.exclusion}") String sqlIcpc2CodeExclusion;
	private @Value("${sql.meddoc.icpc2Code.limit}") String sqlMeddocIcpc2CodeLimit;
	private @Value("${sql.meddoc.icpc2Code.count}") String sqlMeddocIcpc2CodeCount;

	@GetMapping(value = "/r/meddoc/icpc2Code/{seekStr}")
	public @ResponseBody Map<String, Object> seekIcpc2(@PathVariable String seekStr) {
		seekStr = "%"+seekStr+"%";
		Map<String, Object> map = seekMap(seekStr);
		logger.info("\n"
				+ "/r/meddoc/icpc2Code/{seekIcd}"
				+ "\n" + map
				+ "\n" + sqlMeddocIcpc2CodeLimit.replace(":seekStr", "'"+seekStr+"'")
				+ "\n" + sqlMeddocIcpc2CodeCount.replace(":seekStr", "'"+seekStr+"'")
				);
		Integer countAll = db1ParamJdbcTemplate.queryForObject(sqlMeddocIcdCodeCount, map, Integer.class);
		map.put("countAll", countAll);
		List<Map<String, Object>> meddocIcpc2CodeLimit = db1ParamJdbcTemplate.queryForList(sqlMeddocIcpc2CodeLimit, map);
		map.put("meddocIcpc2CodeLimit", meddocIcpc2CodeLimit);
		int size = meddocIcpc2CodeLimit.size();
		map.put("count", size);
		return map;
	}

	int limit = 20;
	@GetMapping(value = "/r/meddoc/icdCode/{seekStr}")
	public @ResponseBody Map<String, Object> seekIcd10(@PathVariable String seekStr) {
		seekStr = "%"+seekStr+"%";
		Map<String, Object> map = seekMap(seekStr);
		logger.info("\n"
				+ "/r/meddoc/icdCode/{seekIcd}"
				+ "\n" + map
				+ "\n" + sqlMeddocIcdCodeLimit.replace(":seekIcd", "'"+seekStr+"'")
				+ "\n" + sqlMeddocIcdCodeCount.replace(":seekIcd", "'"+seekStr+"'")
				+ "\n" + sqlMeddocIcdCodeP1.replace(":seekIcd", "'"+seekStr+"'")
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
	private Map<String, Object> seekMap(String seekStr) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("limit", limit );
		map.put("seekStr", seekStr);
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

	@GetMapping(value = "/r/meddoc/icpc2util")
	public @ResponseBody ResponseEntity<String> icpc2util() throws URISyntaxException {
//		public @ResponseBody Map<String, Object> icpc2util() {
		Map<String, Object> map = new HashMap<>();
		map.put("util", "icpc2");
		StringBuffer stringBuffer = icpc2consider(map);
		return ResponseEntity.created(new URI("/r/meddoc/icpc2util")).body(stringBuffer.toString());
//		return map;
	}

	private StringBuffer icpc2consider(Map<String, Object> map) {
		StringBuffer stringBuffer = new StringBuffer();
		String sql = "SELECT * FROM demo_icpc2_ua WHERE length(consider)";
		List<Map<String, Object>> icpc2List = db1JdbcTemplate.queryForList(sql);
		for (Map<String, Object> map2 : icpc2List) {
//			System.err.println(map2);
			String code = (String) map2.get("code");
			String consider = (String) map2.get("consider");
			System.err.println("-- "+consider);
			String[] split = consider.split(";");
			for (String string : split) {
				String trim = string.trim();
				String[] split2 = trim.split(" ");
				String toCode = split2[split2.length-1];
				String consider2 = trim.replace(toCode, "").trim();
				String sql2 = "insert into icpc2consider (icpc2_code, icpc2_code_consider, consider) values ('"
						+ code
						+ "','"
						+ toCode
						+ "','"
						+ consider2
						+ "');";
				stringBuffer.append(sql2).append(System.getProperty("line.separator"));
				System.err.println(sql2);
			}
			
		}
		return stringBuffer;
	}
	private StringBuffer icpc2inclusion(Map<String, Object> map) {
		StringBuffer stringBuffer = new StringBuffer();
		String sql = "select * from demo_icpc2_ua";
		List<Map<String, Object>> icpc2List = db1JdbcTemplate.queryForList(sql);
		map.put("icpc2List_size", icpc2List.size());
//		stringBuffer.append("-- hello").append(System.getProperty("line.separator"));
		for (Map<String, Object> map2 : icpc2List) {
			String code = (String) map2.get("code");
			String inclusion = (String) map2.get("inclusion");
			String[] split = inclusion.split(";");
			for (String string : split) {
				String trim = string.trim();
				if(trim.length()>0){
					String string2 = "insert into icpc2inclusion (icpc2_code, inclusion) values ('"
							+ code
							+ "','"
							+ trim
							+ "');";
					stringBuffer.append(string2).append(System.getProperty("line.separator"));
//					System.err.println(string2);
				}
			}
		}
		return stringBuffer;
	}

}
